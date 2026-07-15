package com.example.b07group6.ui.addedit;

import android.net.Uri;

import com.example.b07group6.construct.Artifact;

public interface AddEditArtifactContract {

    interface View {
        void showError(String message);
        void showSaving(boolean isSaving, boolean alreadyExists);
        void navigateToHome();
    }

    interface Presenter {
        void onSaveClicked(Artifact draftArtifact, Uri localPathUri);
    }
}