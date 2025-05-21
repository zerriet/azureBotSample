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
import java.util.HashMap;
import java.util.Map;


@Service
public class AzureChatAPIService {
    
    @Value("${azure.openai.api-key}")
    private String apiKey;

    @Value("${azure.openai.endpoint}")
    private String endpoint;

    private final RestTemplate restTemplate;

    public AzureChatAPIService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String getChatResponse(String userInput) {
        String url = endpoint;

        // Construct the request body for the API call
        String requestBody = "{\n" +
    "  \"model\": \"gpt-4o-mini\",\n" +
    "  \"messages\": [\n" +
    "    {\"role\": \"system\", \"content\": \"You are a helpful assistant.\"},\n" +
    "    {\"role\": \"user\", \"content\": \"" + userInput + "\"}\n" +
    "  ],\n" +
    "  \"max_tokens\": 150,\n" +   // Maximum number of tokens for the response (currently set it to 150, will trial and error later)
    "  \"temperature\": 1,\n" +      // Controls randomness in responses: 0 to 1
    "  \"top_p\": 1\n" +             // Controls diversity of responses: 0 to 1
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
