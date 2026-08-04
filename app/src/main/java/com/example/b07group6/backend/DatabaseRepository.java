package com.example.b07group6.backend;

import com.example.b07group6.construct.Artifact;
import com.example.b07group6.construct.Comment;
import java.util.List;
import java.util.Map;

/**
 * An interface defining the contract for database operations.
 * Handles the retrieval, creation, updating, and deletion of artifacts,
 * comments, likes, and saved items.
 */
public interface DatabaseRepository {
    /**
     * A generic callback for database operations that do not return a specific object upon success.
     */
    interface SimpleCallback {
        /**
         * Method that is called whe the database operation completes successfully.
         */
        void onSuccess();

        /**
         * Method that is called when the database operation fails.
         * @param errorMessage a description of the error that occurred.
         */
        void onFailure(String errorMessage);
    }

    /**
     * A callback for database operations that evaluate to a boolean result.
     */
    interface BooleanCallback {
        /**
         * Method that is called when the database operation completes successfully.
         * @param result the boolean result of the operation.
         */
        void onSuccess(boolean result);

        /**
         * Method that is called when the database operation fails.
         * @param errorMessage a description of the error that occurred.
         */
        void onFailure(String errorMessage);
    }

    /**
     * A callback for retrieving a list of {@link Artifact} objects.
     */
    interface ArtifactListCallback {
        /**
         * Method that is called when the artifacts are successfully retrieved.
         * @param artifacts a list containing the retrieved artifacts.
         */
        void onSuccess(List<Artifact> artifacts);

        /**
         * Method that is called when the retrieval operation fails.
         * @param errorMessage a description of the error that occurred.
         */
        void onFailure(String errorMessage);
    }

    /**
     * A callback for retrieving a single {@link Artifact} object.
     */
    interface ArtifactCallback {
        /**
         * Method that is called when the artifact is successfully retrieved.
         * @param artifact the retrieved artifact.
         */
        void onSuccess(Artifact artifact);

        /**
         * Method that is called when the retrieval operation fails.
         * @param errorMessage a description of the error that occurred.
         */
        void onFailure(String errorMessage);
    }

    /**
     * A callback for retrieving a list of {@link Comment} objects.
     */
    interface CommentListCallback {
        /**
         * Method that is called when the comments are successfully retrieved.
         * @param comments a list containing the retrieved comments.
         */
        void onSuccess(List<Comment> comments);

        /**
         * Method that is called when the retrieval operation fails.
         * @param errorMessage a description of the error that occurred.
         */
        void onFailure(String errorMessage);
    }

    /**
     * A callback for retrieving a single {@link Comment} object.
     */
    interface CommentCallback {
        /**
         * Method that is called when the comment is successfully retrieved.
         * @param comment the retrieved comment.
         */
        void onSuccess(Comment comment);

        /**
         * Method that is called when the retrieval operation fails.
         * @param errorMessage a description of the error that occurred.
         */
        void onFailure(String errorMessage);
    }

    /**
     * A callback for retrieving the like status of a specific item.
     */
    interface LikeStatusCallback {
        /**
         * Method that is called when the like status is successfully retrieved.
         * @param likeCount the total number of likes the item has.
         * @param likedByCurrentUser true if the current user has liked the item, false otherwise.
         */
        void onSuccess(long likeCount, boolean likedByCurrentUser);

        /**
         * Method that is called when the retrieval operation fails.
         * @param errorMessage a description of the error that occurred.
         */
        void onFailure(String errorMessage);
    }

    /**
     * A callback for retrieving the total number of comments on an item.
     */
    interface CommentCountCallback {
        /**
         * Method that is called when the comment count is successfully retrieved.
         * @param commentCount the total number of comments.
         */
        void onSuccess(long commentCount);

        /**
         * Method that is when the retrieval operation fails.
         * @param errorMessage a description of the error that occurred.
         */
        void onFailure(String errorMessage);
    }

    /**
     * A callback for retrieving the saved status and count of an artifact.
     */
    interface SavedCountCallback {
        /**
         * Method that is called when the saved count is successfully retrieved.
         *
         * @param savedCount the total number of times the artifact has been saved.
         * @param savedByCurrentUser true if the current user has saved the artifact, false otherwise.
         */
        void onSuccess(long savedCount, boolean savedByCurrentUser);

        /**
         * Method that is called when the retrieval operation fails.
         * @param errorMessage a description of the error that occurred.
         */
        void onFailure(String errorMessage);
    }

    /**
     * A callback for retrieving a list of strings (e.g., lot numbers).
     */
    interface StringListCallback {
        /**
         * Method that is called when the list of strings is successfully retrieved.
         * @param lotNumbers a list of string identifiers.
         */
        void onSuccess(List<String> lotNumbers);

        /**
         * Method that is called when the retrieval operation fails.
         * @param errorMessage a description of the error that occurred.
         */
        void onFailure(String errorMessage);
    }

    /**
     * Defines the constructs that can be liked within the application.
     */
    enum LikeType {
        ARTIFACT,
        COMMENT,
    }

    /**
     * Retrieves all artifacts currently stored in the database.
     * @param callback the callback that will run.
     */
    void getAllArtifacts(ArtifactListCallback callback);

    /**
     * Retrieves a specific artifact by its lot number.
     * @param lotNumber the lot number of the artifact.
     * @param callback the callback that will run.
     */
    void getArtifact(String lotNumber, ArtifactCallback callback);

    /**
     * Checks if an artifact with the specified lot number already exists in the database.
     * @param lotNumber the lot number to check.
     * @param callback the callback that will run
     */
    void checkLotNumberExists(String lotNumber, BooleanCallback callback);

    /**
     * Saves or updates an artifact in the database.
     *
     * @param lotNumber the lot number for the artifact.
     * @param artifactData a map containing the artifact's properties and values.
     * @param callback the callback that will run
     */
    void saveArtifact(String lotNumber, Map<String, Object> artifactData, SimpleCallback callback);

    /**
     * Deletes a specific artifact from the database.
     * @param lotNumber the lot number of the artifact to delete.
     * @param callback the callback that will run
     */
    void deleteArtifact(String lotNumber, SimpleCallback callback);

    /**
     * Retrieves the current like count and whether the specified user has liked the item.
     * @param type the type of item being checked (ARTIFACT or COMMENT).
     * @param typeID the unique identifier of the item (e.g., lotNumber or commentId).
     * @param uid the unique user identifier to check for a specific like.
     * @param callback the callback that will run
     */
    void getLikeStatus(LikeType type, String typeID, String uid, LikeStatusCallback callback);

    /**
     * Toggles the like status of a specific item for a given user.
     * If the user already liked it, the like is removed. If not, a like is added.
     * @param type the type of item being toggled (ARTIFACT or COMMENT).
     * @param typeID the unique identifier of the item.
     * @param uid the unique user id performing the toggle action.
     * @param callback the callback that will run
     */
    void toggleLike(LikeType type, String typeID, String uid, SimpleCallback callback);

    /**
     * Retrieves all comments associated with a specific artifact.
     * @param lotNumber the lot number of the artifact.
     * @param callback the callback that will run
     */
    void getAllComments(String lotNumber, CommentListCallback callback);

    /**
     * Retrieves a specific comment by its ID.
     * @param commentID the id of the comment.
     * @param callback the callback that will run
     */
    void getComment(String commentID, CommentCallback callback);

    /**
     * Adds a new comment to a specific artifact.
     * @param lotNumber the lot number of the artifact being commented on.
     * @param text the text content of the comment.
     * @param username the display name of the user making the comment.
     * @param uid the unique user identifier of the user making the comment.
     * @param callback the callback that will run
     */
    void addComment(String lotNumber, String text, String username, String uid, SimpleCallback callback);

    /**
     * Deletes a specific comment from an artifact.
     * @param lotNumber the lot number of the artifact the comment belongs to.
     * @param commentId the lot number of the comment to delete.
     * @param callback the callback that will run
     */
    void deleteComment(String lotNumber, String commentId, SimpleCallback callback);

    /**
     * Retrieves the total number of comments on a specific artifact.
     * @param lotNumber the lot number of the artifact.
     * @param callback the callback that will run
     */
    void getNumComments(String lotNumber, CommentCountCallback callback);

    /**
     * Retrieves a list of lot numbers for the artifacts saved by a specific user.
     * @param uid the unique user identifier.
     * @param callback the callback that will run
     */
    void getSavedArtifacts(String uid, StringListCallback callback);

    /**
     * Retrieves a list of full {@link Artifact} objects saved by a specific user.
     * @param uid the unique user identifier.
     * @param callback the callback that will run
     */
    void getSavedArtifactsList(String uid, ArtifactListCallback callback);

    /**
     * Retrieves the total number of times an artifact has been saved, and whether
     * the specified user has currently saved it.
     * @param lotNumber the lot number of the artifact.
     * @param uid the unique user identifier to check for a specific save.
     * @param callback the callback that will run
     */
    void getNumSaved(String lotNumber, String uid, SavedCountCallback callback);

    /**
     * Toggles the saved status of a specific artifact for a given user.
     * If the user already saved it, it is unsaved. If not, it is saved.
     * @param uid the unique user identifier performing the toggle action.
     * @param lotNumber the lot number of the artifact.
     * @param callback the callback that will run
     */
    void toggleSaved(String uid, String lotNumber, SimpleCallback callback);
}