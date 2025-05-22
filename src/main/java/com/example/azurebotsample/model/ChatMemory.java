package com.example.azurebotsample.model;

import java.util.ArrayList; 
import java.util.List;
import java.util.Base64;
import org.apache.commons.codec.binary.Base64;


public class ChatMemory {
    
    private final List<Message> history = new ArrayList<>();

    public void addUserMessage(String content) {
    if (Base64.isBase64(content)) {
        System.out.println("❌ Skipping user message (looks like Base64): " + content);
        return;
    }
    history.add(new Message("user", content));
    trimToLast3UserMessagesWithContext();
}

public void addAssistantMessage(String content) {
    if (Base64.isBase64(content)) {
        System.out.println("❌ Skipping assistant message (looks like Base64): " + content);
        return;
    }
    history.add(new Message("assistant", content));
}


    public List<Message> getLast3UserMessagesWithContext() {
        return new ArrayList<>(history);
    }

    private void trimToLast3UserMessagesWithContext() {
        int userSeen = 0;
        List<Message> trimmed = new ArrayList<>();

        // Traverse backwards and keep at most 3 user messages + any assistant replies
        for (int i = history.size() - 1; i >= 0; i--) {
            Message msg = history.get(i);
            if ("user".equals(msg.getRole())) {
                userSeen++;
            }
            trimmed.add(0, msg);
            if (userSeen == 3) break;
        }

        // Replace current history with trimmed list
        history.clear();
        history.addAll(trimmed);
    }

    public void clear() {
        history.clear();
    }
}
