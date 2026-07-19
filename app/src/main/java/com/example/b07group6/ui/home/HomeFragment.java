package com.example.b07group6.ui.home;

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
import com.example.b07group6.shared.UserViewModel;
import com.example.b07group6.construct.User;

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

        // Get the UserViewModel
        NavBackStackEntry backStackEntry = Navigation.findNavController(view).getBackStackEntry(R.id.navigation_graph);
        UserViewModel userViewModel = new ViewModelProvider(backStackEntry).get(UserViewModel.class);

        EditText lotNumberField = view.findViewById(R.id.lot_number_field);
        TextView usernameDisplay = view.findViewById(R.id.display_username);
        TextView emailDisplay = view.findViewById(R.id.display_email);
        TextView idDisplay = view.findViewById(R.id.display_id);
        Button button = view.findViewById(R.id.go_to_add_edit_artifact_button);
        User user = userViewModel.getCurrentUser();
        usernameDisplay.setText("Username: " + user.getUsername());
        emailDisplay.setText("Email: " + user.getEmail());
        idDisplay.setText("ID: " + user.getUid());
        button.setOnClickListener(v -> {
            Toast.makeText(getContext(), "BFI", Toast.LENGTH_SHORT).show();
            String lotNumber = lotNumberField.getText().toString();
            if (!lotNumber.isBlank()) {
                userViewModel.setArtifactEditingLotNumber(lotNumber);
            }
            Navigation.findNavController(requireView()).navigate(R.id.action_home_to_add_edit_artifact);
        });
    }
}