package com.example.b07group6.ui.createaccount;

public interface CreateAccountContract {
    interface View {
        void showError(String message);
        void navigateToHome(String username, String email, String password, boolean isAdmin);
    }
    interface Presenter {
        void onCreateAccountClicked(String username, String email, String password);
    }
}