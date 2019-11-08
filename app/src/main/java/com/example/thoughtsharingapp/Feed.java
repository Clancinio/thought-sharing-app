package com.example.thoughtsharingapp;

import java.io.Serializable;

public class Feed {
    private String postTitle;
    private String postText;
    private String userId;
    private String postId;

    public Feed(String postTitle, String postText, String userId, String postId) {
        this.postTitle = postTitle;
        this.postText = postText;
        this.userId = userId;
        this.postId = postId;

    }

    public Feed(String postText, String userId, String postId) {
        this.postText = postText;
        this.userId = userId;
        this.postId = postId;

    }

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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getPostTitle() {
        return postTitle;
    }

    public void setPostTitle(String postTitle) {
        this.postTitle = postTitle;
    }
}
