package com.example.b07group6.shared;

import androidx.lifecycle.ViewModel;

import java.util.Optional;

public class AuthUserModel extends ViewModel {
    private Optional<User> currentUser = Optional.empty();

    public void setCurrentUser(User user) {
        currentUser = Optional.of(user);
    }

    public boolean userIsPresent() {
        return this.currentUser.isPresent();
    }

    public User getCurrentUser() {
        assert currentUser.isPresent();
        return currentUser.get();
    }
}