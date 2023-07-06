package com.example.myapplication.Models;

public class Post {
    private String postId;
    private String postText;
    private String userId;

    // Default constructor required for Firebase
    public Post() {}

    public Post(String postId, String postText, String userId) {
        this.postId = postId;
        this.postText = postText;
        this.userId = userId;
    }

    public String getPostId() {
        return postId;
    }

    public String getPostText() {
        return postText;
    }

    public String getUserId() {
        return userId;
    }
}
