package com.example.b07group6.construct;

/** The representation of a User stored in Firebase */
public class User {
    private String uid;
    private String username;
    private String email;
    private boolean isAdmin;

    /**
     * Constructs a new {@code User} with complete identification, profile credentials, 
     * and privilege specifications.
     * @param uid the user id
     * @param username the username of the user
     * @param email the email associated with the account
     * @param isAdmin true if the user has administrative privileges, false otherwise
     */
    public User(String uid, String username, String email, boolean isAdmin) {
        this.uid = uid;
        this.username = username;
        this.email = email;
        this.isAdmin = isAdmin;
    }

    /**
     * Returns the unique user identifier (UID) for this account.
     * @return the user UID
     */
    public String getUid() {
        return uid;
    }

    /**
     * Returns the username of the user.
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Returns the email address associated with the user account.
     * @return the email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * Indicates whether the user possesses administrative privileges.
     * @return true if the user is an administrator, false otherwise
     */
    public boolean isAdmin() {
        return isAdmin;
    }
}
