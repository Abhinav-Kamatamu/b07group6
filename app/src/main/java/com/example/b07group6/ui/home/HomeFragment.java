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
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.b07group6.R;
import com.example.b07group6.shared.UserViewModel;
import com.example.b07group6.construct.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.search.SearchBar;

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

        // Get the bottomNav bar
        bottomNav = view.findViewById(R.id.bottom_navigation);


        User user = userViewModel.getCurrentUser();

        generateMenu(user.isAdmin()); // Adjust based on if admin


        EditText searchEditText = view.findViewById(R.id.searchEditText);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString();
                // Handle search query changes here
            }

            @Override
            public void afterTextChanged(Editable s) {
                Toast.makeText(getContext(), s, Toast.LENGTH_LONG).show();
            }
        });
    }


    private void generateMenu(boolean isAdmin){
        Menu menu = bottomNav.getMenu();

        if(!isAdmin){
            menu.removeItem(R.id.nav_add);
        }
    }
}

/*

// 1. Get reference to your SearchView
android.widget.SearchView searchView = view.findViewById(R.id.searchView);

// 2. Make the entire circular bar clickable to open typing mode
        searchView.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        searchView.setIconified(false); // Expands the search view and brings up keyboard
    }
});


// 3. Handle the back press behavior (requires AndroidX AppCompatActivity)
// 3. Handle the back press behavior

requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
    @Override
    public void handleOnBackPressed() {
        View rootLayout = view.findViewById(R.id.coordinator_layout);

        // 1. Check if the keyboard is actually open/visible on screen
        boolean isKeyboardOpen = androidx.core.view.WindowInsetsCompat.toWindowInsetsCompat(
                requireActivity().getWindow().getDecorView().getRootWindowInsets()
        ).isVisible(androidx.core.view.WindowInsetsCompat.Type.ime());

        if (searchView.hasFocus() && isKeyboardOpen) {
            // PRESS 1: Keyboard is open. Close it, but keep the cursor.
            android.view.inputmethod.InputMethodManager imm = (android.view.inputmethod.InputMethodManager)
                    requireContext().getSystemService(android.content.Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
            }
        }
        else if (searchView.hasFocus()) {
            // PRESS 2: Keyboard is already closed, but cursor is still blinking.
            // Move focus to the root layout to strip the cursor, keeping the text intact.
            if (rootLayout != null) {
                rootLayout.setFocusableInTouchMode(true);
                rootLayout.requestFocus();
            }
            searchView.clearFocus();
        }
        else {
            // PRESS 3: Search bar has no focus/cursor. Clean exit.
            setEnabled(false);
            requireActivity().getOnBackPressedDispatcher().onBackPressed();
            setEnabled(true);
        }
    }
});

 */