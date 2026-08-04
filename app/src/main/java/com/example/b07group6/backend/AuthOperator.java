package com.example.b07group6.backend;

/** The model interface for both Login and Create pages*/
public interface AuthOperator {
    /** Contains all functions related to user authentication */
    interface AuthCallback {
        /**
         * Method to allow handing successful authentication
         * @param uid the id of the user
         */
        void onSuccess(String uid);

        /**
         * Method to allow handing failed authentication
         * @param errorMessage the error message
         */
        void onFailure(String errorMessage);
    }
    /** Contains all functions required for the retrieval of user data */
    interface UserRecordCallback {
        /**
         *  Method to allow handling successful retrieval of user data
         * @param username the username of the user
         * @param isAdmin an indicator of whether the user is an admin or not
         */
        void onSuccess(String username, boolean isAdmin);

        /**
         * Method to allow handling failed retrieval of suer data
         * @param errorMessage the error message
         */
        void onFailure(String errorMessage);
    }

    /**
     *  Method to attempt to sign a user in
     * @param email the email of the user
     * @param password the password of the user
     * @param callback the callback that will run
     */
    void signIn(String email, String password, AuthCallback callback);

    /**
     * Method to attempt to sign a user up
     * @param email the email of the user
     * @param password the password of the user
     * @param callback the callback that will run
     */
    void signUp(String email, String password, AuthCallback callback);

    /**
     * Method to get a user record
     * @param uid the uid of the user
     * @param callback the callback that will run
     */
    void getUserRecord(String uid, UserRecordCallback callback);

    /** Method to save a user record
     * @param uid the uid of the user
     * @param username the username of the user
     * @param email the email of the user
     * @param isAdmin an indicator of whether the user is an admin or not
     * @param callback the callback that will run
     */
    void saveUserRecord(String uid, String username, String email, boolean isAdmin, AuthCallback callback);
}