package com.example.b07group6.ui.cataloger.base;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.b07group6.R;
import com.example.b07group6.backend.FirebaseDatabaseRepository;
import com.example.b07group6.construct.Artifact;

import java.util.List;

/**
 * RecyclerView adapter responsible for binding a list of {@link Artifact} objects
 * to the artifact card layout and for wiring up user interactions to an
 * {@link OnArtifactInteractionListener}.
 */
public class ArtifactAdapter extends RecyclerView.Adapter<ArtifactAdapter.ArtifactViewHolder> {
    private OnArtifactInteractionListener listener;
    private List<Artifact> artifactList;
    private FirebaseDatabaseRepository database;
    private String currentUid;

    /**
     * ViewHolder that keeps references to the views within a single artifact card,
     * and forwards clicks and long-presses to the adapter's listener.
     */
    public static class ArtifactViewHolder extends RecyclerView.ViewHolder {
        TextView artifactName;
        TextView artifactDescription;
        ImageView artifactImage;
        CheckBox artifactIsSaved;
        CheckBox artifactLikeButton;
        TextView artifactLikeCount;

        /**
         * Creates a new ViewHolder for a single artifact card
         * @param itemView the inflated artifact card view
         * @param listener the listener to notify of single-click and long-press events
         */
        public ArtifactViewHolder(View itemView, OnArtifactInteractionListener listener) {
            super(itemView);

            artifactName = itemView.findViewById(R.id.artifact_name);
            artifactDescription = itemView.findViewById(R.id.artifact_description);
            artifactIsSaved = itemView.findViewById(R.id.artifact_is_saved);
            artifactImage = itemView.findViewById(R.id.artifact_imageView);
            artifactLikeCount = itemView.findViewById(R.id.artifact_likes_count);
            artifactLikeButton = itemView.findViewById(R.id.artifact_like_button);

            // Define long press listener for the ViewHolder's View.
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int position = getBindingAdapterPosition(); // I probobly don't need position data
                    if (listener != null && position != RecyclerView.NO_POSITION) {
                        listener.onItemLongPress(position);
                    }
                    return true;
                }
            });

            // Handling Going to add-and-edit artifact page:
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getBindingAdapterPosition();
                    if (listener != null && position != RecyclerView.NO_POSITION) {
                        listener.onSingleClick(position);
                    }
                }
            });
        }
    }

    /**
     * Creates a new adapter for a list of artifacts.
     * @param artifactList the list of artifacts to display
     * @param listener the listener for item interactions
     */
    public ArtifactAdapter(List<Artifact> artifactList, OnArtifactInteractionListener listener) {
        this.artifactList = artifactList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ArtifactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.artifact_card, parent, false);
        return new ArtifactViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(ArtifactViewHolder viewHolder, final int position) {
        Artifact artifact = artifactList.get(position);
        viewHolder.artifactName.setText(artifact.getArtifactName());
        viewHolder.artifactDescription.setText(artifact.getDescription());
        Uri imageUrl = Uri.parse(artifact.getImageUrl());
        viewHolder.artifactImage.setImageURI(imageUrl);

        Glide.with(viewHolder.itemView.getContext())
                .load(artifact.getImageUrl())
                .into(viewHolder.artifactImage);

        viewHolder.artifactLikeCount.setText(String.valueOf(artifact.getLikeCount()));
        viewHolder.artifactLikeButton.setOnCheckedChangeListener(null);
        viewHolder.artifactLikeButton.setChecked(artifact.isLikedByCurrentUser());
        viewHolder.artifactLikeButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull CompoundButton buttonView, boolean isChecked) {
                int position = viewHolder.getBindingAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onLikePress(position, isChecked);
                }
            }
        });

        viewHolder.artifactIsSaved.setOnCheckedChangeListener(null);
        viewHolder.artifactIsSaved.setChecked(artifact.isSavedByCurrentUser());
        viewHolder.artifactIsSaved.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull CompoundButton buttonView, boolean isChecked) {
                int position = viewHolder.getBindingAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onSaveArifactPress(position, isChecked);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return artifactList.size();
    }
}