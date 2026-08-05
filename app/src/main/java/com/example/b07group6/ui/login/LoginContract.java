package com.example.b07group6.ui.login;

/**
 * Defines the contract for the Login page
 */
public interface LoginContract {
    /** Contains all functions required for the Login View */
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
    /** Contains all functions required of the Login Presenter */
    interface Presenter {
        /**
         * Method to permit the presenter to handle a login
         * @param email the email of the user
         * @param password the password of the user
         */
        void onLoginClicked(String email, String password);
    }
}