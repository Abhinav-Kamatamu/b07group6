package com.example.b07group6.ui.createaccount;

import com.example.b07group6.backend.AuthOperator;

public class CreateAccountPresenter implements CreateAccountContract.Presenter {
    private final CreateAccountContract.View view;
    private final AuthOperator authOperator;

    public CreateAccountPresenter(CreateAccountContract.View view, AuthOperator authOperator) {
        this.view = view;
        this.authOperator = authOperator;
    }

    @Override
    public void onCreateAccountClicked(String username, String email, String password) {
        if (username == null || username.isBlank() || email == null || email.isBlank() || password == null || password.isBlank()) {
            view.showError("A field is either empty or blank");
            return;
        }
        authOperator.signUp(email, password, new AuthOperator.AuthCallback() {
            @Override
            public void onSuccess(String uid) {
                authOperator.saveUserRecord(uid, username, email, false, new AuthOperator.AuthCallback() {
                    @Override
                    public void onSuccess(String savedUid) {
                        view.navigateToHome(savedUid, username, email, false);
                    }
                    @Override
                    public void onFailure(String errorMessage) {
                        view.showError(errorMessage);
                    }
                });
            }
            @Override
            public void onFailure(String errorMessage) {
                view.showError(errorMessage);
            }
        });
    }
}