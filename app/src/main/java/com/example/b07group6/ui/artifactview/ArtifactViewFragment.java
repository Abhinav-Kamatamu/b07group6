package com.example.b07group6.ui.artifactview;

import android.os.Bundle;
import android.text.TextUtils;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import com.example.b07group6.R;
import com.example.b07group6.backend.DatabaseRepository;
import com.example.b07group6.backend.FirebaseDatabaseRepository;
import com.example.b07group6.backend.SupabaseImageRepository;
import com.example.b07group6.construct.Artifact;
import com.example.b07group6.construct.Comment;
import com.example.b07group6.shared.UserViewModel;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

import com.example.b07group6.R;

import java.util.ArrayList;

public class ArtifactViewFragment extends Fragment {

    private NestedScrollView nestedScrollView;
    private ConstraintLayout rootLayout;
    private ImageView artifactImage;
    private TextView artifactName;
    private TextView artifactCategory;
    private TextView artifactMaterial;
    private TextView artifactDynastyPeriod;
    private TextView descriptionText;
    private TextView viewMoreButton;
    private CheckBox likeButton;
    private CheckBox saveButton;
    private Button commentsButton;
    private TextView commentsHeader;
    private EditText commentsText;
    private MaterialButton commentSubmitButton;
    private RecyclerView recyclerView;

    private boolean isDescriptionExpanded = false;
    private UserViewModel userViewModel;
    private Artifact artifact;
    private FirebaseDatabaseRepository database;

    List<Comment> commentList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_extended_artifact_view, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        nestedScrollView = view.findViewById(R.id.nestedScrollView);
        rootLayout = view.findViewById(R.id.rootConstraintLayout);
        artifactImage = view.findViewById(R.id.artifactImage);
        artifactName = view.findViewById(R.id.artifactName);
        artifactCategory = view.findViewById(R.id.artifactCategory);
        artifactMaterial = view.findViewById(R.id.artifactMaterial);
        artifactDynastyPeriod = view.findViewById(R.id.artifactDynastyPeriod);
        likeButton = view.findViewById(R.id.likesButtonAndCount);
        saveButton = view.findViewById(R.id.savesButtonAndCount);
        commentsButton = view.findViewById(R.id.commentsButtonAndCount);
        commentsHeader = view.findViewById(R.id.commentsHeader);
        descriptionText = view.findViewById(R.id.descriptionText);
        viewMoreButton = view.findViewById(R.id.viewMoreButton);
        commentsText = view.findViewById(R.id.commentEditText);
        commentSubmitButton = view.findViewById(R.id.submitCommentButton);
        recyclerView = view.findViewById(R.id.recyclerView);

        // Get the UserViewModel
        NavBackStackEntry backStackEntry = Navigation.findNavController(view).getBackStackEntry(R.id.navigation_graph);
        userViewModel = new ViewModelProvider(backStackEntry).get(UserViewModel.class);
        FirebaseDatabaseRepository databaseRepository = new FirebaseDatabaseRepository();
        SupabaseImageRepository imageUploader = new SupabaseImageRepository(requireContext());
        String lotNumber = userViewModel.getExtendedLotNumber();

        // check so that checking database won't crash application
        if (lotNumber == null) {
            Toast.makeText(getContext(), "No Artifact Lot Number Found", Toast.LENGTH_SHORT).show();
            navigateToHome();
            return;
        }

        databaseRepository.getArtifact(lotNumber, new DatabaseRepository.ArtifactCallback() {
            @Override
            public void onSuccess(Artifact fetchedArtifact) {
                artifact = fetchedArtifact;
                // set all text and image fields
                setFields(fetchedArtifact, databaseRepository, lotNumber);
                recyclerView.setAdapter(new CommentAdapter(getContext(), commentList));
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            }
            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(getContext(), "Could not fetch Artifact: " + errorMessage, Toast.LENGTH_SHORT).show();
                navigateToHome();
            }
        });

        viewMoreButton.setOnClickListener(v -> toggleDescription());
        likeButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Toast.makeText(getContext(), "Liked", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Unliked", Toast.LENGTH_SHORT).show();
                }
            }
        });

        saveButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Toast.makeText(getContext(), "Saved", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Unsaved", Toast.LENGTH_SHORT).show();
                }
            }
        });

        commentsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int y = commentsHeader.getTop();
                nestedScrollView.smoothScrollTo(0, y);
            }
        });

        commentSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = commentsText.getText().toString();
                if (!input.isBlank()) {
                    databaseRepository.addComment(userViewModel.getExtendedLotNumber(), input,
                            userViewModel.getCurrentUser().getUsername(),
                            userViewModel.getCurrentUser().getUid(),
                            new DatabaseRepository.SimpleCallback() {
                                @Override
                                public void onSuccess() {
                                    commentsText.setText("");
                                }

                                @Override
                                public void onFailure(String errorMessage) {
                                    Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });
    }

    public void navigateToHome() {
        userViewModel.setExtendedLotNumber(null);
        Navigation.findNavController(requireView()).navigate(R.id.action_extended_artifact_to_home);
    }

    private void toggleDescription() {
        isDescriptionExpanded = !isDescriptionExpanded;

        // Animates the layout change that happens right after this call
        TransitionManager.beginDelayedTransition(rootLayout);

        if (isDescriptionExpanded) {
            descriptionText.setMaxLines(Integer.MAX_VALUE);
            descriptionText.setEllipsize(null);
            viewMoreButton.setText("View less");
        } else {
            // change this number to decide how many lines you want to show originally
            descriptionText.setMaxLines(4);
            descriptionText.setEllipsize(TextUtils.TruncateAt.END);
            viewMoreButton.setText("View more");
        }

        TransitionManager.endTransitions(rootLayout);
    }

    public void setFields(Artifact artifact, FirebaseDatabaseRepository databaseRepository, String lotNumber) {
        artifactName.setText(artifact.getArtifactName());
        artifactCategory.setText(artifact.getCategory());
        artifactMaterial.setText(artifact.getMaterial());
        artifactDynastyPeriod.setText(artifact.getDynastyPeriod());
        Glide.with(getContext()).load(artifact.getImageUrl()).error(R.drawable.ic_launcher_background).into(artifactImage);
        descriptionText.setText(artifact.getDescription());
        databaseRepository.getAllComments(lotNumber, new DatabaseRepository.CommentListCallback() {
            @Override
            public void onSuccess(List<Comment> comments) {
                commentList = comments;
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(getContext(), "Failed to fetch comments: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
}