package com.example.b07group6.shared;

import com.google.firebase.auth.FirebaseUser;

public class User {
    private String id;
    private String username;
    private String email;
    private boolean isAdmin;

    public User(String id, String username, String email, boolean isAdmin) {
        this.id = id;
        this.username = username;
        this.email = email;
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

    public boolean isAdmin() {
        return isAdmin;
    }
}
