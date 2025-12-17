package com.funo.app;

public class ChatModel {
    private String message;
    private String senderId;
    private long timestamp;
    private String senderUsername;
    private String senderAvatarUrl;

    public ChatModel() {
        // Default constructor required for calls to DataSnapshot.getValue(ChatModel.class)
    }

    public ChatModel(String message, String senderId, long timestamp, String senderUsername, String senderAvatarUrl) {
        this.message = message;
        this.senderId = senderId;
        this.timestamp = timestamp;
        this.senderUsername = senderUsername;
        this.senderAvatarUrl = senderAvatarUrl;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getSenderUsername() {
        return senderUsername;
    }

    public void setSenderUsername(String senderUsername) {
        this.senderUsername = senderUsername;
    }

    public String getSenderAvatarUrl() {
        return senderAvatarUrl;
    }

    public void setSenderAvatarUrl(String senderAvatarUrl) {
        this.senderAvatarUrl = senderAvatarUrl;
    }
}