package com.example.b07group6.ui.login;

import com.example.b07group6.backend.AuthOperator;

/** The presenter for the Login page */
public class LoginPresenter implements LoginContract.Presenter {
    private final LoginContract.View view;
    private final AuthOperator authModel;

    /**
     * Create a presenter that handles logic between the model and the view
     * @param view an object implementing {@link LoginContract.View}
     * @param authModel an object implementing {@link AuthOperator}
     * */
    public LoginPresenter(LoginContract.View view, AuthOperator authModel) {
        this.view = view;
        this.authModel = authModel;
    }

    @Override
    public void onLoginClicked(String email, String password) {
        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            view.showError("A field is either empty or blank");
            return;
        }
        authModel.signIn(email, password, new AuthOperator.AuthCallback() {
            @Override
            public void onSuccess(String uid) {
                authModel.getUserRecord(uid, new AuthOperator.UserRecordCallback() {
                    @Override
                    public void onSuccess(String username, boolean isAdmin) {
                        view.navigateToHome(uid, username, email, isAdmin);
                    }
                    @Override
                    public void onFailure(String errorMessage) {
                        view.showError("Could not load user data");
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