package com.example.b07group6.construct;

public class User {
    private String uid;
    private String username;
    private String email;
    private boolean isAdmin;

    public User(String uid, String username, String email, boolean isAdmin) {
        this.uid = uid;
        this.username = username;
        this.email = email;
        this.isAdmin = isAdmin;
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

    public boolean isAdmin() {
        return isAdmin;
    }
}
