package com.example.b07group6.ui;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.example.b07group6.backend.AuthOperator;
import com.example.b07group6.ui.login.LoginContract;
import com.example.b07group6.ui.login.LoginPresenter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * Tests LoginPresenter by
 * - verifying its methods return what it expected and nothing else
 * - asserting that values retrieved from the View are sent to the correct location
 */
@RunWith(MockitoJUnitRunner.class)
public class LoginPageTest {

    @Mock
    private LoginContract.View view;

    @Mock
    private AuthOperator authOperator;

    private LoginPresenter presenter;

    /**
     * Sets up the login presenter
     */
    @Before
    public void setUp() {
        presenter = new LoginPresenter(view, authOperator);
    }

    /**
     * Tests if an attempted login with an empty email
     * is not successful
     */
    @Test
    public void emptyEmail_showsError_doesNotCallAuth() {
        presenter.onLoginClicked("", "password123");

        verify(view).showError("A field is either empty or blank");
        verify(authOperator, never()).signIn(anyString(), anyString(), any());
    }

    /**
     * Tests if an attempted login with a blank password
     * is not successful
     */
    @Test
    public void blankPassword_showsError_doesNotCallAuth() {
        presenter.onLoginClicked("name@gmail.com", "   ");

        verify(view).showError("A field is either empty or blank");
        verify(authOperator, never()).signIn(anyString(), anyString(), any());
    }

    /**
     * Tests if an attempted login with correct credentials successfully logs
     * in and navigates to the home page
     */
    @Test
    public void validCredentials_navigatesHomeWithCorrectUser() {
        // Mock successful sign in
        doAnswer(invocation -> {
            AuthOperator.AuthCallback callback = invocation.getArgument(2);
            callback.onSuccess("dummy-uid");
            return null;
        }).when(authOperator).signIn(anyString(), anyString(), any());

        // Mock successful user record fetch (Non-Admin)
        doAnswer(invocation -> {
            AuthOperator.UserRecordCallback callback = invocation.getArgument(1);
            callback.onSuccess("AnActualUsername", false);
            return null;
        }).when(authOperator).getUserRecord(anyString(), any());

        presenter.onLoginClicked("name@gmail.com", "omg6767hehe");

        verify(view).navigateToHome("dummy-uid", "AnActualUsername", "name@gmail.com", false);
    }

    /**
     * Tests if an attempted login for an admin account successfully logs the user
     * in as an admin
     */
    @Test
    public void adminUser_navigatesHomeWithAdminFlag() {
        // Mock successful sign in
        doAnswer(invocation -> {
            AuthOperator.AuthCallback callback = invocation.getArgument(2);
            callback.onSuccess("dummy-uid");
            return null;
        }).when(authOperator).signIn(anyString(), anyString(), any());

        // Mock successful user record fetch (Admin)
        doAnswer(invocation -> {
            AuthOperator.UserRecordCallback callback = invocation.getArgument(1);
            callback.onSuccess("AdminUser", true);
            return null;
        }).when(authOperator).getUserRecord(anyString(), any());

        presenter.onLoginClicked("admin@outlook.com", "sixsept#huitneuf");

        verify(view).navigateToHome("dummy-uid", "AdminUser", "admin@outlook.com", true);
    }

    /**
     * Tests if an attempted login with an incorrect password
     * is not successful
     */
    @Test
    public void wrongPassword_showsAuthErrorFromSignIn() {
        // Mock failed sign in
        doAnswer(invocation -> {
            AuthOperator.AuthCallback callback = invocation.getArgument(2);
            callback.onFailure("The password is invalid");
            return null;
        }).when(authOperator).signIn(anyString(), anyString(), any());

        presenter.onLoginClicked("name@gmail.com", "wrongpassword");

        verify(view).showError("The password is invalid");
        verify(authOperator, never()).getUserRecord(anyString(), any());
        verify(view, never()).navigateToHome(anyString(), anyString(), anyString(), anyBoolean());
    }

    /**
     * Tests if a successful login that fails to fetch the users record
     * displays an error
     */
    @Test
    public void signInSucceeds_butUserRecordFetchFails_showsError() {
        // Mock successful sign in
        doAnswer(invocation -> {
            AuthOperator.AuthCallback callback = invocation.getArgument(2);
            callback.onSuccess("dummy-uid");
            return null;
        }).when(authOperator).signIn(anyString(), anyString(), any());

        // Mock failed user record fetch
        doAnswer(invocation -> {
            AuthOperator.UserRecordCallback callback = invocation.getArgument(1);
            callback.onFailure("Could not load user data");
            return null;
        }).when(authOperator).getUserRecord(anyString(), any());

        presenter.onLoginClicked("name@gmail.com", "password123");

        verify(view).showError("Could not load user data");
        verify(view, never()).navigateToHome(anyString(), anyString(), anyString(), anyBoolean());
    }
}