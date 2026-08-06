package com.example.b07group6.ui.cataloger.base;

/**
 * Callback interface for user interactions with an artifact card in the catalog
 */
public interface OnArtifactInteractionListener {
    /**
     * Called when the artifact card is clicked
     * @param position the position of the clicked artifact in the currently displayed list
     */
    void onSingleClick(int position);
    /**
     * Called when the save checkbox on the artifact card is toggled.
     * @param position the position of the artifact in the currently displayed list
     * @param isSaved the new saved state
     */
    void onSaveArifactPress(int position, boolean isSaved);
    /**
     * Called when the artifact card is pressed for long
     * @param position the position of the artifact in the currently displayed list
     */
    void onItemLongPress(int position);
    /**
     * Called when the like checkbox on the artifact card is toggled.
     * @param position the position of the artifact in the currently displayed list
     * @param isLiked the new liked state
     */
    void onLikePress(int position, boolean isLiked);
}
