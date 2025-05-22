// package com.example.azurebotsample.controller;

// import com.example.azurebotsample.model.ChatRequest;
// import com.example.azurebotsample.model.ChatResponse;
// import com.example.azurebotsample.service.AssistantService;
// import com.example.azurebotsample.service.AzureChatAPIService;
// import com.example.azurebotsample.service.SpeechClient;
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.MediaType;
// import org.springframework.web.bind.annotation.*;
// import io.swagger.v3.oas.annotations.tags.Tag;
// import java.util.Base64;
// import org.json.JSONObject;
// import org.json.JSONException;

// // @RestController
// // @RequestMapping("/api/chat")
// // @Slf4j
// // public class ChatController {

// //     private final AssistantService assistantService;
// //     private final SpeechClient speechClient;

// //     public ChatController(AssistantService assistantService, SpeechClient speechClient) {
// //         this.assistantService = assistantService;
// //         this.speechClient = speechClient;
// //     }

// //     @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
// //     @Tag(name = "SDK Sending the message to the assistant")
// //     public ChatResponse chat(@RequestBody ChatRequest request) {
// //         try {
// //             String assistantId = request.getAssistantId();
// //             String threadId = request.getThreadId();
// //             String message = request.getMessage();

// //             log.info("Received message: {}", message);
// //             log.info("Assistant ID: {}, Thread ID: {}", assistantId, threadId);

// //             if (message == null || message.trim().isEmpty()) {
// //                 return new ChatResponse("Please enter a message before submitting.");
// //             }

// //             String reply = assistantService.chat(assistantId, threadId, message);

// //             // Generate audio
// //             byte[] audio = speechClient.generateResponse(reply);
// //             String base64Audio = Base64.getEncoder().encodeToString(audio);

// //             return new ChatResponse(reply, base64Audio);

// //         } catch (Exception e) {
// //             log.error("Error during chat interaction", e);
// //             return new ChatResponse("Something went wrong while talking to Aria.");
// //         }
// //     }
// // }

// // ChatController for Azure Chat API
// @RestController
// @RequestMapping("/api")        
// @Slf4j
// @Tag(name = "Azure Chat API")
// public class ChatController {

//     // Injecting the service that interacts with Azure OpenAI
//     private final AzureChatAPIService openAIService;
//     // Speech client for generating audio responses
//     private final SpeechClient speechClient;

//     @Autowired
//     public ChatController(AzureChatAPIService openAIService) {
//         this.openAIService = openAIService;
//         this.speechClient = new SpeechClient();
//     }

//     // Endpoint for handling POST requests
//     @PostMapping("/chat")
//     public ChatResponse getChatResponse(@RequestBody ChatRequest request) {
//         // Get the message from the request and call the service layer to get the assistant's reply
//         String assistantReply = openAIService.getChatResponse(request.getMessage());
//         // 2. Extract only the "content" field from the reply
//         String content = extractContentFromReply(assistantReply);

//         // 3. Generate audio using just the content
//         byte[] audio = speechClient.generateResponse(content);

//         // 4. Encode audio to base64
//         String base64Audio = Base64.getEncoder().encodeToString(audio);
        
//         log.info("ChatAPI's reply: {}", assistantReply);
//         return new ChatResponse(assistantReply, base64Audio);  // Return the assistant's response
//     }

//     // Helper method to extract the "content" field from the assistant's reply
//     private String extractContentFromReply(String assistantReply) {
//     try {
//         JSONObject json = new JSONObject(assistantReply);
//         return json.getJSONArray("choices")
//                    .getJSONObject(0)
//                    .getJSONObject("message")
//                    .getString("content");
//     } catch (JSONException e) {
//         log.error("Failed to extract content", e);
//         return "";
//     }
// }

// }