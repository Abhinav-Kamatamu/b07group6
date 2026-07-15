package com.example.b07group6.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.example.b07group6.backend.AuthOperator;
import com.example.b07group6.construct.User;
import com.example.b07group6.ui.login.LoginContract;
import com.example.b07group6.ui.login.LoginPresenter;

import org.junit.Before;
import org.junit.Test;

public class LoginPageTest {

    private DummyView view;
    private DummyAuthOperator authOperator;
    private LoginPresenter presenter;

    @Before
    public void setUp() {
        view = new DummyView();
        authOperator = new DummyAuthOperator();
        presenter = new LoginPresenter(view, authOperator);
    }

    @Test
    public void emptyEmail_showsError_doesNotCallAuth() {
        presenter.onLoginClicked("", "password123");
        assertEquals("A field is either empty or blank", view.lastError);
        assertFalse(authOperator.signInCalled);
    }

    @Test
    public void blankPassword_showsError_doesNotCallAuth() {
        presenter.onLoginClicked("name@gmail.com", "   ");
        assertEquals("A field is either empty or blank", view.lastError);
        assertFalse(authOperator.signInCalled);
    }

    @Test
    public void validCredentials_navigatesHomeWithCorrectUser() {
        authOperator.userRecordUsername = "AnActualUsername";
        authOperator.userRecordIsAdmin = false;
        presenter.onLoginClicked("name@gmail.com", "omg6767hehe");
        assertTrue(view.navigatedHome);
        assertEquals("dummy-uid", view.dummyUser.getUid());
        assertEquals("AnActualUsername", view.dummyUser.getUsername());
        assertEquals("name@gmail.com", view.dummyUser.getEmail());
        assertFalse(view.dummyUser.isAdmin());
    }

    @Test
    public void adminUser_navigatesHomeWithAdminFlag() {
        authOperator.userRecordUsername = "AdminUser";
        authOperator.userRecordIsAdmin = true;
        presenter.onLoginClicked("admin@outlook.com", "sixsept#huitneuf");
        assertTrue(view.navigatedHome);
        assertTrue(view.dummyUser.isAdmin());
    }

    @Test
    public void wrongPassword_showsAuthErrorFromSignIn() {
        authOperator.signInError = "The password is invalid";
        presenter.onLoginClicked("name@gmail.com", "wrongpassword");
        assertEquals("The password is invalid", view.lastError);
        assertFalse(view.navigatedHome);
    }

    @Test
    public void signInSucceeds_butUserRecordFetchFails_showsError() {
        authOperator.userRecordError = "Could not load user data";
        presenter.onLoginClicked("name@gmail.com", "password123");
        assertEquals("Could not load user data", view.lastError);
        assertFalse(view.navigatedHome);
    }

    // Android Studio says "Inner class 'DummyAuthOperator' may be 'static' "

    static class DummyView implements LoginContract.View {
        String lastError;
        boolean navigatedHome;
        User dummyUser;

        @Override
        public void showError(String message) {
            lastError = message;
        }

        @Override
        public void navigateToHome(String uid, String username, String email, boolean isAdmin) {
            navigatedHome = true;
            dummyUser = new User(uid, username, email, isAdmin);
        }
    }

    static class DummyAuthOperator implements AuthOperator {
        boolean signInCalled;
        String signInError;
        String userRecordUsername;
        boolean userRecordIsAdmin;
        String userRecordError;

        @Override
        public void signIn(String email, String password, AuthCallback callback) {
            signInCalled = true;
            if (signInError != null) {
                callback.onFailure(signInError);
            } else {
                callback.onSuccess("dummy-uid");
            }
        }

        @Override
        public void getUserRecord(String uid, UserRecordCallback callback) {
            if (userRecordError != null) {
                callback.onFailure(userRecordError);
            } else {
                callback.onSuccess(userRecordUsername, userRecordIsAdmin);
            }
        }

        @Override
        public void signUp(String email, String password, AuthCallback callback) {
            throw new UnsupportedOperationException("Sign up is not needed for LoginPageTest");
        }

        @Override
        public void saveUserRecord(String uid, String username, String email, boolean isAdmin, AuthCallback callback) {
            throw new UnsupportedOperationException("Saving user records is not needed for LoginPageTest");
        }
    }
}