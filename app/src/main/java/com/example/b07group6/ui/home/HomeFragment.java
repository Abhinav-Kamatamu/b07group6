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
import android.widget.TextView;
import android.widget.Toast;

import com.example.b07group6.R;
import com.example.b07group6.backend.DatabaseRepository;
import com.example.b07group6.backend.FirebaseDatabaseRepository;
import com.example.b07group6.backend.ImageRepository;
import com.example.b07group6.backend.SupabaseImageRepository;
import com.example.b07group6.construct.Artifact;
import com.example.b07group6.shared.UserViewModel;
import com.example.b07group6.construct.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private BottomNavigationView bottomNav;
    private View searchBarContainer;

    private List<Artifact> artifactList = new ArrayList<>();
    private final List<Artifact> displayedArtifacts = new ArrayList<>();
    private ArtifactAdapter adapter;

    private RecyclerView recyclerView;
    private EditText searchEditText;
    private ImageView clearButton;
    private ImageView searchIcon;
    private OnBackPressedCallback backPressedCallback;
    private User user;
    private FirebaseDatabaseRepository firebase;
    private SupabaseImageRepository supabase;


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

        firebase = new FirebaseDatabaseRepository();
        supabase = new SupabaseImageRepository(requireContext());

        // Get the bottomNav bar
        bottomNav = requireActivity().findViewById(R.id.bottom_navigation);
        searchBarContainer = view.findViewById(R.id.searchBarContainer);
        searchEditText = view.findViewById(R.id.searchEditText);
        clearButton = view.findViewById(R.id.clearButton);
        searchIcon = view.findViewById(R.id.searchIcon);
        recyclerView = view.findViewById(R.id.recyclerView);
        user = userViewModel.getCurrentUser();


        // Extract data from database to populate artifactList...
        firebase.getAllArtifacts(new DatabaseRepository.ArtifactListCallback() {
            @Override
            public void onSuccess(List<Artifact> artifacts) {
                artifactList = artifacts;

                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                OnArtifactInteractionListener artifactInteractionListener = createInteractionListener(userViewModel, artifactList);
                adapter = new ArtifactAdapter(artifactList, artifactInteractionListener);

                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(getContext(), "Artifact fetch failed", Toast.LENGTH_SHORT);
            }
        });



        generateMenu(user.isAdmin()); // Adjust based on if admin


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

    private boolean matchesQuery(Artifact artifact, String query) {
        if (query.isEmpty()) {
            return true;
        }
        String[] fields = {
                artifact.getLotNumber(),
                artifact.getArtifactName(),
                artifact.getDescription(),
                artifact.getCategory(),
                artifact.getMaterial(),
                artifact.getDynastyPeriod(),
                artifact.getCulturalOrigin(),
                artifact.getDimensions(),
                artifact.getConditionReport(),
                artifact.getCurrentLocation(),
                artifact.getAcquisitionMethod(),
                artifact.getProvenance(),
                artifact.getAccessionNumber(),
                artifact.getNotes()
        };
        for (String field : fields) {
            if (field != null && field.toLowerCase().contains(query.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    private void refreshDisplayedList() {
        if(artifactList == null || adapter == null)
            return;
        String query = searchEditText.getText().toString().trim();
        displayedArtifacts.clear();
        for (Artifact artifact : artifactList) {
            if (matchesQuery(artifact, query)) {
                displayedArtifacts.add(artifact);
            }
        }
        adapter.notifyDataSetChanged();
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
                    backPressedCallback.setEnabled(true);
                } else {
                    clearButton.setVisibility(View.GONE);
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


        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                refreshDisplayedList();
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
    }

    private OnArtifactInteractionListener createInteractionListener(UserViewModel userViewModel, List<Artifact> artifactList){
        return new OnArtifactInteractionListener() {
            @Override
            public void onSingleClick(int position) {
                // Write code to navigate to extended artifact view page for this artifact
                Artifact artifact  = artifactList.get(position);
                userViewModel.setExtendedLotNumber(artifact.getLotNumber());
                Navigation.findNavController(requireView()).navigate(R.id.action_global_extended);

            }

            @Override
            public void onSaveArifactPress(int position, boolean isSaved) {
                // Write code that handles the bookmarking feature for this artifact
                Artifact artifact = artifactList.get(position);
                firebase.toggleSaved(user.getUid(), artifact.getLotNumber(), new DatabaseRepository.SimpleCallback() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(getContext(), isSaved? "Saved" : "UnSaved" , Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        Toast.makeText(getContext(), "Action failed", Toast.LENGTH_SHORT).show();
                    }
                });

            }

            @Override
            public void onItemLongPress(int position) {
                if (!user.isAdmin()){
                    return;
                }
                Artifact artifact = artifactList.get(position);
                new com.google.android.material.dialog.MaterialAlertDialogBuilder(getContext())
                        .setTitle("Delete artifact?")
                        .setMessage("\"" + artifact.getArtifactName() + "\" will be permanently removed.")
                        .setNegativeButton("Cancel", null)
                        .setPositiveButton("Delete", (dialog, which) -> {
                            // first delete the artifact data
                            firebase.deleteArtifact(artifact.getLotNumber(), new DatabaseRepository.SimpleCallback() {
                                @Override
                                public void onSuccess() {
                                    // then attempt to delete the image.
                                    supabase.deleteImage(artifact.getImageUrl(), new ImageRepository.DeleteCallback() {
                                        @Override
                                        public void onSuccess() {
                                            Toast.makeText(getContext(), "Deleted Succesfully", Toast.LENGTH_SHORT).show();
                                            artifactList.remove(position);
                                            adapter.notifyDataSetChanged();
                                        }

                                        @Override
                                        public void onError(String message) {
                                            Toast.makeText(getContext(), "Delete partially failed", Toast.LENGTH_SHORT).show();
                                            artifactList.remove(position);
                                            adapter.notifyDataSetChanged();
                                        }
                                    });
                                }
                                @Override
                                public void onFailure(String errorMessage) {
                                    Toast.makeText(getContext(), "Delete failed", Toast.LENGTH_SHORT).show();
                                }
                            });
                        })
                        .show();
            }
        };
    }
}