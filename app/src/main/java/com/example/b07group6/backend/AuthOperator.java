package com.example.b07group6.backend;

public interface AuthOperator {
    interface AuthCallback {
        void onSuccess(String uid);
        void onFailure(String errorMessage);
    }
    interface UserRecordCallback {
        void onSuccess(String username, boolean isAdmin);
        void onFailure(String errorMessage);
    }
    void signIn(String email, String password, AuthCallback callback);
    void signUp(String email, String password, AuthCallback callback);
    void getUserRecord(String uid, UserRecordCallback callback);
    void saveUserRecord(String uid, String username, String email, boolean isAdmin, AuthCallback callback);
}