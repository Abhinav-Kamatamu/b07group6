package com.example.b07group6.shared;

import androidx.lifecycle.ViewModel;

import com.example.b07group6.construct.User;

public class UserViewModel extends ViewModel {
    private User currentUser;
    private String artifactEditingLotNumber;

    public void setCurrentUser(User user) {
        currentUser = user;
    }

    public User getCurrentUser() {
        if (!isLoggedIn()) {
            throw new IllegalStateException("getCurrentUser() called with no user logged in");
        }
        return currentUser;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public void logout() {
        currentUser = null;
    }


    public boolean isInArtifactEditMode() {
        return artifactEditingLotNumber != null;
    }

    public String getArtifactEditingLotNumber() {
        return artifactEditingLotNumber;
    }

    public void setArtifactEditingLotNumber(String artifactEditingLotNumber) {
        this.artifactEditingLotNumber = artifactEditingLotNumber;
    }
}