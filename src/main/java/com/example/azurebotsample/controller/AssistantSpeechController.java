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

import java.util.Map;
import java.util.Base64;

@RestController
@Slf4j
@RequestMapping("/v1")
public class AssistantSpeechController {

    @Autowired
    private SpeechClient speechClient;

    @Autowired
    private OpenAIAssistantService assistantService;

    @Autowired
    private AssistantService assistantServiceSdk;

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

    @PostMapping("/ask-and-speak-sdk")
    public ResponseEntity<?> askAndSpeakSDK(@RequestBody Map<String, String> request) {
        try {
            String userInput = request.get("text");
            String key = request.get("key");

            AuthResponse auth = assistantServiceSdk.createAssistantAndThread(key);
            if (!auth.getStatus().equals("OK")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("text", "Invalid API Key."));
            }

            String reply = assistantServiceSdk.chat(auth.getAssistantId(), auth.getThreadId(), userInput);
            return ResponseEntity.ok(Map.of("text", reply));
        } catch (Exception e) {
            log.error("SDK Error", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("text", "Error: " + e.getMessage()));
        }
    }

    public static String cleanFormatting(String input) {
        // Remove Markdown headings like ###, ##, #
        input = input.replaceAll("^#+\\s*", "").trim();
        // Remove bold, *italic*, underline, etc.
        return input.replaceAll("(\\*\\*|__|\\*|_)", "").trim();
    }

}