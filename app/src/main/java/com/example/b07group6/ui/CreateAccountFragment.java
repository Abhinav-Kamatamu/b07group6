package com.example.b07group6.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.b07group6.R;
import com.example.b07group6.shared.AuthUserModel;
import com.example.b07group6.shared.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;


public class CreateAccountFragment extends Fragment {

    public CreateAccountFragment() {
        // Required empty public constructor
    }

    public static CreateAccountFragment newInstance() {
        return new CreateAccountFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get the AuthUserModel
        NavBackStackEntry backStackEntry = Navigation.findNavController(requireView())
                .getBackStackEntry(R.id.navigation_graph);
        AuthUserModel authUserModel = new ViewModelProvider(backStackEntry).get(AuthUserModel.class);

        // Find our fields
        EditText usernameField = view.findViewById(R.id.username_field);
        EditText emailField = view.findViewById(R.id.email_field);
        EditText passwordField = view.findViewById(R.id.password_field);
        Button createAccountButton = view.findViewById(R.id.create_account_button);

        // Listen for clicks on the button
        createAccountButton.setOnClickListener((v) -> {
                String username = usernameField.getText().toString();
                String email = emailField.getText().toString();
                String password = passwordField.getText().toString();
                if (email.isBlank() || password.isBlank()) {
                    Toast.makeText(getContext(), "A field is either empty or blank", Toast.LENGTH_SHORT).show();
                    return;
                }
                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener((authTask) -> {
                    Exception authException = authTask.getException();
                    if (authException instanceof FirebaseAuthException) {
                        String errorCode = ((FirebaseAuthException) authException).getErrorCode();
                        String errorMessage = authException.getMessage();
                        Toast.makeText(getContext(), errorCode + " " + errorMessage, Toast.LENGTH_SHORT).show();
                        return;
                    } else if (authException != null) {
                        String errorMessage = authException.getMessage();
                        System.err.println(errorMessage);
                        Toast.makeText(getContext(), "Unexpected Error: " + errorMessage, Toast.LENGTH_SHORT).show();
                        return;
                    } else if (!authTask.isSuccessful()) {
                        Toast.makeText(getContext(), "Could not create account", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    FirebaseUser firebaseUser = authTask.getResult().getUser();
                    assert firebaseUser != null;
                    firebaseUser.getIdToken(false).addOnCompleteListener((tokenTask) -> {
                        if (!tokenTask.isSuccessful()) {
                            Toast.makeText(getContext(), "Could not retrieve user token", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Map<String, Object> userRecord = new HashMap<>();
                        userRecord.put("username", username);
                        userRecord.put("email", email);
                        userRecord.put("isAdmin", false);
                        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                        firebaseDatabase.getReference("users").child(firebaseUser.getUid())
                                .setValue(userRecord)
                                .addOnCompleteListener((writeTask) -> {
                                    if (!writeTask.isSuccessful()) {
                                        Toast.makeText(getContext(), "Could not save user record", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    User user = new User(
                                        firebaseUser.getUid(),
                                        username,
                                        email,
                                        false
                                    );
                                    authUserModel.setCurrentUser(user);
                                    Navigation.findNavController(view).navigate(R.id.action_create_to_home);
                                });
                    });
                });
        });
    }
}