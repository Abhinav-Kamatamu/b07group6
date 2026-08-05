package com.example.b07group6.ui.home;

public interface OnArtifactInteractionListener{
    void onSingleClick(int position);
    void onSaveArifactPress(int position, boolean isSaved);
    void onItemLongPress(int position);
    void onLikePress(int position, boolean isLiked);
}
