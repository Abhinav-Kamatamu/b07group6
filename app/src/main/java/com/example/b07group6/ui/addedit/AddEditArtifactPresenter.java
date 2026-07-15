package com.example.b07group6.ui.addedit;

import android.net.Uri;

import com.example.b07group6.backend.ArtifactOperator;
import com.example.b07group6.backend.ImageUploader;
import com.example.b07group6.construct.Artifact;

import java.net.URI;

public class AddEditArtifactPresenter implements AddEditArtifactContract.Presenter {

    private final AddEditArtifactContract.View view;
    private final ArtifactOperator artifactOperations;
    private final ImageUploader imageUploader;
    private final boolean isEditMode;

    public AddEditArtifactPresenter(
            AddEditArtifactContract.View view,
            ArtifactOperator artifactOperations,
            ImageUploader imageUploader,
            boolean isEditMode
    ) {
        this.view = view;
        this.artifactOperations = artifactOperations;
        this.imageUploader = imageUploader;
        this.isEditMode = isEditMode;
    }

    @Override
    public void onSaveClicked(Artifact draftArtifact, Uri localPathUri) {
        String validationError = validateMandatoryFields(draftArtifact);
        if (validationError != null) {
            view.showError(validationError);
            return;
        }
        if (
            localPathUri == null &&
            (draftArtifact.getImageUrl() == null || draftArtifact.getImageUrl().isBlank())
        ) {
            view.showError("Please select an image");
            return;
        }
        // Show that we're attempting to save
        view.showSaving(true, isEditMode);
        if (isEditMode) {
            saveArtifactAndImage(draftArtifact, localPathUri);
            return;
        }
        // We only check for uniqueness when we're adding. Not editing
        artifactOperations.checkLotNumberExists(draftArtifact.getLotNumber(), new ArtifactOperator.ExistsCallback() {
            @Override
            public void onResult(boolean exists) {
                if (exists) {
                    // We could not save because it already exists
                    view.showSaving(false, true);
                    view.showError("Lot number \"" + draftArtifact.getLotNumber()  + "\" is already in use");
                    return;
                }
                saveArtifactAndImage(draftArtifact, localPathUri);
            }

            @Override
            public void onFailure(String errorMessage) {
                // We could not save for other reasons
                view.showSaving(false, false);
                view.showError(errorMessage);
            }
        });
    }

    private void saveArtifactAndImage(Artifact draftArtifact, Uri localPathUri) {
        // If we have a new url, use it
        if (localPathUri != null) {
            imageUploader.uploadImage(localPathUri, draftArtifact.getLotNumber(), new ImageUploader.UploadCallback() {
                @Override
                public void onSuccess(String publicUrl) {
                    saveArtifact(draftArtifact, publicUrl);
                }

                @Override
                public void onError(String message) {
                    // We could not save for other reasons
                    view.showSaving(false, isEditMode);
                    view.showError("Could not upload image: " + message);
                }
            });
        } else {
            // Otherwise, keep the old one
            saveArtifact(draftArtifact, draftArtifact.getImageUrl());
        }
    }

    private void saveArtifact(Artifact draftArtifact, String publicUrl) {
        Artifact finalArtifact = new Artifact(draftArtifact);
        finalArtifact.setImageUrl(publicUrl);
        artifactOperations.saveArtifact(finalArtifact, new ArtifactOperator.SaveCallback() {
            @Override
            public void onSuccess() {
                view.navigateToHome();
            }

            @Override
            public void onFailure(String errorMessage) {
                // We could not save for other reasons
                view.showSaving(false, isEditMode);
                view.showError(errorMessage);
            }
        });
    }

    private String validateMandatoryFields(Artifact draftArtifact) {
        if (isBlank(draftArtifact.getLotNumber())) return "Lot number is required";
        if (isBlank(draftArtifact.getName())) return "Artifact name is required";
        if (isBlank(draftArtifact.getDescription())) return "Description is required";
        if (isBlank(draftArtifact.getCategory())) return "Category is required";
        if (isBlank(draftArtifact.getMaterial())) return "Material is required";
        if (isBlank(draftArtifact.getDynastyPeriod())) return "Dynasty/Period is required";
        return null;
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}