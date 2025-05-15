package com.example.azurebotsample.controller;

import com.example.azurebotsample.model.AuthResponse;
import com.example.azurebotsample.service.AssistantService;
import com.example.azurebotsample.model.SpeechRequest;
import com.example.azurebotsample.service.OpenAIAssistantService;
import com.example.azurebotsample.service.SpeechClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.Map;
import java.util.Base64;

@RestController
@Slf4j
@RequestMapping("/v1")
@Tag(name = "URL sending the message to the assistant")
public class AssistantSpeechController {

    @Autowired
    private SpeechClient speechClient;

    @Autowired
    private OpenAIAssistantService assistantService;

    @Autowired
    private AssistantService assistantServiceSdk;

    @PostMapping("/ask-and-speak")

    public ResponseEntity<?> askAndSpeak(@RequestBody Map<String, String> request) {
        String userInput = request.get("text");

        try {
            String assistantMessage = assistantService.getAssistantReply(userInput);
            assistantMessage = cleanFormatting(assistantMessage);
            byte[] audioBytes = speechClient.generateResponse(assistantMessage);
            String base64Audio = Base64.getEncoder().encodeToString(audioBytes);
            SpeechRequest response = new SpeechRequest(assistantMessage, base64Audio);
            // System.out.println(response);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error occurred while processing request", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("text", "Sorry, something went wrong."));
        }
    }


    public static String cleanFormatting(String input) {
        // Remove Markdown headings like ###, ##, #
        input = input.replaceAll("^#+\\s*", "").trim();
        // Remove bold, *italic*, underline, etc.
        return input.replaceAll("(\\*\\*|__|\\*|_)", "").trim();
    }

}