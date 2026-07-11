package com.example.b07group6.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.b07group6.R;
import com.example.b07group6.shared.AuthUserModel;
import com.example.b07group6.shared.User;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;

import java.util.concurrent.ExecutionException;


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

        // Get our models
        LoginViewModel loginModel = new ViewModelProvider(this).get(LoginViewModel.class);
        AuthUserModel authViewModel = new ViewModelProvider(requireActivity()).get(AuthUserModel.class);

        // Find our fields
        EditText emailField = view.findViewById(R.id.username_field);
        EditText usernameField = view.findViewById(R.id.email_field);
        EditText passwordField = view.findViewById(R.id.password_field);
        Button createAccountButton = view.findViewById(R.id.create_account_button);

        // Listen for clicks on the button
        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameField.getText().toString();
                String email = emailField.getText().toString();
                String password = passwordField.getText().toString();
                FirebaseAuth auth = loginModel.getAuth();
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener((authTask) -> {
                    if (!authTask.isSuccessful()) {
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
                        User user = new User(
                            firebaseUser.getUid(),
                            username,
                            email,
                            tokenTask.getResult().getToken(),
                            false
                        );
                        authViewModel.loginUser(user);
                        Navigation.findNavController(view).navigate(R.id.action_login_to_main);
                    });
                });
            }
        });
    }
}