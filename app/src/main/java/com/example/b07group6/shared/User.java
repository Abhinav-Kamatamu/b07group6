package com.example.b07group6.shared;

public class User {
    private String id;
    private String username;
    private String email;
    private String authToken;
    private boolean isAdmin;

    public User(String id, String username, String email, String authToken, boolean isAdmin) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.authToken = authToken;
        this.isAdmin = isAdmin;
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getAuthToken() {
        return authToken;
    }

    public boolean isAdmin() {
        return isAdmin;
    }
}
