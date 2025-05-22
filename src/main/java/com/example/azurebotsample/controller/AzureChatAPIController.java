package com.example.azurebotsample.controller;

import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;

import lombok.extern.slf4j.Slf4j;
import com.example.azurebotsample.model.ChatRequest;
import com.example.azurebotsample.model.ChatResponse;
import com.example.azurebotsample.service.AzureChatAPIService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import com.example.azurebotsample.service.SpeechClient;
import java.util.Base64;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.json.JSONObject;
import org.json.JSONException;

@RestController
@RequestMapping("/api")        
@Slf4j
@Tag(name = "Azure Chat API")
public class AzureChatAPIController {
    
    private final AzureChatAPIService azureChatAPIService;

    private final SpeechClient speechClient;
    
    @Autowired
    public AzureChatAPIController(AzureChatAPIService azureChatAPIService) {
        this.azureChatAPIService = azureChatAPIService;
        this.speechClient = new SpeechClient();
    }

    @PostMapping("/chat")
    public ChatResponse getChatResponse(@RequestBody ChatRequest request) {
        String method = request.getMethod();
        String userInput = request.getMessage();
        // 🔍 Decide which backend method to use based on 'method'
        if ("sdk".equalsIgnoreCase(method)) {
            log.info("Using SDK method for chat response");
            String assistantReply = azureChatAPIService.getChatResponseWithSDK(userInput);
            
            byte[] audio = speechClient.generateResponse(assistantReply);
            String base64Audio = Base64.getEncoder().encodeToString(audio);
            return new ChatResponse(assistantReply, base64Audio);
             
        } else {
            String assistantReply = azureChatAPIService.getChatResponse(userInput);
            String content = extractContentFromReply(assistantReply);
            byte[] audio = speechClient.generateResponse(content);
            String base64Audio = Base64.getEncoder().encodeToString(audio);
            return new ChatResponse(assistantReply, base64Audio);
        }
        
    }
    // Helper method to extract the "content" field from the assistant's reply
    private String extractContentFromReply(String assistantReply) {
        try {
            JSONObject json = new JSONObject(assistantReply);
            return json.getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content");
        } catch (JSONException e) {
            log.error("Failed to extract content", e);
            return "";
        }
    }
}
