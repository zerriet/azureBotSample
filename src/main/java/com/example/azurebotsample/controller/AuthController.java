package com.example.azurebotsample.controller;

import com.example.azurebotsample.model.AuthRequest;
import com.example.azurebotsample.model.AuthResponse;
import com.example.azurebotsample.service.AssistantService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AssistantService assistantService;

    public AuthController(AssistantService assistantService) {
        this.assistantService = assistantService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public AuthResponse health() {
        return new AuthResponse("ENDPOINT_HEALTHY", null, null);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public AuthResponse login(@RequestBody AuthRequest request) {
        return assistantService.createAssistantAndThread(request.getKey());
    }
}