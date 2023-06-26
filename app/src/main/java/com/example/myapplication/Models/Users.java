package com.example.myapplication.Models;

public class Users {
    String UserId, UserName;

    public Users() {
    }

    public Users(String userId, String userName) {
        UserId = userId;
        UserName = userName;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }
}
