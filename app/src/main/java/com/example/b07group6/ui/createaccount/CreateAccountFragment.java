package com.example.b07group6.ui.createaccount;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.b07group6.R;
import com.example.b07group6.backend.FirebaseAuthOperator;
import com.example.b07group6.shared.UserViewModel;
import com.example.b07group6.construct.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class CreateAccountFragment extends Fragment implements CreateAccountContract.View {
    private CreateAccountContract.Presenter presenter;

    public static CreateAccountFragment newInstance() {
        return new CreateAccountFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        presenter = new CreateAccountPresenter(this, new FirebaseAuthOperator());

        BottomNavigationView bottomNav = requireActivity().findViewById(R.id.bottom_navigation);
        bottomNav.setVisibility(View.GONE);

        EditText usernameField = view.findViewById(R.id.username_field);
        EditText emailField = view.findViewById(R.id.email_field);
        EditText passwordField = view.findViewById(R.id.password_field);
        Button createAccountButton = view.findViewById(R.id.create_account_button);

        createAccountButton.setOnClickListener(v -> presenter.onCreateAccountClicked(
                usernameField.getText().toString(),
                emailField.getText().toString(),
                passwordField.getText().toString()
        ));
    }

    @Override
    public void showError(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void navigateToHome(String uid, String username, String email, boolean isAdmin) {
        NavBackStackEntry backStackEntry = Navigation.findNavController(requireView())
                .getBackStackEntry(R.id.navigation_graph);
        UserViewModel userViewModel = new ViewModelProvider(backStackEntry).get(UserViewModel.class);
        User user = new User(uid, username, email, isAdmin);
        userViewModel.setCurrentUser(user);
        Navigation.findNavController(requireView()).navigate(R.id.action_create_to_home);
    }
}