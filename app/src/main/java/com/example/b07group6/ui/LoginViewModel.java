package com.example.b07group6.ui;

import androidx.lifecycle.ViewModel;

import com.example.b07group6.shared.User;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Optional;

public class LoginViewModel extends ViewModel {

    FirebaseDatabase db;
    FirebaseAuth auth;

    public LoginViewModel() {
        db = FirebaseDatabase.getInstance("");
        auth = FirebaseAuth.getInstance();
    }
    // This might not be a valid email + password combo
    public Optional<User> tryLogin(String email, String password) {
        // TODO: use Firebase validation logic later
        if (!email.trim().isEmpty() && password.length() >= 4) {
            boolean isAdmin = email.equalsIgnoreCase("admin");
            return Optional.of(new User("id", "RandomUsername", email, "authtoken", isAdmin));
        }
        return Optional.empty();
    }

    public FirebaseDatabase getDb() {
        return db;
    }

    public FirebaseAuth getAuth() {
        return auth;
    }
}