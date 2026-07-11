package com.example.b07group6.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.b07group6.R;
import com.example.b07group6.shared.AuthUserModel;

public class MainFragment extends Fragment {

    public MainFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView usernameDisplay = view.findViewById(R.id.display_username);
        TextView emailDisplay = view.findViewById(R.id.display_email);

        AuthUserModel authUserModel = new ViewModelProvider(requireActivity()).get(AuthUserModel.class);

        authUserModel.getCurrentUser().observe(getViewLifecycleOwner(), (user) -> {
            if (user != null) {
                usernameDisplay.setText("Username: " + user.getUsername());
                emailDisplay.setText("Email: " + user.getEmail());
            }
        });
    }
}