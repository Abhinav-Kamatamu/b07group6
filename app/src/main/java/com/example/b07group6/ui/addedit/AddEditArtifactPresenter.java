package com.example.b07group6.ui.addedit;

import android.net.Uri;
import android.util.Log;

import com.example.b07group6.backend.DatabaseRepository;
import com.example.b07group6.backend.FirebaseDatabaseRepository;
import com.example.b07group6.backend.ImageRepository;

import java.util.Map;

public class AddEditArtifactPresenter implements AddEditArtifactContract.Presenter {

    private final AddEditArtifactContract.View view;
    private final FirebaseDatabaseRepository databaseRepository;
    private final ImageRepository imageRepository;
    private final boolean isEditMode;

    public AddEditArtifactPresenter(
            AddEditArtifactContract.View view,
            FirebaseDatabaseRepository databaseRepository,
            ImageRepository imageRepository,
            boolean isEditMode
    ) {
        this.view = view;
        this.databaseRepository = databaseRepository;
        this.imageRepository = imageRepository;
        this.isEditMode = isEditMode;
    }

    @Override
    public void onSaveClicked(String lotNumber, Map<String, Object> draftArtifact, Uri localPathUri) {
        String validationError = validateMandatoryFields(lotNumber, draftArtifact);
        if (validationError != null) {
            view.showError(validationError);
            return;
        }
        String imgUrl = (String) draftArtifact.get("imageUrl");
        if (
            localPathUri == null &&
            (imgUrl == null || imgUrl.isBlank())
        ) {
            view.showError("Please select an image");
            return;
        }
        // Show that we're attempting to save
        view.showSaving(true, isEditMode);
        if (isEditMode) {
            saveArtifactAndImage(lotNumber, draftArtifact, localPathUri);
            return;
        }
        // We only check for uniqueness when we're adding. Not editing
        databaseRepository.checkLotNumberExists(lotNumber, new DatabaseRepository.BooleanCallback() {
            @Override
            public void onResult(boolean exists) {
                if (exists) {
                    // We could not save because it already exists
                    view.showSaving(false, true);
                    view.showError("Lot number \"" + lotNumber + "\" is already in use");
                    return;
                }
                saveArtifactAndImage(lotNumber, draftArtifact, localPathUri);
            }

            @Override
            public void onFailure(String errorMessage) {
                // We could not save for other reasons
                view.showSaving(false, false);
                view.showError(errorMessage);
            }
        });
    }

    private void saveArtifactAndImage(String lotNumber, Map<String, Object> draftArtifact, Uri localPathUri) {
        // If we have a new url, use it
        if (localPathUri != null) {
            imageRepository.uploadImage(localPathUri, lotNumber, new ImageRepository.UploadCallback() {
                @Override
                public void onSuccess(String newPublicUrl) {
                    saveArtifact(lotNumber, draftArtifact, newPublicUrl);
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
            saveArtifact(lotNumber, draftArtifact, null);
        }
    }

    private void saveArtifact(String lotNumber, Map<String, Object> draftArtifact, String newPublicUrl) {
        Runnable innerSaveArtifact = () -> databaseRepository.saveArtifact(lotNumber, draftArtifact, new DatabaseRepository.SimpleCallback() {
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
        String oldUrl = null;
        if (newPublicUrl != null) {
            oldUrl = (String) draftArtifact.put("imageUrl", newPublicUrl);
        }
        if (newPublicUrl == null || oldUrl == null) {
            innerSaveArtifact.run();
            return;
        }
        // Delete old url from Supabase
        imageRepository.deleteImage(oldUrl, new ImageRepository.DeleteCallback() {
            @Override
            public void onSuccess() {
                Log.d("Image Repo", "Successfully deleted the old URL");
                innerSaveArtifact.run();
            }

            @Override
            public void onError(String message) {
                view.showError("Could not delete old image from Supabase: " + message);
                view.showSaving(false, isEditMode);
            }
        });
    }

    private String validateMandatoryFields(String lotNumber, Map<String, Object> draftArtifact) {
        if (isBlank(lotNumber)) return "Lot number is required";
        if (isBlank(draftArtifact.get("artifactName"))) return "Artifact name is required";
        if (isBlank(draftArtifact.get("description"))) return "Description is required";
        if (isBlank(draftArtifact.get("category"))) return "Category is required";
        if (isBlank(draftArtifact.get("material"))) return "Material is required";
        if (isBlank(draftArtifact.get("dynastyPeriod"))) return "Dynasty/Period is required";
        return null;
    }

    private boolean isBlank(Object value) {
        return !(value instanceof String) || ((String) value).isBlank();
    }
}