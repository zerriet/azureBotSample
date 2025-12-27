package com.example.azurebotsample.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.example.azurebotsample.model.ChatMemory;
import com.example.azurebotsample.model.Message;
import com.azure.ai.openai.OpenAIClient;
import com.azure.ai.openai.models.ChatRequestUserMessage;
import com.azure.ai.openai.models.ChatRequestAssistantMessage;
import com.azure.ai.openai.models.ChatRequestMessage;
import com.azure.ai.openai.models.ChatRequestSystemMessage;
import com.azure.ai.openai.models.ChatRole;
import com.azure.ai.openai.models.ChatCompletionsOptions;
import com.azure.ai.openai.models.ChatCompletions;
import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
public class AzureChatAPIService {

    // For Azure OpenAI SDK method 
    private final OpenAIClient openAIClient;
    
    private final ChatMemory chatMemory = new ChatMemory(); 

    private final RestTemplate restTemplate;
    
    @Value("${azure.openai.api-key}")
    private String apiKey;

    @Value("${azure.openai.endpoint}")
    private String endpoint; 

    @Value("${azure.openai.endpoint-sdk}")
    private String endPointSDK; 

    @Value("${azure.openai.deployment-name}")
    private String deploymentName;


    public AzureChatAPIService(RestTemplate restTemplate, OpenAIClient openAIClient) {
        this.restTemplate = restTemplate;
        this.openAIClient = openAIClient; 
    }

    //  SDK-based method
    public String getChatResponseWithSDK(String userInput) {
        log.info("Using SDK method for chat response...");
        // One-time purge of corrupted memory
        
        chatMemory.addUserMessage(userInput);
        List<Message> recentMessages = chatMemory.getLast3UserMessagesWithContext();

        List<com.azure.ai.openai.models.ChatRequestMessage> sdkMessages = new ArrayList<>();
        sdkMessages.add(new com.azure.ai.openai.models.ChatRequestSystemMessage("You are a helpful assistant."));
        log.info("User input: {}", userInput);
        log.info("Recent messages: {}", recentMessages.size());

        for (Message msg : recentMessages) {
            if (msg.getContent() == null || msg.getContent().trim().isEmpty()) continue;
            System.out.println("Debugging Message: " + msg.getContent());
            System.out.println("Debugging Role: " + msg.getRole());
            if ("user".equalsIgnoreCase(msg.getRole())) {
                sdkMessages.add(new ChatRequestUserMessage(msg.getContent().trim()));
            } else if ("assistant".equalsIgnoreCase(msg.getRole())) {
                sdkMessages.add(new ChatRequestAssistantMessage(msg.getContent().trim()));
            }
        }
        System.out.println("Debugging SDK Messages: " + sdkMessages);
        ChatCompletionsOptions options = new ChatCompletionsOptions(sdkMessages)
            .setMaxTokens(150)
            .setTemperature(1.0);
        ChatCompletions response = openAIClient.getChatCompletions(deploymentName, options);
        String reply = response.getChoices().get(0).getMessage().getContent().toString(); 
        chatMemory.addAssistantMessage(reply);
        return reply;
    }


    // Helper method to escape JSON strings
    private String escapeJson(String text) {
        return text.replace("\"", "\\\"").replace("\n", "\\n");
    }

    // Method to build the JSON based on the latest 3 user messages 
    private String buildMessagesJson(List<Message> messages) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        sb.append("{\"role\": \"system\", \"content\": \"You are a helpful assistant.\"}");

        for (Message msg : messages) {
            sb.append(", ");
            sb.append("{\"role\": \"").append(msg.getRole()).append("\", ");
            sb.append("\"content\": \"").append(escapeJson(msg.getContent())).append("\"}");
        }

        sb.append("]");
        return sb.toString();
    }

    public String getChatResponse(String userInput) {
        // Add the user message to the chat memory 
        chatMemory.addUserMessage(userInput); 

        // Get the last 3 user messages with context 
        List<Message> last3UserMessages = chatMemory.getLast3UserMessagesWithContext();
        
        String url = endpoint;

        // Build OpenAI-compatible message array JSON
        String messagesJson = buildMessagesJson(last3UserMessages);

        // Construct the request body for the API call
        String requestBody = "{\n" +
        "  \"model\": \"gpt-4o-mini\",\n" +
        "  \"messages\": " + messagesJson + ",\n" +
        "  \"max_tokens\": 150,\n" +
        "  \"temperature\": 1,\n" +
        "  \"top_p\": 1\n" +
        "}";


        // Prepare headers with API key
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("Authorization", "Bearer " + apiKey);

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        // Make the POST request
        ResponseEntity<String> response = restTemplate.exchange(
            url, HttpMethod.POST, entity, String.class
        );

        // Get the response
        return response.getBody();
    }
}
