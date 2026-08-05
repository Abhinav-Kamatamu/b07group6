package com.example.b07group6.ui.createaccount;

/**
 * Defines the contract for the Create Account page
 */
public interface CreateAccountContract {
    /** Contains all functions required for the Create Account View */
    interface View {
        /**
         * Method to allow the view to handle error messages
         * @param message the error message
         */
        void showError(String message);
        /**
         * Method to allow the view to navigate to the home page
         * @param uid the id of the user
         * @param username the username of the user
         * @param email the email fo the user
         * @param isAdmin an indication of whether the user is an admin or not
         */
        void navigateToHome(String uid, String username, String email, boolean isAdmin);
    }

    interface Presenter {
        /**
         * Method to permit the presenter to handle an attempt to create an account
         * @param username the username of the user
         * @param email the email of the user
         * @param password the password of the user
         */
        void onCreateAccountClicked(String username, String email, String password);
    }
}