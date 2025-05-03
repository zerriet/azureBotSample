package com.example.azurebotsample.controller;

import com.example.azurebotsample.model.BaseResponse;
import com.example.azurebotsample.service.SpeechClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public BaseResponse getSpeech() {return BaseResponse.builder().build();}
}
