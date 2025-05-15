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

@Service
public class AssistantService {

        private final AssistantsClient client;
        private final String vectorStoreId;

        // your three hard-coded keys
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

                // 1) create assistant with FileSearch tool only
                Assistant assistant = client.createAssistant(
                                new AssistantCreationOptions("virtual-avatar-gpt-mini")
                                                .setName("yy testing")
                                                .setInstructions(
                                                                // (paste everything from your JS `options.instructions`
                                                                // here verbatim)
                                                                "Your primary responsibility is to act as a friend to a Gen Alpha user.\n"
                                                                                +
                                                                                "Please respond in a way that appeals to Gen Alpha users (those born roughly from 2010 onwards). Tag it under generic intent\n"
                                                                                +
                                                                                "You must not include emojis in your response.\n"
                                                                                +
                                                                                "…\n" +
                                                                                "When a user asks for definitions … do not elaborate on your own.\n")
                                                .setTools(List.of(new FileSearchToolDefinition())));

                // 2) update assistant to attach your existing Vector Store
                client.updateAssistant(
                                assistant.getId(),
                                new UpdateAssistantOptions()
                                                .setToolResources(new UpdateToolResourcesOptions()
                                                                .setFileSearch(new UpdateFileSearchToolResourceOptions()
                                                                                .setVectorStoreIds(List
                                                                                                .of(vectorStoreId)))));

                // 3) open a new thread
                AssistantThread thread = client.createThread(new AssistantThreadCreationOptions());

                return new AuthResponse("OK", assistant.getId(), thread.getId());
        }

        public String chat(String assistantId, String threadId, String userMessage) throws InterruptedException {
                // 1) post user message
                client.createMessage(threadId,
                                new ThreadMessageOptions(com.azure.ai.openai.assistants.models.MessageRole.USER,
                                                userMessage));

                // 2) start a run
                ThreadRun run = client.createRun(threadId, new CreateRunOptions(assistantId));

                // 3) poll until complete
                RunStatus status = run.getStatus();
                while (status == RunStatus.QUEUED || status == RunStatus.IN_PROGRESS) {
                        Thread.sleep(500);
                        run = client.getRun(threadId, run.getId());
                        status = run.getStatus();
                }

                // 4) collect assistant’s text parts
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