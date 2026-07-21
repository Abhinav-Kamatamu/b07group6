package com.example.b07group6.ui.addedit;

import android.net.Uri;

import java.util.Map;

public interface AddEditArtifactContract {

    interface View {
        void showError(String message);
        void showSaving(boolean isSaving, boolean alreadyExists);
        void navigateToHome();
    }

    interface Presenter {
        void onSaveClicked(String lotNumber, Map<String, Object> draftArtifact, Uri localPathUri);
    }
}