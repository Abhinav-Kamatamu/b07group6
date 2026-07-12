package com.example.b07group6.ui.login;

public interface LoginContract {
    interface View {
        void showError(String message);
        void navigateToHome(String uid, String username, String email, boolean isAdmin);
    }
    interface Presenter {
        void onLoginClicked(String email, String password);
    }
}