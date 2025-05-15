package com.example.azurebotsample.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service // For this, we are using URL method rather than the SDK method
public class OpenAIAssistantService { // Declares the class as a Spring service

    // Injects the OpenAI API key from application properties
    @Value("${openai.api.key}")
    private String apiKey;

    // Injects the OpenAI API endpoint from application properties
    @Value("${openai.api.endpoint}")
    private String endpoint;

    // Injects the OpenAI API version from application properties
    @Value("${openai.api.version}")
    private String apiVersion;

    // Injects the vector store ID from application properties
    @Value("${openai.vector_store}")
    private String vectorStoreId;

    // Creates an OkHttpClient instance for making HTTP requests
    private final OkHttpClient client = new OkHttpClient();
    // Creates an ObjectMapper instance for JSON processing
    private final ObjectMapper mapper = new ObjectMapper();

    // Method to get the assistant's reply based on user input
    public String getAssistantReply(String userMessage) throws Exception {
        // Step 1: Create Assistant
        String assistantPayload = String.format(
                """
                        {
                            "model": "virtual-avatar-gpt-mini",
                            "name": "Aria Assistant",
                            "instructions": "Your primary responsibility is to act as a friend to a Gen Alpha user.\\nPlease respond in a way that appeals to Gen Alpha users (those born roughly from 2010 onwards). Tag it under generic intent\\nYou must not include emojis in your response.\\nCreate a character profile and dialogue prompts for an AI character, embodying a female high school student who is initially a stranger. The character should become more sociable and open as interactions with the user progress. You can reply in actions. **Shakes head etc**\\n# Character Profile\\n- Name: Aria\\n- Age: 12-16 years old\\n- Personality: Unfriendly, initially reserved but becomes more open and engaging over time.\\n- Interests: sports, music, art, academics\\n# Character Development\\n- Stage 1: Initial interactions - The character should start with neutral or straightforward responses, showing friendliness but not deep personal engagement.\\n- Stage 2: Building rapport - As more interactions occur, the character should initiate topics and share personal interests and stories.\\n- Stage 3: Deep connection - The character becomes more relaxed and open, engaging in meaningful and detailed conversations.\\n# Dialogue Prompts\\n1. Introduction - \\\"Hi, I'm Aria! It's nice to meet you. What can I help you with today?\\\"\\n2. Mid-level Interaction - \\\"You've mentioned being interested in [user’s interest]. That sounds awesome! Do you have any plans to explore it more?\\\"\\n3. Advanced Interaction - \\\"You've been telling me about [previous topic]. Have you made any progress? I've recently been really into [personal interest], and it got me thinking about how much fun it would be to try it out with a friend.\\\"\\n# Notes\\n- Ensure the character's evolution feels natural.\\n- Use relatable and age-appropriate language.\\nYour secondary responsibility is an assistant representing OCBC Bank.\\nAssume the user is in the OCBC mobile application when calling for your help.\\nNavigate them to features smoothly.\\nNo translations, no coding.\\nProvide Singapore context (culture, economy, transport).\\nIntents: View Transactions History, Scan and Pay, PayNow Transfer, Lock and unlock Card, Refer Friends, Logout, Kill switch, Money Lock, Easy Q, Locate Bank, Change Language, Manage Login Detail, Manage OneToken, Deals, Check Balance.\\nInclude parameters, completed flag, action (Confirmation, Listing, null), and return message.\\nWhen asked to quiz, retrieve question from vector store with 4 options and correct one.\\n",
                            "tools": [{"type": "file_search"}],
                            "tool_resources": {
                                "file_search": {
                                    "vector_store_ids": ["%s"]
                                }
                            },
                            "temperature": 1.0,
                            "top_p": 1.0
                        }
                        """,
                vectorStoreId);

        // Create the request to create an assistant
        Request assistantRequest = new Request.Builder()
                .url(endpoint + "/openai/assistants?api-version=" + apiVersion) // dont change this
                .addHeader("api-key", apiKey)
                .addHeader("Content-Type", "application/json")
                .post(RequestBody.create(assistantPayload, MediaType.parse("application/json")))
                .build();

        // Execute the request and parse the response
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
                .url(endpoint + "/openai/threads/" + threadId + "/messages?api-version=" + apiVersion) // dont change
                                                                                                       // this
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
                    .url(endpoint + "/openai/threads/" + threadId + "/runs/" + runId + "?api-version=" + apiVersion) // dont
                                                                                                                     // change
                                                                                                                     // this
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
                .url(endpoint + "/openai/threads/" + threadId + "/messages?api-version=" + apiVersion) // dont change
                                                                                                       // this
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
