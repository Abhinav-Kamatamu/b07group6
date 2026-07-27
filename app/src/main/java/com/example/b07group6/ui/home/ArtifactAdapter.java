package com.example.b07group6.ui.home;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.b07group6.R;
import com.example.b07group6.construct.Artifact;

import java.util.List;

public class ArtifactAdapter extends RecyclerView.Adapter<ArtifactAdapter.ArtifactViewHolder> {
    private OnArtifactInteractionListener listener;
    private List<Artifact> artifactList;

    // ========= Defining the ViewHolder Class to hold our views ===========
    public static class ArtifactViewHolder extends RecyclerView.ViewHolder {
        TextView artifactName;
        TextView artifactDescription;
        ImageView artifactImage;
        CheckBox artifactIsSaved;

        TextView artifactLikeCount;

        public ArtifactViewHolder(View itemView, OnArtifactInteractionListener listener) {
            super(itemView);

            artifactName = itemView.findViewById(R.id.artifact_name);
            artifactDescription = itemView.findViewById(R.id.artifact_description);
            artifactIsSaved = itemView.findViewById(R.id.artifact_is_saved);
            artifactImage = itemView.findViewById(R.id.artifact_imageView);
            artifactLikeCount = itemView.findViewById(R.id.artifact_likes_count);

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

            // Handling adding to saved artifact page:
            artifactIsSaved.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(@NonNull CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        // Run code to add to saved and stuff for this artifact
                    } else {
                        // Run code to REMOVE from saved and stuff for this artifact
                    }
                }
            });

            // Note for future me, use artifactIsSaved.toggle(); when you are initiating these stuffs.
        }

        // Write public fucntion to toggle saved and un saved. This function needs to handle all the saving operations...?
        //      Not sure if we want to make this function do the saving of the operations yet...
    }

    public ArtifactAdapter(List<Artifact> artifactList, OnArtifactInteractionListener listener) {
        this.artifactList = artifactList;
        this.listener = listener;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ArtifactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create a new view.
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.artifact_card, parent, false);

        return new ArtifactViewHolder(view, listener);
    }
    // END_INCLUDE(recyclerViewOnCreateViewHolder)

    // BEGIN_INCLUDE(recyclerViewOnBindViewHolder)
    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ArtifactViewHolder viewHolder, final int position) {
        Artifact artifact = artifactList.get(position);
        viewHolder.artifactName.setText(artifact.getArtifactName());
        viewHolder.artifactDescription.setText(artifact.getDescription());
        // Implement a way to get like count and then change the like count here:
        //        viewHolder.artifactLikeCount.setText(<<VALUE TO BE PASSED AS STRING>>);
        // Implement a way to get if toggled and then change the toggle here using an if statement here:
        //        viewHolder.artifactIsSaved.toggle();
    }
    // END_INCLUDE(recyclerViewOnBindViewHolder)

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return artifactList.size();
    }
}