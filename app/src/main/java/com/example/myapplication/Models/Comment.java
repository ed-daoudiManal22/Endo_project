package com.example.myapplication.Models;

public class Comment {
    private String commentId;
    private String commentText;
    private String userId;

    // Default constructor required for Firebase
    public Comment() {}

    public Comment(String commentId, String commentText, String userId) {
        this.commentId = commentId;
        this.commentText = commentText;
        this.userId = userId;
    }

    public String getCommentId() {
        return commentId;
    }

    public String getCommentText() {
        return commentText;
    }

    public String getUserId() {
        return userId;
    }
}
