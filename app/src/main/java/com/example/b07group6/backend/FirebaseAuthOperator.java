package com.example.b07group6.backend;

import android.os.Handler;
import android.os.Looper;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class FirebaseAuthOperator implements AuthOperator {
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    @Override
    public void signIn(String email, String password, AuthCallback callback) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(authTask -> {
                    Exception authException = authTask.getException();
                    if (authException instanceof FirebaseAuthException) {
                        String errorCode = ((FirebaseAuthException) authException).getErrorCode();
                        mainHandler.post(() -> callback.onFailure(errorCode + " " + authException.getMessage()));
                        return;
                    } else if (authException != null) {
                        mainHandler.post(() -> callback.onFailure("Unexpected Error: " + authException.getMessage()));
                        return;
                    } else if (!authTask.isSuccessful()) {
                        mainHandler.post(() -> callback.onFailure("Could not sign in"));
                        return;
                    }
                    FirebaseUser firebaseUser = authTask.getResult().getUser();
                    if (firebaseUser == null) {
                        mainHandler.post(() -> callback.onFailure("Could not sign in"));
                        return;
                    }
                    mainHandler.post(() -> callback.onSuccess(firebaseUser.getUid()));
                });
    }
    @Override
    public void getUserRecord(String uid, UserRecordCallback callback) {
        FirebaseDatabase.getInstance().getReference("users").child(uid)
                .get()
                .addOnCompleteListener(dbTask -> {
                    if (!dbTask.isSuccessful() || dbTask.getResult() == null || !dbTask.getResult().exists()) {
                        mainHandler.post(() -> callback.onFailure("Could not load user data"));
                        return;
                    }
                    DataSnapshot snapshot = dbTask.getResult();
                    String username = snapshot.child("username").getValue(String.class);
                    Boolean isAdmin = snapshot.child("isAdmin").getValue(Boolean.class);
                    mainHandler.post(() -> callback.onSuccess(username, isAdmin != null && isAdmin));
                });
    }
    @Override
    public void signUp(String email, String password, AuthCallback callback) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(authTask -> {
                    Exception authException = authTask.getException();
                    if (authException instanceof FirebaseAuthException) {
                        String errorCode = ((FirebaseAuthException) authException).getErrorCode();
                        mainHandler.post(() -> callback.onFailure(errorCode + " " + authException.getMessage()));
                        return;
                    } else if (authException != null) {
                        mainHandler.post(() -> callback.onFailure("Unexpected Error: " + authException.getMessage()));
                        return;
                    } else if (!authTask.isSuccessful()) {
                        mainHandler.post(() -> callback.onFailure("Could not create account"));
                        return;
                    }
                    FirebaseUser firebaseUser = authTask.getResult().getUser();
                    if (firebaseUser == null) {
                        mainHandler.post(() -> callback.onFailure("Could not create account"));
                        return;
                    }
                    mainHandler.post(() -> callback.onSuccess(firebaseUser.getUid()));
                });
    }
    @Override
    public void saveUserRecord(String uid, String username, String email, boolean isAdmin, AuthCallback callback) {
        Map<String, Object> userRecord = new HashMap<>();
        userRecord.put("username", username);
        userRecord.put("email", email);
        userRecord.put("isAdmin", isAdmin);

        FirebaseDatabase.getInstance().getReference("users").child(uid)
                .setValue(userRecord)
                .addOnCompleteListener(writeTask -> {
                    if (!writeTask.isSuccessful()) {
                        mainHandler.post(() -> callback.onFailure("Could not save user record"));
                        return;
                    }
                    mainHandler.post(() -> callback.onSuccess(uid));
                });
    }
}