package com.example.b07group6.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.b07group6.R;
import com.example.b07group6.shared.AuthUserModel;
import com.example.b07group6.shared.User;

public class HomeFragment extends Fragment {

    public HomeFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get the AuthUserModel
        NavBackStackEntry backStackEntry = Navigation.findNavController(view).getBackStackEntry(R.id.navigation_graph);
        AuthUserModel authUserModel = new ViewModelProvider(backStackEntry).get(AuthUserModel.class);

        TextView usernameDisplay = view.findViewById(R.id.display_username);
        TextView emailDisplay = view.findViewById(R.id.display_email);
        TextView idDisplay = view.findViewById(R.id.display_id);
        User user = authUserModel.getCurrentUser();
        usernameDisplay.setText("Username: " + user.getUsername());
        emailDisplay.setText("Email: " + user.getEmail());
        idDisplay.setText("ID: " + user.getId());
    }
}