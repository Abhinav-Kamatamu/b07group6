package com.example.b07group6.backend;
import android.net.Uri;

public interface ImageUploader {

    interface UploadCallback {
        void onSuccess(String publicUrl);
        void onError(String message);
    }

    void uploadImage(Uri uri, String lotNumber, UploadCallback callback);
}
