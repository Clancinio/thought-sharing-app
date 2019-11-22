package com.example.thoughtsharingapp.classes;

public class FriendRequest {
    private String postId;
    private String requestId;

    public FriendRequest(String postId,String requestId){
        this.postId = postId;
        this.requestId = requestId;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }
}
