package com.example.b07group6.ui.home;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.Navigation;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.b07group6.R;
import com.example.b07group6.shared.UserViewModel;
import com.example.b07group6.construct.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.search.SearchBar;
import com.google.android.material.search.SearchView;

public class HomeFragment extends Fragment {
    private BottomNavigationView bottomNav;
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


        User user = userViewModel.getCurrentUser();

        generateMenu(view, true);

        SearchView searchView = view.findViewById(R.id.searchView);
        BottomNavigationView bottomNav = view.findViewById(R.id.bottom_navigation);
        SearchBar searchBar = view.findViewById(R.id.searchBar);


        searchView.getEditText().setOnEditorActionListener((textView, actionId, event) -> {
            String query = textView.getText().toString();
            // Handle search action
            Toast.makeText(getContext(), "Search: " + query, Toast.LENGTH_SHORT).show();
            searchView.hide();
            searchBar.setText(query);
            return false;
        });
        searchView.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString();
                // Filter or update results as user types
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        searchBar.setOnClickListener(v -> {
            searchView.show();
        });

        searchView.addTransitionListener(new SearchView.TransitionListener() {
            @Override
            public void onStateChanged(
                    SearchView searchView,
                    SearchView.TransitionState previousState,
                    SearchView.TransitionState newState) {
                if (newState == SearchView.TransitionState.SHOWING) {
                    bottomNav.setVisibility(View.GONE);
                } else if (newState == SearchView.TransitionState.HIDDEN) {
                    bottomNav.setVisibility(View.VISIBLE);
                }
            }
        });
    }


    private void generateMenu(View view, boolean isAdmin){
        bottomNav = view.findViewById(R.id.bottom_navigation);

        Menu menu = bottomNav.getMenu();
        //menu.clear(); // Apparently convention to clear existing items

        if(isAdmin){
//            menu.add(0, R.id.nav_profile, 0, "")
//                    .setIcon(R.drawable.profile_logo);
        }
    }
}