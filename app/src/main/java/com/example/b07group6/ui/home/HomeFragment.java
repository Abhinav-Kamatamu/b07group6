package com.example.b07group6.ui.home;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
    private View searchBarContainer;
    private EditText searchEditText;
    private ImageView clearButton;
    private ImageView searchIcon;
    private OnBackPressedCallback backPressedCallback;

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
        bottomNav = requireActivity().findViewById(R.id.bottom_navigation);
        searchBarContainer = view.findViewById(R.id.searchBarContainer);
        searchEditText = view.findViewById(R.id.searchEditText);
        clearButton = view.findViewById(R.id.clearButton);
        searchIcon = view.findViewById(R.id.searchIcon);


        User user = userViewModel.getCurrentUser();

        generateMenu(true || user.isAdmin()); // Adjust based on if admin


        setListeners();

        // Scroll Space Adder has been disaled becasue we changed to Reletive Layout in activity_main.xml
//        scrollSpaceAdder(view);


        // Handling Back Presses:
        backPressedCallback = new OnBackPressedCallback(false) {
            @Override
            public void handleOnBackPressed() {
                searchEditText.getText().clear();
                searchEditText.clearFocus();
                clearButton.setVisibility(View.GONE);
                hideKeyboard();
                setEnabled(false);
            }
        };

        requireActivity().getOnBackPressedDispatcher()
                .addCallback(getViewLifecycleOwner(), backPressedCallback);
    }


    private void setListeners(){

        clearButton.setVisibility(View.GONE);

        searchBarContainer.setOnClickListener(v -> {
            searchEditText.requestFocus();
            clearButton.setVisibility(View.VISIBLE);
        });


        searchEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    clearButton.setVisibility(View.VISIBLE);
                    bottomNav.setVisibility(View.GONE);
                    backPressedCallback.setEnabled(true);
                }else{
                    clearButton.setVisibility(View.GONE);
                    bottomNav.setVisibility(View.VISIBLE);
                    backPressedCallback.setEnabled(false);
                    hideKeyboard();
                }
            }
        });
        searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || // This is code to get enter press from screen keyboard
                        (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && // This is for external keyboard enter presses
                                event.getAction() == KeyEvent.ACTION_DOWN)) {
                    // ---- INSERT CODE TO HANDLE TEXT ACTION ------
                    // (Basically our Recycle Viewer)

                    searchEditText.clearFocus();
                    hideKeyboard();
                    return true;
                }
                return false;
            }
        });

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(searchEditText.getText().toString().isEmpty()){
                    searchEditText.clearFocus();
                    clearButton.setVisibility(View.GONE);
                    return;
                }
                searchEditText.getText().clear();
            }
        });
    }
    private void showKeyboard() {
        InputMethodManager imm = (InputMethodManager)requireContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        if (imm != null) {
            imm.showSoftInput(searchEditText, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager)
                requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);
        }
    }


    private void generateMenu(boolean isAdmin){
        bottomNav.setVisibility(View.VISIBLE);
        Menu menu = bottomNav.getMenu();
        menu.findItem(R.id.nav_home).setChecked(true);

        if(!isAdmin){
            menu.removeItem(R.id.nav_add);
        }

//        bottomNav.post(() -> bottomNav.setSelectedItemId(R.id.nav_home));
//              |--> This line causes errors because of infinite loop of calling the homepage which is bad.
    }

    private void scrollSpaceAdder(View view){
        NestedScrollView scrollView = view.findViewById(R.id.homeScrollView);

        bottomNav.addOnLayoutChangeListener((v, l, t, r, b, ol, ot, or, ob) -> {
            int navHeight = bottomNav.getHeight();
            if (scrollView.getPaddingBottom() != navHeight) {
                scrollView.setPadding(
                        scrollView.getPaddingLeft(),
                        scrollView.getPaddingTop(),
                        scrollView.getPaddingRight(),
                        navHeight
                );
            }
        });
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