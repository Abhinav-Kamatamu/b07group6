package com.example.b07group6.ui.artifactview;

import static android.view.View.GONE;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.transition.TransitionManager;
import android.util.Log;
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
import com.example.b07group6.construct.User;
import com.example.b07group6.shared.UserViewModel;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

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
    private TextView artifactMetadata;
    private TextView artifactCulturalOrigin;
    private TextView artifactDimensions;
    private TextView artifactConditionReport;
    private TextView artifactCurrentLocation;
    private TextView artifactAcquisitionMethod;
    private TextView artifactProvenance;
    private TextView artifactAccesionMethod;
    private TextView artifactNotes;
    private TextView commentsHeader;
    private EditText commentsText;
    private MaterialButton commentSubmitButton;
    private RecyclerView recyclerView;

    private boolean isDescriptionExpanded = false;
    private UserViewModel userViewModel;
    private FirebaseDatabaseRepository databaseRepository;
    private CompoundButton.OnCheckedChangeListener saveButtonListener;
    private CompoundButton.OnCheckedChangeListener likeButtonListener;

    List<Comment> commentList;

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
        artifactMetadata = view.findViewById(R.id.artifactMetadata);
        artifactCulturalOrigin = view.findViewById(R.id.artifactCulturalOrigin);
        artifactDimensions = view.findViewById(R.id.artifactDimensions);
        artifactConditionReport = view.findViewById(R.id.artifactConditionReport);
        artifactCurrentLocation = view.findViewById(R.id.artifactCurrentLocation);
        artifactAcquisitionMethod = view.findViewById(R.id.artifactAcquisitionMethod);
        artifactProvenance = view.findViewById(R.id.artifactProvenance);
        artifactAccesionMethod = view.findViewById(R.id.artifactAccesionNumber);
        artifactNotes = view.findViewById(R.id.artifactNotes);
        commentsText = view.findViewById(R.id.commentEditText);
        commentSubmitButton = view.findViewById(R.id.submitCommentButton);
        recyclerView = view.findViewById(R.id.recyclerView);

        // Get the UserViewModel
        NavBackStackEntry backStackEntry = Navigation.findNavController(view).getBackStackEntry(R.id.navigation_graph);
        userViewModel = new ViewModelProvider(backStackEntry).get(UserViewModel.class);
        databaseRepository = new FirebaseDatabaseRepository();
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
                // set all text and image fields
                setFields(fetchedArtifact, databaseRepository, lotNumber);
            }
            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(getContext(), "Could not fetch Artifact: " + errorMessage, Toast.LENGTH_SHORT).show();
                navigateToHome();
            }
        });

        viewMoreButton.setOnClickListener(v -> toggleDescription());

        // must define so that we can turn on and off listener when updating isChecked
        likeButtonListener = (buttonView, isChecked) -> databaseRepository.toggleLike(
                DatabaseRepository.LikeType.ARTIFACT,
                lotNumber,
                userViewModel.getCurrentUser().getUid(),
                new DatabaseRepository.SimpleCallback() {
                    @Override
                    public void onSuccess() {
                        getArtifactLikeCount(lotNumber, userViewModel.getCurrentUser().getUid());
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        Toast.makeText(getContext(), "Failed to toggle like: " + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
        likeButton.setOnCheckedChangeListener(likeButtonListener);


        saveButtonListener = (buttonView, isChecked) ->
                databaseRepository.toggleSaved(
                userViewModel.getCurrentUser().getUid(),
                lotNumber,
                new DatabaseRepository.SimpleCallback() {
                    @Override
                    public void onSuccess() {
                        getArtifactSaveCount(lotNumber, userViewModel.getCurrentUser().getUid());
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        Toast.makeText(getContext(), "Failed to toggle like: " + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
        saveButton.setOnCheckedChangeListener(saveButtonListener);

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
                    databaseRepository.addComment(
                            lotNumber,
                            input,
                            userViewModel.getCurrentUser().getUsername(),
                            userViewModel.getCurrentUser().getUid(),
                            new DatabaseRepository.SimpleCallback() {
                                @Override
                                public void onSuccess() {
                                    commentsText.setText("");
                                    updateComments(lotNumber);
                                    Toast.makeText(getContext(), "Comment Posted!", Toast.LENGTH_SHORT).show();
                                }
                                @Override
                                public void onFailure(String errorMessage) {
                                    Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    Toast.makeText(getContext(), "Field cannot be empty", Toast.LENGTH_SHORT).show();
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

        // if checks for all optional artifact fields (is there a better way?)
        int counter = 0;    // if all optional fields are empty, hide entire section
        if (artifact.getCulturalOrigin() == null || artifact.getCulturalOrigin().trim().equals("")) {
            artifactCulturalOrigin.setVisibility(GONE);
            counter += 1;
        } else {
            artifactCulturalOrigin.setText("Cultural Origins: " + artifact.getCulturalOrigin());
        }
        if (artifact.getDimensions() == null || artifact.getDimensions().trim().equals("")) {
            artifactDimensions.setVisibility(GONE);
            counter += 1;
        } else {
            artifactDimensions.setText("Dimensions: " + artifact.getDimensions());
        }
        if (artifact.getConditionReport() == null || artifact.getConditionReport().trim().equals("")) {
            artifactConditionReport.setVisibility(GONE);
            counter += 1;
        } else {
            artifactConditionReport.setText("Condition: " + artifact.getConditionReport());
        }
        if (artifact.getCurrentLocation() == null || artifact.getCurrentLocation().trim().equals("")) {
            artifactCurrentLocation.setVisibility(GONE);
            counter += 1;
        } else {
            artifactCurrentLocation.setText("Current Location: " + artifact.getCurrentLocation());
        }
        if (artifact.getAcquisitionMethod() == null || artifact.getAcquisitionMethod().trim().equals("")) {
            artifactAcquisitionMethod.setVisibility(GONE);
            counter += 1;
        } else {
            artifactAcquisitionMethod.setText("Acquisition Method: " + artifact.getAcquisitionMethod());
        }
        if (artifact.getProvenance() == null || artifact.getProvenance().trim().equals("")) {
            artifactProvenance.setVisibility(GONE);
            counter += 1;
        } else {
            artifactProvenance.setText("Provenance: " + artifact.getProvenance());
        }
        if (artifact.getAccessionNumber() == null || artifact.getAccessionNumber().trim().equals("")) {
            artifactAccesionMethod.setVisibility(GONE);
            counter += 1;
        } else {
            artifactAccesionMethod.setText("Accesion Method: " + artifact.getAccessionNumber());
        }
        if (artifact.getNotes() == null || artifact.getNotes().trim().equals("")) {
            artifactNotes.setVisibility(GONE);
            counter += 1;
        } else {
            artifactNotes.setText("Other Notes: " + artifact.getNotes());
        }

        if (counter == 8) {
            artifactMetadata.setVisibility(GONE);
        }

        getArtifactLikeCount(lotNumber, userViewModel.getCurrentUser().getUid());
        getArtifactSaveCount(lotNumber, userViewModel.getCurrentUser().getUid());
        databaseRepository.getAllComments(lotNumber, new DatabaseRepository.CommentListCallback() {
            @Override
            public void onSuccess(List<Comment> comments) {
                commentList = new ArrayList<>(comments); // need array list to delete comments (mutable)
                CommentAdapter adapter = new CommentAdapter(getContext(),
                        commentList,
                        new CommentAdapter.onCommentLongClickListener() {
                            @Override
                            public void onLongClick(int position) {
                                if (!userViewModel.getCurrentUser().isAdmin()) {
                                    return;
                                }
                                showDeleteAlertDialog(position, lotNumber);
                            }
                        });
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                if (recyclerView.getAdapter() != null) {
                    commentsButton.setText(String.valueOf(recyclerView.getAdapter().getItemCount()));
                } else {
                    commentsButton.setText("0");
                }
            }
            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(getContext(), "Failed to fetch comments: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getArtifactLikeCount(String lotNumber, String uid) {
        databaseRepository.getLikeStatus(DatabaseRepository.LikeType.ARTIFACT, lotNumber, uid,
                new DatabaseRepository.LikeStatusCallback() {
                    @Override
                    public void onSuccess(long likeCount, boolean likedByCurrentUser) {
                        likeButton.setText(String.valueOf(likeCount));
                        // turn off listener to prevent infinite loop
                        likeButton.setOnCheckedChangeListener(null);
                        likeButton.setChecked(likedByCurrentUser);
                        likeButton.setOnCheckedChangeListener(likeButtonListener);
                    }
                    @Override
                    public void onFailure(String errorMessage) {
                        Toast.makeText(getContext(), "Failed to fetch likes: " + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
    }


    public void getArtifactSaveCount(String lotNumber, String uid) {
        databaseRepository.getNumSaved(lotNumber, uid,
                new DatabaseRepository.SavedCountCallback() {
            @Override
            public void onSuccess(long count, boolean savedByCurrentUser) {
                saveButton.setText(String.valueOf(count));
                saveButton.setOnCheckedChangeListener(null);
                saveButton.setChecked(savedByCurrentUser);
                saveButton.setOnCheckedChangeListener(saveButtonListener);
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(getContext(), "Failed to get save count: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void updateComments(String lotNumber) {
        databaseRepository.getAllComments(lotNumber, new DatabaseRepository.CommentListCallback() {
            @Override
            public void onSuccess(List<Comment> comments) {
                commentList.clear();
                commentList.addAll(comments);
                assert recyclerView.getAdapter() != null;
                commentsButton.setText(String.valueOf(recyclerView.getAdapter().getItemCount()));
                recyclerView.getAdapter().notifyDataSetChanged();
            }
            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(getContext(), "Failed to update comments: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void showDeleteAlertDialog(int position, String lotNumber) {
        Comment comment = commentList.get(position);
        User user = userViewModel.getCurrentUser();
        new AlertDialog.Builder(getContext())
                .setTitle("Delete " + comment.getUsername() + "'s comment?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        databaseRepository.deleteComment(
                                lotNumber,
                                comment.getId(),
                                new DatabaseRepository.SimpleCallback() {
                                    @Override
                                    public void onSuccess() {
                                        commentList.remove(position);
                                        assert recyclerView.getAdapter() != null;
                                        recyclerView.getAdapter().notifyItemRemoved(position);
                                        Toast.makeText(getContext(), "Comment deleted", Toast.LENGTH_SHORT).show();
                                    }
                                    @Override
                                    public void onFailure(String errorMessage) {
                                        Toast.makeText(getContext(), "Failed to delete: " + errorMessage, Toast.LENGTH_SHORT).show();
                                    }
                                });

                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}