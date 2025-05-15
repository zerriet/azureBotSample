package com.example.azurebotsample.model;

public class AuthResponse {
    private String message;
    private String assistantId;
    private String threadId;

    public AuthResponse() {
    }

    public AuthResponse(String message, String assistantId, String threadId) {
        this.message = message;
        this.assistantId = assistantId;
        this.threadId = threadId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getAssistantId() {
        return assistantId;
    }
  

    public void setAssistantId(String assistantId) {
        this.assistantId = assistantId;
    }

    public String getThreadId() {
        return threadId;
    }

    public void setThreadId(String threadId) {
        this.threadId = threadId;
    }

    public String getStatus() {
        return "OK";
    }
}