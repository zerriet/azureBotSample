package com.example.azurebotsample.model;

public class ChatResponse {
    private String message;
    private String audioBase64Wav;

    public ChatResponse() {
    }

    public ChatResponse(String message) {
        this.message = message;
    }

    // ✅ Add this constructor
    public ChatResponse(String message, String audioBase64Wav) {
        this.message = message;
        this.audioBase64Wav = audioBase64Wav;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getAudioBase64Wav() {
        return audioBase64Wav;
    }

    public void setAudioBase64Wav(String audioBase64Wav) {
        this.audioBase64Wav = audioBase64Wav;
    }
}