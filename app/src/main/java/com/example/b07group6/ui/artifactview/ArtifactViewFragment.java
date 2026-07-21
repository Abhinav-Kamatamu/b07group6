package com.example.b07group6.ui.artifactview;

import android.os.Bundle;
import android.text.TextUtils;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.example.b07group6.R;

public class ArtifactViewFragment extends Fragment {

    private static final int COLLAPSED_MAX_LINES = 4;

    private ConstraintLayout rootLayout;
    private TextView descriptionText;
    private TextView viewMoreButton;

    private boolean isDescriptionExpanded = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.test_artifact_view, container, false);

        rootLayout = root.findViewById(R.id.rootConstraintLayout); // give your ConstraintLayout this id
        descriptionText = root.findViewById(R.id.descriptionText);
        viewMoreButton = root.findViewById(R.id.viewMoreButton);

        viewMoreButton.setOnClickListener(v -> toggleDescription());

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
            descriptionText.setMaxLines(COLLAPSED_MAX_LINES);
            descriptionText.setEllipsize(TextUtils.TruncateAt.END);
            viewMoreButton.setText("View more");
        }
    }
}