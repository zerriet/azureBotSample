package com.example.azurebotsample.controller;

import com.example.azurebotsample.model.ChatRequest;
import com.example.azurebotsample.model.ChatResponse;
import com.example.azurebotsample.service.AssistantService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
@Slf4j
public class ChatController {

    private final AssistantService assistantService;

    public ChatController(AssistantService assistantService) {
        this.assistantService = assistantService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
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
            return new ChatResponse(reply);

        } catch (Exception e) {
            log.error("Error during chat interaction", e);
            return new ChatResponse("Something went wrong while talking to Aria.");
        }
    }
}