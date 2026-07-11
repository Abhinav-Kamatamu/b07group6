package com.example.b07group6.ui;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.b07group6.R;
import com.example.b07group6.shared.User;
import com.example.b07group6.shared.AuthUserModel;

import java.util.Optional;

public class LoginFragment extends Fragment {

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Create our Models
        LoginViewModel loginModel = new ViewModelProvider(this).get(LoginViewModel.class);
        AuthUserModel authViewModel = new ViewModelProvider(requireActivity()).get(AuthUserModel.class);

        // Find our fields
        EditText emailField = view.findViewById(R.id.email_field);
        EditText passwordField = view.findViewById(R.id.password_field);
        Button loginButton = view.findViewById(R.id.login_button);
        TextView newAccountView = view.findViewById(R.id.create_account_view);

        // Listen for clicks on the button
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailField.getText().toString();
                String password = passwordField.getText().toString();
                Optional<User> loginResult = loginModel.tryLogin(email, password);
                loginResult.ifPresentOrElse(
                        (user) -> {
                            authViewModel.loginUser(user);
                            Navigation.findNavController(view).navigate(R.id.action_login_to_home);
                        },
                        () -> {
                            Toast.makeText(getContext(), "Invalid Credentials", Toast.LENGTH_SHORT).show();
                        }
                );
            }
        });
        // listen for clicks on the view
        newAccountView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.action_create_account);
            }
        });
    }
}