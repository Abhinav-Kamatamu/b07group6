package com.example.b07group6.backend;
import android.net.Uri;

public interface ImageRepository {

    interface UploadCallback {
        void onSuccess(String publicUrl);
        void onError(String message);
    }

    public interface DeleteCallback {
        void onSuccess();
        void onError(String message);
    }

    void uploadImage(Uri uri, String lotNumber, UploadCallback callback);

    void deleteImage(String publicUrl, DeleteCallback callback);
}
