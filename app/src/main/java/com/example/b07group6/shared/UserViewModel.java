package com.example.b07group6.shared;

import androidx.lifecycle.ViewModel;

import com.example.b07group6.construct.User;

/** The view model to keep state between fragment transitions */
public class UserViewModel extends ViewModel {
    private User currentUser;
    private String artifactEditingLotNumber;
    private String extendedLotNumber;

    /**
     * Sets the currently authenticated user for the session. Note, setting this
     * to null does not deauthenticate the user. Firebase functions must be used to
     * truly log out a user.
     * @param user the {@link User} instance to set as active, or null to clear
     */
    public void setCurrentUser(User user) {
        currentUser = user;
    }

    /**
     * Returns the currently authenticated user.
     * @return the active {@link User} object
     * @throws IllegalStateException if no user is currently logged in
     */
    public User getCurrentUser() {
        if (!isLoggedIn()) {
            throw new IllegalStateException("getCurrentUser() called with no user logged in");
        }
        return currentUser;
    }

    /**
     * Checks whether a user is currently logged into the session.
     * @return true if a user session is active, false otherwise
     */
    public boolean isLoggedIn() {
        return currentUser != null;
    }

    /**
     * Clears the current user session, simulating a log-out the active user. Note, calling this
     * method does not deauthenticate the user. Firebase functions must be used to
     * truly log out a user.
     */
    public void logout() {
        currentUser = null;
    }

    /**
     * Determines whether the application is currently in artifact edit mode.
     * @return true if an artifact editing lot number is set, false otherwise
     */
    public boolean isInArtifactEditMode() {
        return artifactEditingLotNumber != null;
    }

    /**
     * Returns the lot number of the artifact currently being edited, if applicable.
     * @return the artifact editing lot number, or null if not editing
     */
    public String getArtifactEditingLotNumber() {
        return artifactEditingLotNumber;
    }

    /**
     * Sets or clears the lot number of the artifact currently being edited.
     * @param artifactEditingLotNumber the lot number to set, or null to exit edit mode
     */
    public void setArtifactEditingLotNumber(String artifactEditingLotNumber) {
        this.artifactEditingLotNumber = artifactEditingLotNumber;
    }

    /**
     * Returns the extended lot number used for extended details or specific views.
     * @return the extended lot number
     */
    public String getExtendedLotNumber() {
        return extendedLotNumber;
    }

    /**
     * Sets the extended lot number for tracking expanded UI states.
     * @param extendedLotNumber the extended lot number to set
     */
    public void setExtendedLotNumber(String extendedLotNumber) {
        this.extendedLotNumber = extendedLotNumber;
    }
}