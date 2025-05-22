package com.example.azurebotsample.config;

import com.azure.ai.openai.OpenAIClient;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.core.credential.KeyCredential;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAIClientConfig {

    @Value("${azure.openai.endpoint_sdk}")
    private String endpointSDK;

    @Value("${azure.openai.api-key}")
    private String apiKey;

    @Bean
    public OpenAIClient openAIClient() {
        return new OpenAIClientBuilder()
            .endpoint(endpointSDK)
            .credential(new KeyCredential(apiKey))  // This will now compile
            .buildClient();
    }

}
