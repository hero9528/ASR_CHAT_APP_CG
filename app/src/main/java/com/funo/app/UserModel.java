package com.funo.app;

public class UserModel {
    private String uid;
    private String username;
    private String email;
    private String avatar;

    public UserModel() {
    }

    public UserModel(String uid, String username, String email, String avatar) {
        this.uid = uid;
        this.username = username;
        this.email = email;
        this.avatar = avatar;
    }

    public String getUid() {
        return uid;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getAvatar() {
        return avatar;
    }
}