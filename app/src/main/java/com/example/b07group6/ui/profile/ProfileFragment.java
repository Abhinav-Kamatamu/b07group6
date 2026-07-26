package com.example.b07group6.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.Navigation;

import com.example.b07group6.R;
import com.example.b07group6.construct.User;
import com.example.b07group6.shared.UserViewModel;
import com.google.firebase.auth.FirebaseAuth;

public class ProfileFragment extends Fragment {
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        NavBackStackEntry backStackEntry = Navigation.findNavController(view).getBackStackEntry(R.id.navigation_graph);
        UserViewModel userViewModel = new ViewModelProvider(backStackEntry).get(UserViewModel.class);
        User user = userViewModel.getCurrentUser();

        TextView profileUsername = view.findViewById(R.id.profile_username);
        TextView profileAvatarInitial = view.findViewById(R.id.profile_avatar_initial);
        TextView profileEmail = view.findViewById(R.id.profile_email);
        TextView profileRole = view.findViewById(R.id.profile_role);
        Button logoutButton = view.findViewById(R.id.logout_button);

        profileUsername.setText(user.getUsername());
        profileEmail.setText(user.getEmail());

        String username = user.getUsername();
        profileAvatarInitial.setText(username.isEmpty() ? "?" : username.substring(0, 1).toUpperCase());
        profileRole.setText(user.isAdmin() ? "Administrator" : "User");
        logoutButton.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            userViewModel.logout();
            Navigation.findNavController(view).navigate(R.id.action_profile_to_login);
        });
    }
}
