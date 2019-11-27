package com.example.thoughtsharingapp.classes;

public class Thoughts {
    private String text;
    private String userId;

    public Thoughts() {
    }

    public Thoughts(String text, String username) {
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