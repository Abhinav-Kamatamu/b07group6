package com.example.b07group6.ui.login;


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
import android.widget.TextView;
import android.widget.Toast;

import com.example.b07group6.R;
import com.example.b07group6.backend.FirebaseAuthOperator;
import com.example.b07group6.construct.User;
import com.example.b07group6.shared.UserViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class LoginFragment extends Fragment implements LoginContract.View {
    private LoginContract.Presenter presenter;
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
        presenter = new LoginPresenter(this, new FirebaseAuthOperator());

        EditText emailField = view.findViewById(R.id.email_field);
        EditText passwordField = view.findViewById(R.id.password_field);
        Button loginButton = view.findViewById(R.id.login_button);
        TextView newAccountView = view.findViewById(R.id.create_account_view);

        loginButton.setOnClickListener(v ->
                presenter.onLoginClicked(emailField.getText().toString(), passwordField.getText().toString())
        );
        newAccountView.setOnClickListener(
                v -> Navigation.findNavController(view).navigate(R.id.action_create_account)
        );

        BottomNavigationView bottomNav = requireActivity().findViewById(R.id.bottom_navigation);
        bottomNav.setVisibility(View.GONE);
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
        Navigation.findNavController(requireView()).navigate(R.id.action_login_to_home);
    }

}