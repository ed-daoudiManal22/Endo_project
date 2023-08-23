package com.example.myapplication.Community;

import com.google.firebase.Timestamp;
public class Comment {
    private String commentId;
    private String commentText;
    private String userId;
    private String userName, userImage;
    private Timestamp timestamp;

    public Comment() {
        // Default constructor required for Firestore deserialization
    }
    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public Comment(String commentText) {
        this.commentText = commentText;
    }

    public Comment(String id ,String commentText, String userId,String userName,String userImage, Timestamp timestamp) {
        this.commentId =id;
        this.commentText = commentText;
        this.userId = userId;
        this.timestamp = timestamp;
        this.userName = userName;
        this.userImage = userImage;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}