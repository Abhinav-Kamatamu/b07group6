package com.example.b07group6.backend;

import com.example.b07group6.construct.Artifact;
import com.example.b07group6.construct.Comment;
import java.util.List;
import java.util.Map;

public interface DatabaseRepository {

    interface SimpleCallback {
        void onSuccess();
        void onFailure(String errorMessage);
    }

    interface BooleanCallback {
        void onSuccess(boolean result);
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

    interface CommentCallback {
        void onSuccess(Comment comment);
        void onFailure(String errorMessage);
    }

    interface LikeStatusCallback {
        void onSuccess(long likeCount, boolean likedByCurrentUser);
        void onFailure(String errorMessage);
    }

    interface CommentCountCallback {
        void onSuccess(long commentCount);
        void onFailure(String errorMessage);
    }

    interface SavedCountCallback {
        void onSuccess(long savedCount);
        void onFailure(String errorMessage);
    }

    interface StringListCallback {
        void onSuccess(List<String> lotNumbers);
        void onFailure(String errorMessage);
    }

    enum LikeType {
        ARTIFACT,
        COMMENT,
    }

    enum SaveArtifactMode {
        CREATE,
        UPDATE
    }

    // Artifacts
    void getAllArtifacts(ArtifactListCallback callback);
    void getArtifact(String lotNumber, ArtifactCallback callback);
    void checkLotNumberExists(String lotNumber, BooleanCallback callback);
    void saveArtifact(String lotNumber, Map<String, Object> artifactData, SimpleCallback callback);
    void deleteArtifact(String lotNumber, SimpleCallback callback);

    // Likes
    void getLikeStatus(LikeType type, String typeID, String uid, LikeStatusCallback callback);
    void toggleLike(LikeType type, String typeID, String uid, SimpleCallback callback);

    // Comments
    void getAllComments(String lotNumber, CommentListCallback callback);
    void getComment(String commentID, CommentCallback callback);
    void addComment(String lotNumber, String text, String username, String uid, SimpleCallback callback);
    void deleteComment(String lotNumber, String commentId, SimpleCallback callback);
    void getNumComments(String lotNumber, CommentCountCallback callback);

    // Saved artifacts
    void getSavedArtifacts(String uid, StringListCallback callback);
    void getSavedArtifactsList(String uid, ArtifactListCallback callback);
    void getNumSaved(String lotNumber, SavedCountCallback callback);
    void toggleSaved(String uid, String lotNumber, SimpleCallback callback);
}