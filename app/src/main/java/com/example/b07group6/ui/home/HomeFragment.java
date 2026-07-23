package com.example.b07group6.ui.home;

import android.app.AlertDialog;
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
import android.widget.ToggleButton;

import com.example.b07group6.MainActivity;
import com.example.b07group6.R;
import com.example.b07group6.backend.DatabaseRepository;
import com.example.b07group6.backend.FirebaseDatabaseRepository;
import com.example.b07group6.construct.Artifact;
import com.example.b07group6.shared.UserViewModel;
import com.example.b07group6.construct.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.search.SearchBar;

import java.util.List;

public class HomeFragment extends Fragment {
    private BottomNavigationView bottomNav;
    private View searchBarContainer;

    private List<Artifact> artifactList;
    private RecyclerView recyclerView;
    private EditText searchEditText;
    private ImageView clearButton;
    private ImageView searchIcon;
    private OnBackPressedCallback backPressedCallback;

    public HomeFragment() {
    }

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
        recyclerView = view.findViewById(R.id.recyclerView);

        // Extract data from database to populate artifactList...
        FirebaseDatabaseRepository firebase = new FirebaseDatabaseRepository();

        Log.d("FIREBASE STATUSUSUUSUSU", "Firebase is " + ((Boolean)(firebase == null)));
        firebase.getAllArtifacts(new DatabaseRepository.ArtifactListCallback() {
            @Override
            public void onSuccess(List<Artifact> artifacts) {
                Log.d("Reached", "This piont in code");
                artifactList = artifacts;

                for (int i = 0; i < artifactList.size(); i++) {
                    Log.d("Artifact Recieved: ", "Name: "+ artifactList.get(i).getArtifactName());
                }
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                ArtifactAdapter adapter = new ArtifactAdapter(artifactList, new OnArtifactInteractionListener() {
                    @Override
                    public void onSingleClick(int position) {
                        // Write code to navigate to extended artifact view page for this artifact
                    }

                    @Override
                    public void onSaveArifactPress(int position) {
                        // Write code that handles the bookmarking feature for this artifact

                    }

                    @Override
                    public void onItemLongPress(int position) {
                        // Write code that creates a highlight and asks for delete.
                        // If you choose to delete, then handle the delete too...
                        // You also need to handle the code to de-select the delete if you press elsewhere.
                        // This feature should only work for admins too...

                        // Here is temp alert box that does the job for now... (not verifying admin)
                        Artifact artifact = artifactList.get(position);
                        new AlertDialog.Builder(getContext())
                                .setTitle(artifact.getArtifactName())
                                .setItems(new String[]{"Cancel", "Delete"}, (dialog, which) -> {
                                    switch (which) {
                                        case 0: /* cancel */
                                            break;
                                        case 1:
                                            artifactList.remove(position);
                                            // Write code here to handle the artifact deletion in database
                                            break;
                                    }
                                })
                                .show();
                    }
                });

                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(getContext(), "Artifact fetch failed", Toast.LENGTH_SHORT);
                Log.d("OMG OMG OMG OMG", "SHIT FIREBASE FAILED!" + errorMessage);
            }
        });



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


    private void setListeners() {

        clearButton.setVisibility(View.GONE);

        searchBarContainer.setOnClickListener(v -> {
            searchEditText.requestFocus();
            clearButton.setVisibility(View.VISIBLE);
        });


        searchEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    clearButton.setVisibility(View.VISIBLE);
                    bottomNav.setVisibility(View.GONE);
                    backPressedCallback.setEnabled(true);
                } else {
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
                if (searchEditText.getText().toString().isEmpty()) {
                    searchEditText.clearFocus();
                    clearButton.setVisibility(View.GONE);
                    return;
                }
                searchEditText.getText().clear();
            }
        });
    }

    private void showKeyboard() {
        InputMethodManager imm = (InputMethodManager) requireContext()
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


    private void generateMenu(boolean isAdmin) {
        bottomNav.setVisibility(View.VISIBLE);
        Menu menu = bottomNav.getMenu();
        menu.findItem(R.id.nav_home).setChecked(true);

        if (!isAdmin) {
            menu.removeItem(R.id.nav_add);
        }

//        bottomNav.post(() -> bottomNav.setSelectedItemId(R.id.nav_home));
//              |--> This line causes errors because of infinite loop of calling the homepage which is bad.
    }

    // The following function is pretty much not needed anymore
//    private void scrollSpaceAdder(View view){
//        NestedScrollView scrollView = view.findViewById(R.id.homeScrollView);
//
//        bottomNav.addOnLayoutChangeListener((v, l, t, r, b, ol, ot, or, ob) -> {
//            int navHeight = bottomNav.getHeight();
//            if (scrollView.getPaddingBottom() != navHeight) {
//                scrollView.setPadding(
//                        scrollView.getPaddingLeft(),
//                        scrollView.getPaddingTop(),
//                        scrollView.getPaddingRight(),
//                        navHeight
//                );
//            }
//        });
//    }

}