// package com.example.azurebotsample.config;

// import com.azure.ai.openai.assistants.AssistantsClient;
// import com.azure.ai.openai.assistants.AssistantsClientBuilder;
// import com.azure.core.credential.AzureKeyCredential;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;

// @Configuration
// public class AzureOpenAIConfig {

//     @Value("${azure.openai.endpoint}")
//     private String endpoint;

//     @Value("${azure.openai.api-key}")
//     private String apiKey;

//     @Bean
//     public AssistantsClient assistantsClient() {
//         return new AssistantsClientBuilder()
//                 .endpoint(endpoint)
//                 .credential(new AzureKeyCredential(apiKey))
//                 .buildClient();
//     }
// }
