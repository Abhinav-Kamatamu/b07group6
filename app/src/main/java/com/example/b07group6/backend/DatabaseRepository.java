package com.example.b07group6.backend;

import java.util.List;
import java.util.Map;

public interface DatabaseRepository {

    interface SimpleCallback {
        void onSuccess();
        void onFailure(String errorMessage);
    }

    interface BooleanCallback {
        void onResult(boolean result);
        void onFailure(String errorMessage);
    }

    interface ArtifactListCallback {
        void onSuccess(List<Artifact> artifacts);
        void onFailure(String errorMessage);
    }

    interface ArtifactCallback {
        void onSuccess(Artifact artifact);
        void onFailure(String errorMessage);
    }

    interface CommentListCallback {
        void onSuccess(List<Comment> comments);
        void onFailure(String errorMessage);
    }

    interface LikeStatusCallback {
        void onResult(long likeCount, boolean likedByCurrentUser);
        void onFailure(String errorMessage);
    }

    interface StringListCallback {
        void onSuccess(List<String> lotNumbers);
        void onFailure(String errorMessage);
    }

    // Artifacts
    void getAllArtifacts(ArtifactListCallback callback);
    void getArtifact(String lotNumber, ArtifactCallback callback);
    void checkLotNumberExists(String lotNumber, BooleanCallback callback);
    void addArtifact(String lotNumber, Map<String, Object> artifactData, SimpleCallback callback);
    void updateArtifact(String lotNumber, Map<String, Object> artifactData, SimpleCallback callback);
    void deleteArtifact(String lotNumber, SimpleCallback callback);

    // Likes
    void getLikeStatus(String lotNumber, String uid, LikeStatusCallback callback);
    void toggleLike(String lotNumber, String uid, SimpleCallback callback);

    // Comments
    void getComments(String lotNumber, CommentListCallback callback);
    void addComment(String lotNumber, String text, String username, String uid, SimpleCallback callback);
    void deleteComment(String lotNumber, String commentId, SimpleCallback callback);

    // Saved artifacts
    void getSavedArtifacts(String uid, StringListCallback callback);
    void toggleSaved(String uid, String lotNumber, SimpleCallback callback);
}