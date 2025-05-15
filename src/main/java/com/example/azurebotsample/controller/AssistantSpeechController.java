package com.example.azurebotsample.controller;

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

import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/v1")
public class AssistantSpeechController {

    @Autowired
    private SpeechClient speechClient;

    @Autowired
    private OpenAIAssistantService assistantService;

    @PostMapping("/ask-and-speak")
    // public ResponseEntity<?> askAndSpeak(@RequestBody Map<String, String>
    // request) {
    // String userInput = request.get("text");

    // try {
    // // 1. Get assistant reply
    // String assistantMessage = assistantService.getAssistantReply(userInput);

    // // 2. Convert to speech (audio in raw format)
    // byte[] audioBytes = speechClient.generateResponse(assistantMessage);

    // // 3. Return JSON with base64 or raw audio
    // HttpHeaders headers = new HttpHeaders();
    // headers.setContentType(MediaType.MULTIPART_FORM_DATA);

    // MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
    // body.add("text", assistantMessage);
    // body.add("audio", new ByteArrayResource(audioBytes) {
    // @Override
    // public String getFilename() {
    // return "response.wav"; // Filename for the audio response
    // }
    // });

    // return new ResponseEntity<>(body, headers, HttpStatus.OK);

    // } catch (Exception e) {
    // log.error("Error occurred while processing request", e);
    // return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: "
    // + e.getMessage());
    // }
    // }
    public ResponseEntity<?> askAndSpeak(@RequestBody Map<String, String> request) {
        String userInput = request.get("text");

        try {
            // Only get the assistant's reply
            String assistantMessage = assistantService.getAssistantReply(userInput);

            // Return as plain JSON
            return ResponseEntity.ok(Map.of("text", assistantMessage));
        } catch (Exception e) {
            log.error("Error occurred while processing request", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("text", "Sorry, something went wrong."));
        }
    }
}