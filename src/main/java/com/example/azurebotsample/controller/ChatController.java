package com.example.azurebotsample.controller;

import com.example.azurebotsample.model.ChatRequest;
import com.example.azurebotsample.model.ChatResponse;
import com.example.azurebotsample.service.AssistantService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final AssistantService assistantService;

    public ChatController(AssistantService assistantService) {
        this.assistantService = assistantService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ChatResponse chat(@RequestBody ChatRequest request) throws InterruptedException {
        String reply = assistantService.chat(
                request.getAssistantId(),
                request.getThreadId(),
                request.getMessage());
        return new ChatResponse(reply);
    }
}