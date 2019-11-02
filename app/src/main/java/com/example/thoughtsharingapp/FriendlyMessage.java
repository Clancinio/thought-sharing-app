package com.example.thoughtsharingapp;

public class FriendlyMessage {
    private String text;
    private String userId;

    public FriendlyMessage() {
    }

    public FriendlyMessage(String text, String username) {
        this.text = text;
        this.userId = username;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}