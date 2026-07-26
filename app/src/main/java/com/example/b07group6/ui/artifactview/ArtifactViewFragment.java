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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;

import com.example.b07group6.R;

public class ArtifactViewFragment extends Fragment {

    private NestedScrollView nestedScrollView;
    private ConstraintLayout rootLayout;
    private TextView descriptionText;
    private TextView viewMoreButton;
    private CheckBox likeButton;
    private CheckBox saveButton;
    private Button commentsButton;
    private TextView commentsHeader;

    private boolean isDescriptionExpanded = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_extended_artifact_view, container, false);
        nestedScrollView = root.findViewById(R.id.nestedScrollView);
        rootLayout = root.findViewById(R.id.rootConstraintLayout);
        likeButton = root.findViewById(R.id.likesButtonAndCount);
        saveButton = root.findViewById(R.id.savesButtonAndCount);
        commentsButton = root.findViewById(R.id.commentsButtonAndCount);
        commentsHeader = root.findViewById(R.id.commentsHeader);
        descriptionText = root.findViewById(R.id.descriptionText);
        viewMoreButton = root.findViewById(R.id.viewMoreButton);

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

        return root;
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
}