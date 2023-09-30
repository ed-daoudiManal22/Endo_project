package com.spmenais.paincare.Models;

public class Users {
    String UserId, name,email,imageUrl;

    public Users() {
    }

    public Users(String userId, String userName) {
        UserId = userId;
        name = userName;
    }

    public Users(String userId, String userName, String email) {
        UserId = userId;
        name = userName;
        this.email = email;
    }

    public Users(String userId, String name, String email, String imageUrl) {
        UserId = userId;
        this.name = name;
        this.email = email;
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String userName) {
        name = userName;
    }
}
