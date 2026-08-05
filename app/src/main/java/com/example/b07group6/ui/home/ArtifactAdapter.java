package com.example.b07group6.ui.home;

import android.net.Uri;
import android.util.Log;
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
import com.example.b07group6.construct.Artifact;
import com.example.b07group6.backend.DatabaseRepository;
import com.example.b07group6.backend.FirebaseDatabaseRepository;

import java.util.List;

public class ArtifactAdapter extends RecyclerView.Adapter<ArtifactAdapter.ArtifactViewHolder> {
    private OnArtifactInteractionListener listener;
    private List<Artifact> artifactList;
    private FirebaseDatabaseRepository database;
    private String currentUid;

    // ========= Defining the ViewHolder Class to hold our views ===========
    public static class ArtifactViewHolder extends RecyclerView.ViewHolder {
        TextView artifactName;
        TextView artifactDescription;
        ImageView artifactImage;
        CheckBox artifactIsSaved;
        CheckBox artifactLikeButton;
        TextView artifactLikeCount;

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



            // Note for future me, use artifactIsSaved.toggle(); when you are initiating these stuffs.

            artifactLikeButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(@NonNull CompoundButton buttonView, boolean isChecked) {
                    int position = getBindingAdapterPosition();
                    if (listener != null && position != RecyclerView.NO_POSITION) {
                        listener.onLikePress(position, isChecked);
                    }
                }
            });
        }

        // Write public fucntion to toggle saved and un saved. This function needs to handle all the saving operations...?
        //      Not sure if we want to make this function do the saving of the operations yet...
    }

    public ArtifactAdapter(List<Artifact> artifactList, OnArtifactInteractionListener listener) {
        this.artifactList = artifactList;
        this.listener = listener;
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public ArtifactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create a new view.
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.artifact_card, parent, false);

        return new ArtifactViewHolder(view, listener);
    }


    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ArtifactViewHolder viewHolder, final int position) {
        int position_2 = position;
        Artifact artifact = artifactList.get(position);
        viewHolder.artifactName.setText(artifact.getArtifactName());
        viewHolder.artifactDescription.setText(artifact.getDescription());
        Uri imageUrl = Uri.parse(artifact.getImageUrl());
        viewHolder.artifactImage.setImageURI(imageUrl);

        Glide.with(viewHolder.itemView.getContext())
                .load(artifact.getImageUrl())
                .into(viewHolder.artifactImage);

        viewHolder.artifactIsSaved.setChecked(artifact.isSavedByCurrentUser());
        viewHolder.artifactLikeCount.setText(String.valueOf(artifact.getLikeCount()));
        viewHolder.artifactLikeButton.setOnCheckedChangeListener(null);
        viewHolder.artifactLikeButton.setChecked(artifact.isLikedByCurrentUser());

        // Handling adding to saved artifact page:
        viewHolder.artifactIsSaved.setOnCheckedChangeListener(null);
        viewHolder.artifactIsSaved.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull CompoundButton buttonView, boolean isChecked) {
                Log.d("OUCHES:SDLKJF:LSD", "We are calling a swap on: " + position_2);
                if (listener != null && position_2 != RecyclerView.NO_POSITION) {
                    listener.onSaveArifactPress(position_2, isChecked);
                }
            }
        });
    }
    // END_INCLUDE(recyclerViewOnBindViewHolder)

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return artifactList.size();
    }
}