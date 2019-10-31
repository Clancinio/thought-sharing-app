package com.example.thoughtsharingapp;

public class Feed {
    private String postText;

    public Feed() {

    }

    public Feed(String postText) {
        this.postText = postText;
    }

    public String getPostText() {
        return postText;
    }

    public void setPostText(String postText) {
        this.postText = postText;
    }
}
