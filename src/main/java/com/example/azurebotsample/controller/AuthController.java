package com.example.azurebotsample.controller;

import com.example.azurebotsample.model.AuthRequest;
import com.example.azurebotsample.model.AuthResponse;
import com.example.azurebotsample.service.AssistantService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AssistantService assistantService;

    public AuthController(AssistantService assistantService) {
        this.assistantService = assistantService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Tag(name = "Check if the endpoint is healthy")
    public AuthResponse health() {
        return new AuthResponse("ENDPOINT_HEALTHY", null, null);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Tag(name = "Get back the thread ID and assistant ID")
    
    public AuthResponse login(@RequestBody AuthRequest request) {
        return assistantService.createAssistantAndThread(request.getKey());
    }
}