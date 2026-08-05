package com.example.b07group6.ui;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.example.b07group6.backend.AuthOperator;
import com.example.b07group6.ui.createaccount.CreateAccountContract;
import com.example.b07group6.ui.createaccount.CreateAccountPresenter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * Tests CreateAccountPresenter by
 * - verifying its methods return what it expected and nothing else
 * - asserting that values retrieved from the View are sent to the correct location
 */
@RunWith(MockitoJUnitRunner.class)
public class CreateAccountPageTest {

    @Mock
    private CreateAccountContract.View view;

    @Mock
    private AuthOperator authOperator;

    private CreateAccountPresenter presenter;

    /**
     * Sets up the create account presenter
     */
    @Before
    public void setUp() {
        presenter = new CreateAccountPresenter(view, authOperator);
    }

    /**
     * Tests if an attempted sign-up with an empty username
     * is not successful
     */
    @Test
    public void emptyUsername_showsError_doesNotCallAuth() {
        presenter.onCreateAccountClicked("", "name@gmail.com", "password123");

        verify(view).showError("A field is either empty or blank");
        verify(authOperator, never()).signUp(anyString(), anyString(), any());
    }

    /**
     * Tests if an attempted sign-up with a blank email
     * is not successful
     */
    @Test
    public void blankEmail_showsError_doesNotCallAuth() {
        presenter.onCreateAccountClicked("Haaland", "  ", "password123");

        verify(view).showError("A field is either empty or blank");
        verify(authOperator, never()).signUp(anyString(), anyString(), any());
    }

    /**
     * Tests if an attempted sign-up with an empty email
     * is not successful
     */
    @Test
    public void emptyPassword_showsError_doesNotCallAuth() {
        presenter.onCreateAccountClicked("Mbappe", "name@gmail.com", "");

        verify(view).showError("A field is either empty or blank");
        verify(authOperator, never()).signUp(anyString(), anyString(), any());
    }

    /**
     * Tests if an attempted sign-up valid input fields successfullt\y
     * navigates to the homepage with this newly created account
     */
    @Test
    public void validInputs_navigatesHomeWithNewUser() {
        // Mock successful sign up
        doAnswer(invocation -> {
            AuthOperator.AuthCallback callback = invocation.getArgument(2);
            callback.onSuccess("dummy-uid");
            return null;
        }).when(authOperator).signUp(anyString(), anyString(), any());

        // Mock successful user record saving
        doAnswer(invocation -> {
            AuthOperator.AuthCallback callback = invocation.getArgument(4);
            callback.onSuccess("dummy-uid");
            return null;
        }).when(authOperator).saveUserRecord(anyString(), anyString(), anyString(), anyBoolean(), any());

        presenter.onCreateAccountClicked("Ronaldo", "name@gmail.com", "password123");

        verify(view).navigateToHome("dummy-uid", "Ronaldo", "name@gmail.com", false);
    }

    /**
     * Tests if an attempted sign-up with an email that is already in use
     * is not successful
     */
    @Test
    public void signUpFails_showsAuthError_doesNotSaveRecord() {
        // Mock failed sign up
        doAnswer(invocation -> {
            AuthOperator.AuthCallback callback = invocation.getArgument(2);
            callback.onFailure("The email address is already in use");
            return null;
        }).when(authOperator).signUp(anyString(), anyString(), any());

        presenter.onCreateAccountClicked("AVeryUniqueUsername", "name@gmail.com", "password123");

        verify(view).showError("The email address is already in use");
        verify(authOperator, never()).saveUserRecord(anyString(), anyString(), anyString(), anyBoolean(), any());
        verify(view, never()).navigateToHome(anyString(), anyString(), anyString(), anyBoolean());
    }

    /**
     * Tests if an attempted sign-up shows an error if sign-up was successful but
     * record saving fails
     */
    @Test
    public void signUpSucceeds_butSaveRecordFails_showsError() {
        // Mock successful sign up
        doAnswer(invocation -> {
            AuthOperator.AuthCallback callback = invocation.getArgument(2);
            callback.onSuccess("dummy-uid");
            return null;
        }).when(authOperator).signUp(anyString(), anyString(), any());

        // Mock failed user record saving
        doAnswer(invocation -> {
            AuthOperator.AuthCallback callback = invocation.getArgument(4);
            callback.onFailure("Could not save user record");
            return null;
        }).when(authOperator).saveUserRecord(anyString(), anyString(), anyString(), anyBoolean(), any());

        presenter.onCreateAccountClicked("AVeryUniqueUsername", "name@gmail.com", "password123");

        verify(view).showError("Could not save user record");
        verify(view, never()).navigateToHome(anyString(), anyString(), anyString(), anyBoolean());
    }
}