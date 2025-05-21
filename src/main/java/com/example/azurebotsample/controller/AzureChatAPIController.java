package com.example.azurebotsample.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import com.example.azurebotsample.service.AzureChatAPIService;
import org.springframework.web.bind.annotation.RequestBody;

public class AzureChatAPIController {
    
    private final AzureChatAPIService azureChatAPIService;

    @Autowired
    public AzureChatAPIController(AzureChatAPIService azureChatAPIService) {
        this.azureChatAPIService = azureChatAPIService;
    }

    @PostMapping
    public String getChatResponse(@RequestBody String userInput) {
        return azureChatAPIService.getChatResponse(userInput);
    }
}
