package com.example.b07group6.ui.artifactview;

import android.app.AlertDialog;
import android.content.DialogInterface;
import static android.view.View.GONE;
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
import android.widget.ImageButton;

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
import com.example.b07group6.backend.ImageRepository;
import com.example.b07group6.backend.SupabaseImageRepository;
import com.example.b07group6.construct.Artifact;
import com.example.b07group6.construct.Comment;
import com.example.b07group6.construct.User;
import com.example.b07group6.shared.UserViewModel;
import com.example.b07group6.ui.home.ArtifactAdapter;
import com.example.b07group6.ui.home.OnArtifactInteractionListener;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class ArtifactViewFragment extends Fragment {

    private NestedScrollView nestedScrollView;
    private ConstraintLayout rootLayout;
    private ImageView artifactImage;
    private ImageButton editArtifactButton;
    private ImageButton deleteArtifactButton;
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
    private RecyclerView relatedArtifactsRecyclerView;
    private TextView relatedArtifactsHeader;
    private View relatedDivider;

    private boolean isDescriptionExpanded = false;
    private UserViewModel userViewModel;
    private FirebaseDatabaseRepository databaseRepository;
    private SupabaseImageRepository imageRepository;
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
        editArtifactButton = view.findViewById(R.id.editArtifactButton);
        deleteArtifactButton = view.findViewById(R.id.deleteArtifactButton);
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
        relatedArtifactsRecyclerView = view.findViewById(R.id.relatedArtifactsRecyclerView);
        relatedArtifactsHeader = view.findViewById(R.id.relatedArtifactsHeader);
        relatedDivider = view.findViewById(R.id.relatedDivider);

        // Get the UserViewModel
        NavBackStackEntry backStackEntry = Navigation.findNavController(view).getBackStackEntry(R.id.navigation_graph);
        userViewModel = new ViewModelProvider(backStackEntry).get(UserViewModel.class);
        databaseRepository = new FirebaseDatabaseRepository();
        imageRepository = new SupabaseImageRepository(requireContext());
        String lotNumber = userViewModel.getExtendedLotNumber();
        // if user presses back and returns to extended artifact page, erase the artifact information
        // from add-edit
        userViewModel.setArtifactEditingLotNumber(null);

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

        // edit and delete artifact buttons only show when user is admin
        if (userViewModel.getCurrentUser().isAdmin()) {
            editArtifactButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    userViewModel.setArtifactEditingLotNumber(lotNumber);
                    Navigation.findNavController(requireView()).navigate(R.id.action_extended_artifact_to_add_edit);
                }
            });

            deleteArtifactButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    databaseRepository.getArtifact(lotNumber,
                            new DatabaseRepository.ArtifactCallback() {
                                @Override
                                public void onSuccess(Artifact artifact) {
                                    showArtifactDeleteAlertDialog(lotNumber, artifact);
                                }

                                @Override
                                public void onFailure(String errorMessage) {
                                    Toast.makeText(getContext(), "Failed to fetch artifact: " + errorMessage, Toast.LENGTH_SHORT).show();
                                }
                            });

                }
            });
        } else {
            editArtifactButton.setVisibility(GONE);
            deleteArtifactButton.setVisibility(GONE);
        }

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
        loadRelatedArtifacts(artifact);
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
                                showCommentDeleteAlertDialog(position, lotNumber);
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

    public void showArtifactDeleteAlertDialog(String lotNumber, Artifact artifact) {
        new com.google.android.material.dialog.MaterialAlertDialogBuilder(getContext())
                .setTitle("Delete artifact?")
                .setMessage("\"" + artifact.getArtifactName() + "\" will be permanently removed.")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // prevent access to database while deleting artifact
                        likeButton.setOnCheckedChangeListener(null);
                        saveButton.setOnCheckedChangeListener(null);
                        commentSubmitButton.setOnClickListener(null);
                        String imageurl = artifact.getImageUrl();
                        databaseRepository.deleteArtifact(
                                lotNumber,
                                new DatabaseRepository.SimpleCallback() {
                                    @Override
                                    public void onSuccess() {
                                        imageRepository.deleteImage(
                                                imageurl,
                                                new ImageRepository.DeleteCallback() {
                                                    @Override
                                                    public void onSuccess() {
                                                        navigateToHome();
                                                        Toast.makeText(getContext(), "Deleted Successfully", Toast.LENGTH_SHORT).show();
                                                    }
                                                    @Override
                                                    public void onError(String message) {
                                                        Toast.makeText(getContext(), "Failed to delete image: " + message, Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                        );
                                    }
                                    @Override
                                    public void onFailure(String errorMessage) {
                                        Toast.makeText(getContext(), "Delete failed: " + errorMessage, Toast.LENGTH_SHORT).show();
                                    }
                                });

                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
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
                commentsButton.setText(String.valueOf(recyclerView.getAdapter().getItemCount()));
                recyclerView.getAdapter().notifyDataSetChanged();
            }
            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(getContext(), "Failed to update comments: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void showCommentDeleteAlertDialog(int position, String lotNumber) {
        Comment comment = commentList.get(position);
        User user = userViewModel.getCurrentUser();
        new com.google.android.material.dialog.MaterialAlertDialogBuilder(getContext())
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

    public void loadRelatedArtifacts(Artifact currentArtifact) {
        databaseRepository.getAllArtifacts(new DatabaseRepository.ArtifactListCallback() {
            @Override
            public void onSuccess(List<Artifact> allArtifacts) {
                List<Artifact> relatedArtifacts = new ArrayList<>();

                for (Artifact other : allArtifacts) {
                    if (other.getLotNumber().equals(currentArtifact.getLotNumber())) {
                        continue;
                    }
                    if (isRelated(currentArtifact, other)) {
                        relatedArtifacts.add(other);
                    }
                }

                if (relatedArtifacts.isEmpty()) {
                    relatedDivider.setVisibility(View.GONE);
                    relatedArtifactsHeader.setVisibility(View.GONE);
                    relatedArtifactsRecyclerView.setVisibility(View.GONE);
                    return;
                }
                relatedDivider.setVisibility(View.VISIBLE);
                relatedArtifactsHeader.setVisibility(View.VISIBLE);
                relatedArtifactsRecyclerView.setVisibility(View.VISIBLE);

                // TODO: The following functions are to be implemented
                OnArtifactInteractionListener relatedListener = new OnArtifactInteractionListener() {
                    @Override
                    public void onSingleClick(int position) {
                        Artifact clicked = relatedArtifacts.get(position);
                        userViewModel.setExtendedLotNumber(clicked.getLotNumber());
                        Navigation.findNavController(requireView()).navigate(R.id.action_global_extended);
                    }

                    @Override
                    public void onSaveArifactPress(int position, boolean isSaved) {
                        // On Save artifactPress needs to be handled
                    }

                    @Override
                    public void onItemLongPress(int position) {
                        // Long Press behaviour to be defined
                    }
                    @Override
                    public void onLikePress(int position, boolean isLiked) {
                        // On Like Press to be handled
                    }
                };

                ArtifactAdapter adapter = new ArtifactAdapter(relatedArtifacts, relatedListener);
                relatedArtifactsRecyclerView.setLayoutManager(
                        new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
                relatedArtifactsRecyclerView.setAdapter(adapter);
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(getContext(), "Failed to load related artifacts: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isRelated(Artifact current, Artifact other) {
        if (fieldsMatch(current.getCategory(), other.getCategory())) {
            return true;
        }
        if (fieldsMatch(current.getMaterial(), other.getMaterial())) {
            return true;
        }
        if (fieldsMatch(current.getDynastyPeriod(), other.getDynastyPeriod())) {
            return true;
        }
        return fieldsMatch(current.getCulturalOrigin(), other.getCulturalOrigin());
    }

    private boolean fieldsMatch(String field1, String field2) {
        return field1 != null && field1.equals(field2);
    }
}