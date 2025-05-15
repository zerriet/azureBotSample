package com.example.azurebotsample.controller;

import com.example.azurebotsample.model.BaseResponse;
import com.example.azurebotsample.model.SpeechRequest;
import com.example.azurebotsample.model.SpeechRequestPayload;
import com.example.azurebotsample.service.SpeechClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.azurebotsample.controller.AssistantSpeechController;

import java.util.Arrays;
import java.util.Base64;

@RestController
@Slf4j
@RequestMapping("/v1")
public class SpeechController {
    @Autowired
    private SpeechClient speechClient;

    /**
     * API call for azure TTS resource
     */
    @PostMapping(value = "get-speech", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SpeechRequest> getSpeech(@RequestBody SpeechRequestPayload payload) {
        try {
            // System.out.println("Request text :" + payload.getSpeechText());
            String rawText = payload.getSpeechText();
            String cleanText = AssistantSpeechController.cleanFormatting(rawText); // basically cleaning up the unlikely
                                                                                   // inputs of # and special characters
                                                                                   // by the user
            byte[] audioBytes = speechClient.generateResponse(cleanText);
            String base64Audio = Base64.getEncoder().encodeToString(audioBytes);
            SpeechRequest response = new SpeechRequest(payload.getSpeechText(), base64Audio);
            // System.out.println(response);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
