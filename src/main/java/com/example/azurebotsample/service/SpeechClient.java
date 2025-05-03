package com.example.azurebotsample.service;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@NoArgsConstructor
public class SpeechClient {
    private String speechSubscriptionKey = "placedholder";
    private String resourceRegion = "southeastasia";
    private String endpointUrl = "https://southeastasia.api.cognitive.microsoft.com/";
}
