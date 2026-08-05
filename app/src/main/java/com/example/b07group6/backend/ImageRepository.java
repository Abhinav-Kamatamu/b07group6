package com.example.b07group6.backend;
import android.net.Uri;

/**
 * An interface defining the contract for remote image storage operations.
 * Handles the uploading and deletion of images linked to artifacts.
 */
public interface ImageRepository {

    /**
     * A callback to handle the result of an image upload operation.
     */
    interface UploadCallback {
        /**
         * Method that is called when the image is successfully uploaded.
         * @param publicUrl the publicly accessible URL of the newly uploaded image.
         */
        void onSuccess(String publicUrl);

        /**
         * Method that is called when the image upload fails.
         * @param message a description of the error that occurred.
         */
        void onError(String message);
    }

    /**
     * A callback to handle the result of an image deletion operation.
     */
    interface DeleteCallback {
        /**
         * Method that is called when the image is successfully deleted from storage.
         */
        void onSuccess();

        /**
         * Method that is called when the image deletion fails.
         * @param message a description of the error that occurred.
         */
        void onError(String message);
    }

    /**
     * Uploads an image from a local URI to the remote storage.
     * @param uri the local URI of the image to upload.
     * @param lotNumber the unique identifier of the artifact associated with this image,
     *                  often used for naming or organizing the file in storage.
     * @param callback the callback that will run
     */
    void uploadImage(Uri uri, String lotNumber, UploadCallback callback);

    /**
     * Deletes an image from the remote storage using its public URL.
     * @param publicUrl the publicly accessible URL of the image to be deleted.
     * @param callback the callback to handle the success or failure of the deletion.
     */
    void deleteImage(String publicUrl, DeleteCallback callback);
}