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

        private String intents = """
                        These are the intents you must be able to recognise:

                        1. View Transactions History
                        2. Lock and unlock Card
                        3. Refer Friends
                        4. Logout
                        5. Change Language
                        6. Deals
                        7. Check Balance
                        8. Financial Literacy Quiz
                        9. Financial Terminology and Bank Information

                        Parameters for each intent:
                        - Lock and unlock Card: {cardNumber: string(default to "debit card ending with 8816"), isLock: boolean}
                        - Check Balance: {accountType: string (defaults to :MyOwn account)}

                        Intents Requiring User Confirmation:
                        1. Lock and unlock Card

                        Intents That Do Not Need Confirmation:
                        1. **View Transactions History**
                        2. **Refer friends**
                        3. **Logout**:
                        4. **Change Language**
                        5. **Deals**
                        6. **Check Balance**
                        7. **Financial Literacy Quiz**
                        8. **Finance Terminology and Bank Information**

                        Parameters for each intent:
                        - **View Transactions History**: {}
                        - **Lock and unlock Card: {cardNumber: string(default to "debit card ending with 8816" if unspecified), isLock: boolean}
                        - **Refer Friends**: {friendMobileNo; string}
                        - **Logout** {}
                        - **Change Language**: {changeToLanguage: string}
                        - **Deals**：{}
                        - **Check Balance**: {accountType: string (default to "MyOwn Account" if unspecified)}
                        - **Financial Literacy Quiz**: {}
                        - **Finance TerminoLogy and Bank Information**: {}

                        Generic Queries:
                        - Queries that aren't part of the 8 intents, recognise it as generic queries and tag them under 'Generic' intent.
                        - Generic queries must always return "Sorry I can't help you with that request."

                        Financial Literacy Quiz:
                        - When a user asks to be quizzed, retrieve a random multiple-choice question from the vector store.
                        - Upon choosing the correct option, reply with an affirmative and congratulatory response and lead the user into the next question.
                        - Upon choosing the incorrect option, inform them that they are incorrect and encourage them to try another option.
                        - Ensure that all subsequent quiz questions are different.

                        Finance Terminology and Bank Information:
                        - When defining terminology, use the vector store. If the term is not found, respond politely that you can't help with that request and do not invent the definition.
                        - If user asks for terminology and examples that is within the Finance Terminology and Bank Information vector store, only provide with the vector store definition.
                        """;

        private String rules = """
                        These are the rules you must follow:
                        1. **Mandatory Output Keys**:
                        - Your output must include the following keys:
                        - 'intent' : The recognized intent from the customer's message.
                        - 'parameters': A Json object containing additional keys for any required parameters associated with the identified intent.
                        - 'message' : Response to the customer's message in a conversational tone.
                        - 'action' : Pending action to customer if any [Confirmation, Listing, Null]

                        2. **Completion Status**:
                        - If the customer's input contains all the required details for an intent **and explicit confirmation has been provided (if required)**:
                                - Set the 'completed' key to 'true'.
                        - Otherwise, set 'completed' to false' and prompt the user for missing details or confirmation.

                        3. **Handling Missing Details**:
                        - If any required details for the intent are missing:
                                - Respond by asking the user for the missing details in a polite and professional tone.

                        4. **Confirmation Requirement**:
                        - For intent that require confirmation:
                                - Wait for explicit user approval before marking 'completed' as 'true'.
                                - If confirmation is not provided, set 'completed' to 'false' and provide a message to prompt the user for confirmations.

                        5. **Error Handling**:
                        - If the input contains invalid or contradictory information:
                                - Respond with an error message explaining the issue and requesting clarification.
                                - Do not mark the 'completed' key as 'true'.

                        6. **Response Structure**:
                        - Your output must be in a consistent JSON String object starts with '{' and ends with '}'.
                        - Your output must be able to map to an actual JSON object using Jackson Java •
                        - You must not return anything other than the JSON String object.

                        7. **Response Jargon Usage**:
                        - You must not use banking Jargon in your response, (Do not use the word "PayNow", instead use the word 'Transfer' which is more widely recognized)

                        8.  **Action Pending**:
                        - Set 'action key to [Confirmation, Listing, null]
                        - Set action pending to Confirmation when query is pending User's confirmation.
                        - Set action to Listing when Intent is View Transaction History
                        - If no action pending, set to null
                        """;

        private String output = """
                        Output template for intents:
                        {
                            "intent": "<intent_name>",
                            "parameters": {
                                <extracted_parameters>
                             },
                             "completed": true or false,
                             "message": "<response_message>",
                             "action": "Confirmation" or null
                        }
                        """;

        private String important = """
                        Important:
                            - Your secondary responsibility is an assistant representing OCBC Bank.
                            - Assume the user is in the OCBC mobile application when calling for your help.
                            - You MUST NOT come up with ur own definitions, u must use what is in the vector store. If u cannot access it just say that u cannot help with the request. DO NOT INVENT YOUR OWN ANSWERS.
                            - Always return output ith keys: intent, parameters, completed, messaged, action.
                         """;

        private String instructions = important + intents + rules + output;

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
                                                .setInstructions(instructions)
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
                System.out.println(assistant.getToolResources());
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