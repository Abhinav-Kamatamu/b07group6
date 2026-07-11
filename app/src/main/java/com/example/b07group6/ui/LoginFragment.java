package com.example.b07group6.ui;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavBackStackEntry;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Map;

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

        // Get the AuthUserModel
        NavBackStackEntry backStackEntry = Navigation.findNavController(requireView())
                .getBackStackEntry(R.id.navigation_graph);
        AuthUserModel authUserModel = new ViewModelProvider(backStackEntry).get(AuthUserModel.class);

        // Find our fields
        EditText emailField = view.findViewById(R.id.email_field);
        EditText passwordField = view.findViewById(R.id.password_field);
        Button loginButton = view.findViewById(R.id.login_button);
        TextView newAccountView = view.findViewById(R.id.create_account_view);

        // Listen for clicks on the button
        loginButton.setOnClickListener((v) -> {
            String email = emailField.getText().toString();
            String password = passwordField.getText().toString();
            if (email.isBlank() || password.isBlank()) {
                Toast.makeText(getContext(), "A field is either empty or blank", Toast.LENGTH_SHORT).show();
                return;
            }
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener((authTask) -> {
                Exception authException = authTask.getException();
                if (authException instanceof FirebaseAuthException) {
                    String errorCode = ((FirebaseAuthException) authException).getErrorCode();
                    String errorMessage = authException.getMessage();
                    Toast.makeText(getContext(), errorCode + " " + errorMessage, Toast.LENGTH_SHORT).show();
                    return;
                } else if (authException != null) {
                    String errorMessage = authException.getMessage();
                    Toast.makeText(getContext(), "Unexpected Error: " + errorMessage, Toast.LENGTH_SHORT).show();
                    return;
                } else if (!authTask.isSuccessful()) {
                    Toast.makeText(getContext(), "Could not sign in", Toast.LENGTH_SHORT).show();
                    return;
                }
                FirebaseUser firebaseUser = authTask.getResult().getUser();
                assert firebaseUser != null;
                FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid())
                        .get()
                        .addOnCompleteListener((dbTask) -> {
                            if (!dbTask.isSuccessful() || dbTask.getResult() == null || !dbTask.getResult().exists()) {
                                Toast.makeText(getContext(), "Could not load user data", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            DataSnapshot snapshot = dbTask.getResult();
                            String username = snapshot.child("username").getValue(String.class);
                            Boolean isAdmin = snapshot.child("isAdmin").getValue(Boolean.class);
                            User user = new User(
                                firebaseUser.getUid(),
                                username,
                                firebaseUser.getEmail(),
                                isAdmin != null && isAdmin
                            );
                            authUserModel.setCurrentUser(user);
                            Navigation.findNavController(view).navigate(R.id.action_login_to_home);
                        });
            });
        });
        // listen for clicks on the view
        newAccountView.setOnClickListener(
                (v) -> Navigation.findNavController(view).navigate(R.id.action_create_account)
        );
    }
}