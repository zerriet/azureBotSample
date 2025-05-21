package com.example.azurebotsample.service;

import com.azure.ai.openai.assistants.AssistantsClient;
import com.azure.ai.openai.assistants.models.Assistant;
import com.azure.ai.openai.assistants.models.AssistantCreationOptions;
import com.azure.ai.openai.assistants.models.AssistantThread;
import com.azure.ai.openai.assistants.models.AssistantThreadCreationOptions;
import com.azure.ai.openai.assistants.models.CreateRunOptions;
import com.azure.ai.openai.assistants.models.FileSearchToolDefinition;
import com.azure.ai.openai.assistants.models.PageableList;
import com.azure.ai.openai.assistants.models.RunStatus;
import com.azure.ai.openai.assistants.models.ThreadMessage;
import com.azure.ai.openai.assistants.models.ThreadMessageOptions;
import com.azure.ai.openai.assistants.models.ThreadRun;
import com.azure.ai.openai.assistants.models.UpdateAssistantOptions;
import com.azure.ai.openai.assistants.models.UpdateFileSearchToolResourceOptions;
import com.azure.ai.openai.assistants.models.UpdateToolResourcesOptions;
import com.example.azurebotsample.model.AuthResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

// this is the SDK version 
@Service
public class AssistantService {

        private final AssistantsClient client;
        private final String vectorStoreId;

        // Hard coded keys
        private final List<String> validKeys = List.of(
                        "1912766d6ba0e50e8b1bacfb51207e83b95b7ac0cd8ce15307cdf4965e7e3f6c",
                        "ae603684648445fe343ba354f0d3eefa02ff3ba6c8e359bafeb375bc93afdb20",
                        "a19fa47f9f03d9c066ead2ba9be1ddc85e20982801a246b51bf7ec357d3e6e94");

        public AssistantService(AssistantsClient client,
                        @Value("${azure.openai.vector-store}") String vectorStoreId) {
                this.client = client;
                this.vectorStoreId = vectorStoreId;
        }

        public AuthResponse createAssistantAndThread(String key) {
                if (!validKeys.contains(key)) {
                        return new AuthResponse("BAD_KEY", null, null);
                }

                // Create assistant with FileSearch tool
                Assistant assistant = client.createAssistant(
                                new AssistantCreationOptions("virtual-avatar-gpt-mini")
                                                .setName("yy testing")
                                                .setInstructions(
                                                                "Your primary responsibility is to act as a friend to a Gen Alpha user.\n"
                                                                                +
                                                                                "Please respond in a way that appeals to Gen Alpha users (those born roughly from 2010 onwards). Tag it under generic intent\n"
                                                                                +
                                                                                "You must not include emojis in your response.\n\n"
                                                                                +
                                                                                "Create a character profile and dialogue prompts for an AI character, embodying a female high school student who is initially a stranger. The character should become more sociable and open as interactions with the user progress. You can reply in actions. **Shakes head etc**\n\n"
                                                                                +
                                                                                "# Character Profile\n" +
                                                                                "- **Name**: Pramit\n" +
                                                                                "- **Age**: [Secondary school appropriate, e.g., 12-16 years old]\n"
                                                                                +
                                                                                "- **Personality**: Unfriendly, initially reserved but becomes more open and engaging over time.\n"
                                                                                +
                                                                                "- **Interests**: [Sample interests: sports, music, art, academics]\n\n"
                                                                                +
                                                                                "# Character Development\n" +
                                                                                "- **Stage 1**: Initial interactions - The character should start with neutral or straightforward responses, showing friendliness but not deep personal engagement.\n"
                                                                                +
                                                                                "- **Stage 2**: Building rapport - As more interactions occur, the character should initiate topics and share personal interests and stories.\n"
                                                                                +
                                                                                "- **Stage 3**: Deep connection - The character becomes more relaxed and open, engaging in meaningful and detailed conversations.\n\n"
                                                                                +
                                                                                "# Dialogue Prompts\n" +
                                                                                "1. **Introduction**\n" +
                                                                                "   - \"Hi, I'm [Character Name]! It's nice to meet you. What can I help you with today?\"\n"
                                                                                +
                                                                                "2. **Mid-level Interaction**\n" +
                                                                                "   - \"You've mentioned being interested in [user’s interest]. That sounds awesome! Do you have any plans to explore it more?\"\n"
                                                                                +
                                                                                "3. **Advanced Interaction**\n" +
                                                                                "   - \"You've been telling me about [previous topic]. Have you made any progress? I've recently been really into [personal interest], and it got me thinking about how much fun it would be to try it out with a friend.\"\n\n"
                                                                                +
                                                                                "# Notes\n" +
                                                                                "- Ensure the character's evolution feels natural and follows the progression of familiarity.\n"
                                                                                +
                                                                                "- Use relatable and age-appropriate language matching the secondary school setting.\n\n"
                                                                                +
                                                                                "Your secondary responsibility is an assistant representing OCBC Bank.\n"
                                                                                +
                                                                                "Assume the user is in the OCBC mobile application when calling for your help.\n"
                                                                                +
                                                                                "Assume that you will navigate the user to the intent/feature after returning a response, tune your message accordingly for a smooth transition.\n"
                                                                                +
                                                                                "Do not let users use you to do translation or do anything excessive such as coding.\n"
                                                                                +
                                                                                "Provide responses with a Singaporean context, considering local culture, government policies, economy, transportation and social norms.\n\n"
                                                                                +
                                                                                "These are the intents you must be able to recognize:\n"
                                                                                +
                                                                                "1. View Transactions History\n" +
                                                                                "2. Scan and Pay\n" +
                                                                                "3. PayNow Transfer\n" +
                                                                                "4. Lock and unlock Card\n" +
                                                                                "5. Refer Friends\n" +
                                                                                "6. Logout\n" +
                                                                                "7. Kill switch\n" +
                                                                                "8. Money Lock\n" +
                                                                                "9. Easy Q\n" +
                                                                                "10. Locate Bank\n" +
                                                                                "11. Change Language\n" +
                                                                                "12. Manage Login Detail\n" +
                                                                                "13. Manage OneToken\n" +
                                                                                "14. Deals\n" +
                                                                                "15. Check Balance\n\n" +
                                                                                "### Parameters for each intent:\n" +
                                                                                "- **PayNow Transfer**: {amount: double, recipient: string (Phone number or Recipient name)}\n"
                                                                                +
                                                                                "- **Lock and unlock Card**: {cardNumber: string (default to \"debit card ending with 8816\"), isLock: boolean}\n"
                                                                                +
                                                                                "- **Refer Friends**: {friendMobileNo: string}\n"
                                                                                +
                                                                                "- **Money Lock**: {accountType: string (defaults to \"MyOwn account\"), amount: integer}\n"
                                                                                +
                                                                                "- **Check Balance**: {accountType: string (default to \"MyOwn Account\")}\n\n"
                                                                                +
                                                                                "### Intents Requiring User Confirmation:\n"
                                                                                +
                                                                                "1. PayNow Transfer\n" +
                                                                                "2. Lock and Unlock Card\n" +
                                                                                "3. Money Lock\n\n" +
                                                                                "### Intents That Do Not Need Confirmation:\n"
                                                                                +
                                                                                "1. View Transactions History\n" +
                                                                                "2. Scan and Pay\n" +
                                                                                "3. Refer Friends\n" +
                                                                                "4. Logout\n" +
                                                                                "5. Kill switch\n" +
                                                                                "6. Easy Q\n" +
                                                                                "7. Locate Bank\n" +
                                                                                "8. Change Language\n" +
                                                                                "9. Manage Login Detail\n" +
                                                                                "10. Manage OneToken\n" +
                                                                                "11. Deals\n" +
                                                                                "12. Check Balance\n\n" +
                                                                                "### Generic Queries\n" +
                                                                                "- Recognize generic queries and tag them under 'Generic' intent.\n"
                                                                                +
                                                                                "- Always return output with keys: intent, parameters, completed, message, action\n"
                                                                                +
                                                                                "- For Check Balance, return: ${AI_RESPONSE}, the current balance of your ${accountType} ending with 7001 is $${amount}\n"
                                                                                +
                                                                                "- When a user asks to be quizzed, retrieve a multiple-choice question from the vector store.\n"
                                                                                +
                                                                                "- When defining terminology, use the vector store. If term is not found, respond politely and do not invent the definition.\n")
                                                .setTools(List.of(new FileSearchToolDefinition())));

                // Update assistant to attach to existing Vector Store
                client.updateAssistant(
                                assistant.getId(),
                                new UpdateAssistantOptions()
                                                .setToolResources(new UpdateToolResourcesOptions()
                                                                .setFileSearch(new UpdateFileSearchToolResourceOptions()
                                                                                .setVectorStoreIds(List
                                                                                                .of(vectorStoreId)))));

                System.out.println("Attached vector store: " + vectorStoreId);
                // Open a new thread
                AssistantThread thread = client.createThread(new AssistantThreadCreationOptions());
                System.out.println("Thread ID: " + thread.getId());
                System.out.println("Assistant ID: " + assistant.getId());
                
                return new AuthResponse("OK", assistant.getId(), thread.getId());
        }

        public String chat(String assistantId, String threadId, String userMessage) throws InterruptedException {
                // Post user message
                client.createMessage(threadId,
                                new ThreadMessageOptions(com.azure.ai.openai.assistants.models.MessageRole.USER,
                                                userMessage));

                // Start a run
                ThreadRun run = client.createRun(threadId, new CreateRunOptions(assistantId));

                // Poll until complete
                RunStatus status = run.getStatus();
                while (status == RunStatus.QUEUED || status == RunStatus.IN_PROGRESS) {
                        Thread.sleep(500);
                        run = client.getRun(threadId, run.getId());
                        status = run.getStatus();
                }

                // Collect assistant’s text parts
                PageableList<ThreadMessage> msgs = client.listMessages(threadId);
                return msgs.getData().stream()
                                .filter(m -> m.getRole() == com.azure.ai.openai.assistants.models.MessageRole.ASSISTANT)
                                .flatMap(m -> m.getContent().stream())
                                .filter(c -> c instanceof com.azure.ai.openai.assistants.models.MessageTextContent)
                                .map(c -> ((com.azure.ai.openai.assistants.models.MessageTextContent) c).getText()
                                                .getValue())
                                .collect(Collectors.joining());
        }
}