package com.example.azurebotsample.controller;

import com.example.azurebotsample.model.ChatRequest;
import com.example.azurebotsample.model.ChatResponse;
import com.example.azurebotsample.service.AssistantService;
import com.example.azurebotsample.service.SpeechClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.Base64;

@RestController
@RequestMapping("/api/chat")
@Slf4j
public class ChatController {

    private final AssistantService assistantService;
    private final SpeechClient speechClient;

    public ChatController(AssistantService assistantService, SpeechClient speechClient) {
        this.assistantService = assistantService;
        this.speechClient = speechClient;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Tag(name = "SDK Sending the message to the assistant")
    public ChatResponse chat(@RequestBody ChatRequest request) {
        try {
            String assistantId = request.getAssistantId();
            String threadId = request.getThreadId();
            String message = request.getMessage();

            log.info("Received message: {}", message);
            log.info("Assistant ID: {}, Thread ID: {}", assistantId, threadId);

            if (message == null || message.trim().isEmpty()) {
                return new ChatResponse("Please enter a message before submitting.");
            }

            String reply = assistantService.chat(assistantId, threadId, message);

            // Generate audio
            byte[] audio = speechClient.generateResponse(reply);
            String base64Audio = Base64.getEncoder().encodeToString(audio);

            return new ChatResponse(reply, base64Audio);

        } catch (Exception e) {
            log.error("Error during chat interaction", e);
            return new ChatResponse("Something went wrong while talking to Aria.");
        }
    }
}