package com.example.b07group6.shared;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AuthUserModel extends ViewModel {
    private final MutableLiveData<User> currentUser = new MutableLiveData<>();

    public void loginUser(User user) {
        currentUser.setValue(user);
    }

    public LiveData<User> getCurrentUser() {
        return currentUser;
    }
}