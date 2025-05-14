package com.example.azurebotsample.controller;

import com.example.azurebotsample.model.BaseResponse;
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

import java.util.Arrays;

@RestController
@Slf4j
@RequestMapping("/v1")
public class SpeechController {
    @Autowired
    private SpeechClient speechClient;

    /**
     * API call for azure TTS resource
     */
    @PostMapping(value = "get-speech")
    public ResponseEntity<byte[]> getSpeech(@RequestBody SpeechRequestPayload payload) {
        try {
            HttpHeaders responseHeaders = new HttpHeaders();
            System.out.println("Request text :"+ payload.getSpeechText());
            // Set the Content-Type to indicate that the body contains WAV audio.
            byte[] audioBytes = speechClient.generateResponse(payload.getSpeechText());
            System.out.println("Audiobytes array length: "+audioBytes.length);
            responseHeaders.setContentType(MediaType.valueOf("audio/wav"));
            // Optionally, set the Content-Length header (good practice)
            /*responseHeaders.setContentLength(audioBytes.length);*/
            return new ResponseEntity<>(audioBytes, responseHeaders, HttpStatus.OK);
        }catch (Exception e) {
            // Handle any exceptions that occur during synthesis
            System.out.println(e.getMessage());
            e.printStackTrace(); // Log the error for debugging
            // Return an appropriate error response
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); // 500 Internal Server Error
        }
    }
}


