package com.example.azurebotsample.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class OpenAIAssistantService {

    @Value("${openai.api.key}")
    private String apiKey;

    @Value("${openai.api.endpoint}")
    private String endpoint;

    @Value("${openai.api.version}")
    private String apiVersion;

    @Value("${openai.vector_store}")
    private String vectorStoreId;

    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    public String getAssistantReply(String userMessage) throws Exception {
        // Step 1: Create Assistant
        String assistantPayload = String.format("""
            {
                "model": "virtual-avatar-gpt-mini",
                "name": "Aria Assistant",
                "instructions": "You are Aria, a Gen Alpha companion...",
                "tools": [{"type": "file_search"}],
                "tool_resources": {
                    "file_search": {
                        "vector_store_ids": ["%s"]
                    }
                },
                "temperature": 1.0,
                "top_p": 1.0
            }
        """, vectorStoreId);

        Request assistantRequest = new Request.Builder()
                .url(endpoint + "/openai/assistants?api-version=" + apiVersion) // dont change this
                .addHeader("api-key", apiKey)
                .addHeader("Content-Type", "application/json")
                .post(RequestBody.create(assistantPayload, MediaType.parse("application/json")))
                .build();

        Response assistantResponse = client.newCall(assistantRequest).execute();
        JsonNode assistantJson = mapper.readTree(assistantResponse.body().string());
        String assistantId = assistantJson.path("id").asText();

        System.out.println("Assistant created: " + assistantId);

        // Step 2: Create Thread
        Request threadRequest = new Request.Builder()
                .url(endpoint + "/openai/threads?api-version=" + apiVersion)
                .addHeader("api-key", apiKey)
                .addHeader("Content-Type", "application/json")
                .post(RequestBody.create("{}", MediaType.parse("application/json")))
                .build();

        Response threadResponse = client.newCall(threadRequest).execute();
        JsonNode threadJson = mapper.readTree(threadResponse.body().string());
        String threadId = threadJson.path("id").asText();

        System.out.println("Thread created: " + threadId);

        // Step 3: Add user message to thread
        String messageBody = String.format("""
            {
                "role": "user",
                "content": "%s"
            }
        """, userMessage);

        Request messageRequest = new Request.Builder()
                .url(endpoint + "/openai/threads/" + threadId + "/messages?api-version=" + apiVersion) // dont change this
                .addHeader("api-key", apiKey)
                .addHeader("Content-Type", "application/json")
                .post(RequestBody.create(messageBody, MediaType.parse("application/json")))
                .build();

        client.newCall(messageRequest).execute();

        // Step 4: Run the assistant
        String runBody = String.format("""
            {
                "assistant_id": "%s"
            }
        """, assistantId);

        Request runRequest = new Request.Builder()
                .url(endpoint + "/openai/threads/" + threadId + "/runs?api-version=" + apiVersion) // dont change this
                .addHeader("api-key", apiKey)
                .addHeader("Content-Type", "application/json")
                .post(RequestBody.create(runBody, MediaType.parse("application/json")))
                .build();

        Response runResponse = client.newCall(runRequest).execute();
        JsonNode runJson = mapper.readTree(runResponse.body().string());
        String runId = runJson.path("id").asText();

        System.out.println("Run started: " + runId);

        // Step 5: Poll until run completes
        String status;
        do {
            Thread.sleep(1000);
            Request pollRequest = new Request.Builder()
                    .url(endpoint + "/openai/threads/" + threadId + "/runs/" + runId + "?api-version=" + apiVersion) // dont change this
                    .addHeader("api-key", apiKey)
                    .get()
                    .build();
            Response pollResponse = client.newCall(pollRequest).execute();
            JsonNode pollJson = mapper.readTree(pollResponse.body().string());
            status = pollJson.path("status").asText();
            System.out.println("Run status: " + status);
        } while (status.equals("queued") || status.equals("in_progress"));

        // Step 6: Get final assistant response
        Request msgRequest = new Request.Builder()
                .url(endpoint + "/openai/threads/" + threadId + "/messages?api-version=" + apiVersion) // dont change this
                .addHeader("api-key", apiKey)
                .get()
                .build();

        Response msgResponse = client.newCall(msgRequest).execute();
        JsonNode msgJson = mapper.readTree(msgResponse.body().string());
        JsonNode dataArray = msgJson.path("data");

        for (JsonNode message : dataArray) {
            if ("assistant".equals(message.path("role").asText())) {
                for (JsonNode content : message.path("content")) {
                    if ("text".equals(content.path("type").asText())) {
                        return content.path("text").path("value").asText();
                    }
                }
            }
        }

        throw new RuntimeException("No assistant reply found.");
    }
}