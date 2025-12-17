package com.funo.app.entities;

public class Message {
    private String senderId;
    private String senderUsername;
    private String senderAvatarUrl;
    private String text;

    public Message(String senderId, String senderUsername, String senderAvatarUrl, String text) {
        this.senderId = senderId;
        this.senderUsername = senderUsername;
        this.senderAvatarUrl = senderAvatarUrl;
        this.text = text;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getSenderUsername() {
        return senderUsername;
    }

    public String getSenderAvatarUrl() {
        return senderAvatarUrl;
    }

    public String getText() {
        return text;
    }
}