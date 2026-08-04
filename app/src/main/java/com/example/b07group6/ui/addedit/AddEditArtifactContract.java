package com.example.b07group6.ui.addedit;

import android.net.Uri;

import java.util.Map;

/**
 * Defines the contract for the Add and Edit Artifact page
 */
public interface AddEditArtifactContract {
    /** Contains all functions required for the Add and Edit Artifact View */
    interface View {
        /**
         * Method to allow the view to handle error messages
         * @param message the error message
         */
        void showError(String message);

        /**
         * Method to allow the view to display if it's attempting to save an artifact
         * @param isSaving an indication of whether a save is currently in process
         * @param alreadyExists an indication of whether this artifact currently exists
         */
        void showSaving(boolean isSaving, boolean alreadyExists);
        /** Method to allow the view to navigate to the home page */
        void navigateToHome();
    }
    /** Contains all functions required of the Add and Edit Artifact Presenter */
    interface Presenter {
        /**
         * Method to permit the presenter to handle a save
         * @param lotNumber the lot number of the artifact
         * @param draftArtifact the artifact data to be uploaded to Firebase
         * @param localPathUri the uri of the image to be uploaded to Supabase
         */
        void onSaveClicked(String lotNumber, Map<String, Object> draftArtifact, Uri localPathUri);
    }
}