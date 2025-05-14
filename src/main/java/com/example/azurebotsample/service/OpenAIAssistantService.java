package com.example.azurebotsample.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class OpenAIAssistantService {

    @Value("${openai.api.key}")
    private String apiKey;

    @Value("${openai.api.endpoint}")
    private String endpoint;

    @Value("${openai.api.version}")
    private String apiVersion;

    @Value("${openai.assistant_id}")
    private String assistantId;

    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    public String getAssistantReply(String userMessage) throws Exception {
        // 1. Create thread
        Request threadRequest = new Request.Builder()
                .url(endpoint + "/openai/assistants/v1/threads")
                .post(RequestBody.create("", MediaType.parse("application/json")))
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("OpenAI-Beta", "assistants=v1")
                .build();

        Response threadResponse = client.newCall(threadRequest).execute();
        JsonNode threadJson = mapper.readTree(threadResponse.body().string());
        String threadId = threadJson.path("id").asText(null); // Returns null if "id" is not found
            if (threadId == null) {
                throw new RuntimeException("Thread ID not found in response: " + threadJson.toString());
            }

        // 2. Add message
        String messageBody = "{\"role\": \"user\", \"content\": \"" + userMessage + "\"}";
        Request messageRequest = new Request.Builder()
                .url(endpoint + "/openai/assistants/v1/threads/" + threadId + "/messages")
                .post(RequestBody.create(messageBody, MediaType.parse("application/json")))
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("OpenAI-Beta", "assistants=v1")
                .build();

        client.newCall(messageRequest).execute();

        // 3. Run assistant
        String runBody = "{\"assistant_id\": \"" + assistantId + "\"}";
        Request runRequest = new Request.Builder()
                .url(endpoint + "/openai/assistants/v1/threads/" + threadId + "/runs")
                .post(RequestBody.create(runBody, MediaType.parse("application/json")))
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("OpenAI-Beta", "assistants=v1")
                .build();

        Response runResponse = client.newCall(runRequest).execute();
        JsonNode runJson = mapper.readTree(runResponse.body().string());
        String runId = runJson.path("id").asText(null); // Returns null if "id" is not found
        if (runId == null) {
            throw new RuntimeException("Run ID not found in response: " + runJson.toString());
        }

        // 4. Poll for completion
        String status = "queued";
        while (status.equals("queued") || status.equals("in_progress")) {
            Thread.sleep(1000);
            Request pollRequest = new Request.Builder()
                    .url(endpoint + "/openai/assistants/v1/threads/" + threadId + "/runs/" + runId)
                    .get()
                    .addHeader("Authorization", "Bearer " + apiKey)
                    .addHeader("OpenAI-Beta", "assistants=v1")
                    .build();
            Response pollResponse = client.newCall(pollRequest).execute();
            JsonNode pollJson = mapper.readTree(pollResponse.body().string());
            status = pollJson.get("status").asText();
        }

        // 5. Get response message
        Request msgRequest = new Request.Builder()
                .url(endpoint + "/openai/assistants/v1/threads/" + threadId + "/messages")
                .get()
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("OpenAI-Beta", "assistants=v1")
                .build();

        Response msgResponse = client.newCall(msgRequest).execute();
        JsonNode msgJson = mapper.readTree(msgResponse.body().string());

        JsonNode dataArray = msgJson.get("data");
        if (dataArray == null || !dataArray.isArray()) {
            throw new RuntimeException(
                    "Missing or invalid 'data' array in message response: " + msgJson.toPrettyString());
        }

        for (JsonNode message : dataArray) {
            JsonNode roleNode = message.get("role");
            if (roleNode != null && "assistant".equals(roleNode.asText())) {
                JsonNode contentArray = message.get("content");
                if (contentArray != null && contentArray.isArray()) {
                    for (JsonNode content : contentArray) {
                        if ("text".equals(content.path("type").asText())) {
                            JsonNode textNode = content.get("text");
                            if (textNode != null && textNode.has("value")) {
                                return textNode.get("value").asText();
                            }
                        }
                    }
                }
            }
        }

        throw new RuntimeException("No valid assistant response found in: " + msgJson.toPrettyString());
    }
}