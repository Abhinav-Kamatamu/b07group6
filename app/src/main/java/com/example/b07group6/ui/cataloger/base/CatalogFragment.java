package com.example.b07group6.ui.cataloger.base;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.b07group6.R;
import com.example.b07group6.backend.DatabaseRepository;
import com.example.b07group6.backend.FirebaseDatabaseRepository;
import com.example.b07group6.backend.ImageRepository;
import com.example.b07group6.backend.SupabaseImageRepository;
import com.example.b07group6.construct.Artifact;
import com.example.b07group6.construct.User;
import com.example.b07group6.shared.UserViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Base fragment for displaying a searchable and interactive catalog of artifacts. It is meant to
 * reduce code duplication between the Home Page and the Saved Artifacts page.
 * <p>
 * Subclasses must state which type of artifacts to load by passing in {@link CatalogType} (either
 * all artifacts for {@link CatalogType#HOME} or the current user's saved artifacts for
 * {@link CatalogType#SAVED}), and supply their own layout by defining  {@link #onCreateView}.
 */
public abstract class CatalogFragment extends Fragment {
    private BottomNavigationView bottomNav;
    private View searchBarContainer;

    private List<Artifact> artifactList = new ArrayList<>();
    private final List<Artifact> displayedArtifacts = new ArrayList<>();
    private ArtifactAdapter adapter;

    private RecyclerView recyclerView;
    private EditText searchEditText;
    private ImageView clearButton;
    private OnBackPressedCallback backPressedCallback;
    private UserViewModel userViewModel;
    private FirebaseDatabaseRepository firebase;
    private SupabaseImageRepository supabase;
    private CatalogType ctype;

    /**
     * Specifies which set of artifacts a {@link CatalogFragment} should load and display.
     */
    public enum CatalogType {
        /** Loads all artifacts in the catalog. */
        HOME,
        /** Loads solely the artifacts saved by the current user. */
        SAVED
    }

    /**
     * Creates a new catalog fragment
     * @param ctype an indicator of whether this fragment shows all artifacts or only saved ones
     */
    public CatalogFragment(CatalogType ctype) {
        this.ctype = ctype;
    }

    @Override
    public abstract View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState);

    /**
     * Creates two instances of a {@link CompletableFuture}, one for the retrieval of likes, and
     * one for the retrieval of saved, and adds it to the list of futures to complete for a
     * specific artifact, based on the index of the artifact.
     * @param artifact the current artifact being processed
     * @param index the index of the artifact being processed
     * @param futures the list of all futures to complete
     */
    private void createFutureForArtifact(Artifact artifact, int index, CompletableFuture<Void>[] futures) {
        CompletableFuture<Void> likedFuture = new CompletableFuture<>();
        CompletableFuture<Void> savedFuture = new CompletableFuture<>();
        firebase.getLikeStatus(
                DatabaseRepository.LikeType.ARTIFACT,
                artifact.getLotNumber(),
                userViewModel.getCurrentUser().getUid(),
                new DatabaseRepository.LikeStatusCallback() {
                    @Override
                    public void onSuccess(long likeCount, boolean likedByCurrentUser) {
                        artifact.setLikeCount(likeCount);
                        artifact.setLikedByCurrentUser(likedByCurrentUser);
                        likedFuture.complete(null);
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        likedFuture.completeExceptionally(new Exception(errorMessage));
                    }
                }
        );
        firebase.getNumSaved(artifact.getLotNumber(), userViewModel.getCurrentUser().getUid(),
                new DatabaseRepository.SavedCountCallback() {
                    @Override
                    public void onSuccess(long savedCount, boolean savedByCurrentUser) {
                        artifact.setSavedByCurrentUser(savedByCurrentUser);
                        savedFuture.complete(null);
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        likedFuture.completeExceptionally(new Exception(errorMessage));
                    }
                }
        );
        futures[2 * index] = likedFuture;
        futures[2 * index + 1] = savedFuture;
    }

    /**
     * Assembles the adapter from a list of artifacts
     * @param artifacts the list of artifacts to assemble
     * @param userViewModel the current user view model
     */
    private void assembleAdapter(List<Artifact> artifacts, UserViewModel userViewModel) {
        artifactList = artifacts;
        displayedArtifacts.clear();
        displayedArtifacts.addAll(artifactList);
        // For every artifact, we have to wait for 2 things; The number of likes and the
        // number of saved. Therefore, we need a array of futures that's twice the size of the
        // artifact array. We cannot use a List as CompletableFuture.allOf does not accept it as
        // a parameter. Therefore, we must use an array and manually insert instead of using
        // List.add(...).
        @SuppressWarnings("unchecked")
        CompletableFuture<Void>[] futures = new CompletableFuture[artifactList.size() * 2];
        for (int i = 0; i < artifactList.size(); i++) {
            Artifact artifact = artifactList.get(i);
            createFutureForArtifact(artifact, i, futures);
        }
        CompletableFuture.allOf(futures).thenAccept(result -> {
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            OnArtifactInteractionListener artifactInteractionListener = createInteractionListener();
            adapter = new ArtifactAdapter(displayedArtifacts, artifactInteractionListener);
            recyclerView.setAdapter(adapter);
        }).exceptionally(ex -> {
            Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
            return null;
        });
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get the UserViewModel
        NavBackStackEntry backStackEntry = Navigation.findNavController(view).getBackStackEntry(R.id.navigation_graph);
        userViewModel = new ViewModelProvider(backStackEntry).get(UserViewModel.class);

        firebase = new FirebaseDatabaseRepository();
        supabase = new SupabaseImageRepository(requireContext());

        // Get the bottomNav bar
        bottomNav = requireActivity().findViewById(R.id.bottom_navigation);
        searchBarContainer = view.findViewById(R.id.searchBarContainer);
        searchEditText = view.findViewById(R.id.searchEditText);
        clearButton = view.findViewById(R.id.clearButton);
        recyclerView = view.findViewById(R.id.recyclerView);

        DatabaseRepository.ArtifactListCallback callback = new DatabaseRepository.ArtifactListCallback() {
            @Override
            public void onSuccess(List<Artifact> artifacts) {
                assembleAdapter(artifacts, userViewModel);
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(getContext(), "Failed to load saved artifacts", Toast.LENGTH_SHORT).show();
            }
        };

        // Extract data from database to populate artifactList...
        if (ctype == CatalogType.HOME) {
            firebase.getAllArtifacts(callback);
        } else if (ctype == CatalogType.SAVED) {
            firebase.getSavedArtifactsList(userViewModel.getCurrentUser().getUid(), callback);
        } else {
            throw new IllegalStateException("Catalog type was neither home nor saved");
        }

        generateMenu(userViewModel.getCurrentUser().isAdmin());
        setListeners();

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

    /**
     * Checks whether an artifact matches a search query.
     * @param artifact the artifact to check
     * @param query the search query
     * @return true if the query is empty or found in any of the artifact's fields
     */
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

    /**
     * Recomputes {@link #displayedArtifacts} from {@link #artifactList} based on the
     * current text in the search field, and notifies the adapter of the change.
     * Does nothing if the artifact list or adapter has not been initialized yet.
     * Note that using notifyDataSetChanged is not recommended unless it's absolutely
     * necessary. As such, only use this function if there's no suitable alternative.
     */
    @SuppressLint("NotifyDataSetChanged")
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
        // We kind of have to do this...
        adapter.notifyDataSetChanged();
    }

    /**
     * Sets up listeners for the search bar
     */
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

    /**
     * Brings up the keyboard
     */
    private void showKeyboard() {
        InputMethodManager imm = (InputMethodManager) requireContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(searchEditText, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    /**
     * Hides the keyboard
     */
    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager)
                requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);
        }
    }

    /**
     * Configures the bottom navigation bar for the catalog screen. Adds the option to transition
     * to the add and edit artifact page if and only if the user is an admin.
     */
    private void generateMenu(boolean isAdmin) {
        bottomNav.setVisibility(View.VISIBLE);
        Menu menu = bottomNav.getMenu();
        if (ctype == CatalogType.HOME) {
            menu.findItem(R.id.nav_home).setChecked(true);
        } else if (ctype == CatalogType.SAVED) {
            menu.findItem(R.id.nav_saved).setChecked(true);
        } else {
            throw new IllegalStateException("Catalog type was neither home nor saved");
        }
        if (!isAdmin) {
            menu.removeItem(R.id.nav_add);
        }
    }

    /**
     * Removes the artifact associated with a lot number from both the full artifact list
     * and the currently displayed list, while also notifying the adapter of a removal if the
     * artifact is currently being displayed.
     * @param lotNumber the lot number of the artifact to remove
     */
    private void removeArtifactByLotNumber(String lotNumber) {
        for (int i = 0; i < artifactList.size(); i++) {
            if (artifactList.get(i).getLotNumber().equals(lotNumber)) {
                artifactList.remove(i);
                break;
            }
        }
        // The number of artifacts in the list is not necessarily equal to the
        // number of artifacts being displayed
        for (int i = 0; i < displayedArtifacts.size(); i++) {
            if (displayedArtifacts.get(i).getLotNumber().equals(lotNumber)) {
                displayedArtifacts.remove(i);
                adapter.notifyItemRemoved(i);
                break;
            }
        }
    }

    /**
     * Builds the {@link OnArtifactInteractionListener} used by the adapter to handle
     * interactions with artifact cards
     * @return a listener that handles all artifact card interactions
     */
    private OnArtifactInteractionListener createInteractionListener(){
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
                firebase.toggleSaved(
                        userViewModel.getCurrentUser().getUid(),
                        artifact.getLotNumber(),
                        new DatabaseRepository.SimpleCallback() {
                            @Override
                            public void onSuccess() {
                                if (ctype == CatalogType.SAVED && !isSaved) {
                                    removeArtifactByLotNumber(artifact.getLotNumber());
                                }
                            }

                            @Override
                            public void onFailure(String errorMessage) {
                                Toast.makeText(getContext(), "Action failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                );
            }

            @Override
            public void onLikePress(int position, boolean isLiked) {
                Artifact artifact = artifactList.get(position);
                artifact.setLikedByCurrentUser(isLiked);
                artifact.setLikeCount(artifact.getLikeCount() + (isLiked ? 1 : -1));
                firebase.toggleLike(
                        DatabaseRepository.LikeType.ARTIFACT,
                        artifact.getLotNumber(),
                        userViewModel.getCurrentUser().getUid(),
                        new DatabaseRepository.SimpleCallback() {
                            @Override
                            public void onSuccess() {
                                adapter.notifyItemChanged(position);
                            }

                            @Override
                            public void onFailure(String errorMessage) {
                                artifact.setLikedByCurrentUser(!isLiked);
                                artifact.setLikeCount(artifact.getLikeCount() + (isLiked ? -1 : 1));
                                adapter.notifyItemChanged(position);
                                Toast.makeText(getContext(), "Action Failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                );
            }

            @Override
            public void onItemLongPress(int position) {
                if (!userViewModel.getCurrentUser().isAdmin()) {
                    return;
                }
                Artifact artifact = artifactList.get(position);
                new com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Delete artifact?")
                        .setMessage("\"" + artifact.getArtifactName() + "\" will be permanently removed.")
                        .setNegativeButton("Cancel", null)
                        .setPositiveButton("Delete", createDeleteListener(artifact))
                        .show();
            }
        };
    }

    /**
     * Creates a delete listener for an artifact card
     * @param artifact the artifact to associate with this listener
     * @return a callback
     */
    private DialogInterface.OnClickListener createDeleteListener(Artifact artifact) {
        return (dialog, which) -> {
            firebase.deleteArtifact(artifact.getLotNumber(), new DatabaseRepository.SimpleCallback() {
                @Override
                public void onSuccess() {
                    supabase.deleteImage(artifact.getImageUrl(), new ImageRepository.DeleteCallback() {
                        @Override
                        public void onSuccess() {
                            removeArtifactByLotNumber(artifact.getLotNumber());
                            Toast.makeText(getContext(), "Deleted Successfully", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(String message) {
                            removeArtifactByLotNumber(artifact.getLotNumber());
                            Toast.makeText(getContext(), "Delete partially failed", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                @Override
                public void onFailure(String errorMessage) {
                    Toast.makeText(getContext(), "Delete failed", Toast.LENGTH_SHORT).show();
                }
            });
        };
    }
}