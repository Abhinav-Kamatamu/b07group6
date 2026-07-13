package com.example.b07group6.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.example.b07group6.backend.AuthOperator;
import com.example.b07group6.construct.User;
import com.example.b07group6.ui.createaccount.CreateAccountContract;
import com.example.b07group6.ui.createaccount.CreateAccountPresenter;

import org.junit.Before;
import org.junit.Test;

public class CreateAccountPageTest {

    private DummyView view;
    private DummyAuthOperator authOperator;
    private CreateAccountPresenter presenter;

    @Before
    public void setUp() {
        view = new DummyView();
        authOperator = new DummyAuthOperator();
        presenter = new CreateAccountPresenter(view, authOperator);
    }

    @Test
    public void emptyUsername_showsError_doesNotCallAuth() {
        presenter.onCreateAccountClicked("", "name@gmail.com", "password123");
        assertEquals("A field is either empty or blank", view.lastError);
        assertFalse(authOperator.signUpCalled);
    }

    @Test
    public void blankEmail_showsError_doesNotCallAuth() {
        presenter.onCreateAccountClicked("Haaland", "  ", "password123");
        assertEquals("A field is either empty or blank", view.lastError);
        assertFalse(authOperator.signUpCalled);
    }

    @Test
    public void emptyPassword_showsError_doesNotCallAuth() {
        presenter.onCreateAccountClicked("Mbappe", "name@gmail.com", "");
        assertEquals("A field is either empty or blank", view.lastError);
        assertFalse(authOperator.signUpCalled);
    }

    @Test
    public void validInputs_navigatesHomeWithNewUser() {
        presenter.onCreateAccountClicked("Ronaldo", "name@gmail.com", "password123");
        assertTrue(view.navigatedHome);
        assertEquals("dummy-uid", view.dummyUser.getId());
        assertEquals("Ronaldo", view.dummyUser.getUsername());
        assertEquals("name@gmail.com", view.dummyUser.getEmail());
        assertFalse(view.dummyUser.isAdmin());
    }

    @Test
    public void signUpFails_showsAuthError_doesNotSaveRecord() {
        authOperator.signUpError = "The email address is already in use";
        presenter.onCreateAccountClicked("AVeryUniqueUsername", "name@gmail.com", "password123");
        assertEquals("The email address is already in use", view.lastError);
        assertFalse(authOperator.saveUserRecordCalled);
        assertFalse(view.navigatedHome);
    }

    @Test
    public void signUpSucceeds_butSaveRecordFails_showsError() {
        authOperator.saveUserRecordError = "Could not save user record";
        presenter.onCreateAccountClicked("AVeryUniqueUsername", "name@gmail.com", "password123");
        assertEquals("Could not save user record", view.lastError);
        assertFalse(view.navigatedHome);
    }

    static class DummyView implements CreateAccountContract.View {
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
        boolean signUpCalled;
        boolean saveUserRecordCalled;
        String signUpError;
        String saveUserRecordError;

        @Override
        public void signIn(String email, String password, AuthCallback callback) {
            throw new UnsupportedOperationException("Sign in is not needed for CreateAccountPageTest");
        }

        @Override
        public void getUserRecord(String uid, UserRecordCallback callback) {
            throw new UnsupportedOperationException("Get user record is not needed for CreateAccountPageTest");
        }

        @Override
        public void signUp(String email, String password, AuthCallback callback) {
            signUpCalled = true;
            if (signUpError != null) {
                callback.onFailure(signUpError);
            } else {
                callback.onSuccess("dummy-uid");
            }
        }

        @Override
        public void saveUserRecord(String uid, String username, String email, boolean isAdmin, AuthCallback callback) {
            saveUserRecordCalled = true;
            if (saveUserRecordError != null) {
                callback.onFailure(saveUserRecordError);
            } else {
                callback.onSuccess(uid);
            }
        }
    }
}