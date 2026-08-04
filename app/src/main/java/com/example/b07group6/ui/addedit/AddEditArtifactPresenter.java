package com.example.b07group6.ui.addedit;

import android.net.Uri;
import android.util.Log;

import com.example.b07group6.backend.DatabaseRepository;
import com.example.b07group6.backend.FirebaseDatabaseRepository;
import com.example.b07group6.backend.ImageRepository;
import com.example.b07group6.ui.login.LoginContract;

import java.util.Map;

public class AddEditArtifactPresenter implements AddEditArtifactContract.Presenter {

    private final AddEditArtifactContract.View view;
    private final FirebaseDatabaseRepository databaseRepository;
    private final ImageRepository imageRepository;
    private final boolean isEditMode;

    /**
     * Create a presenter that handles logic between the model and the view
     * @param view an object implementing {@link LoginContract.View}
     * @param databaseRepository an object implementing {@link FirebaseDatabaseRepository}
     * @param imageRepository an onject implementing {@link ImageRepository}
     * */
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
            public void onSuccess(boolean exists) {
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

    /**
     * Method to upload artifact data onto Firebase, as well as upload its corresponding
     * image to Supabase
     * @param lotNumber the lot number of the artifact
     * @param draftArtifact the artifact data to be uploaded to Firebase
     * @param localPathUri the uri of the image to be uploaded to Supabase
     */
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

    /**
     * Method to upload artifact data onto Firebase
     * @param lotNumber the lot number of the artifact
     * @param draftArtifact the artifact data to be uploaded to Firebase
     * @param newPublicUrl the supabaseUrl of the image associated with the artifact
     */
    private void saveArtifact(String lotNumber, Map<String, Object> draftArtifact, String newPublicUrl) {
       Runnable innerSaveArtifact = this.getRunnableForSave(lotNumber, draftArtifact);
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

    /**
     * Method to create a runnable that uploads the final draft of an artifact onto Firebase
     * @param lotNumber the lot number of the artifact
     * @param draftArtifact the artifact data to be uploaded
     * @return a runnable function that we can execute
     */
    private Runnable getRunnableForSave(String lotNumber, Map<String, Object> draftArtifact) {
        return () -> databaseRepository.saveArtifact(lotNumber, draftArtifact, new DatabaseRepository.SimpleCallback() {
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

    /**
     * A method to validate that the mandatory fields of an artifact are present
     * @param lotNumber the lot number of an artifact
     * @param draftArtifact the artifact data to be uploaded to firebase
     * @return a String that is null on success, and contains an error message if a field is not present
     */
    private String validateMandatoryFields(String lotNumber, Map<String, Object> draftArtifact) {
        if (isBlank(lotNumber)) return "Lot number is required";
        if (isBlank(draftArtifact.get("artifactName"))) return "Artifact name is required";
        if (isBlank(draftArtifact.get("description"))) return "Description is required";
        if (isBlank(draftArtifact.get("category"))) return "Category is required";
        if (isBlank(draftArtifact.get("material"))) return "Material is required";
        if (isBlank(draftArtifact.get("dynastyPeriod"))) return "Dynasty/Period is required";
        return null;
    }

    /**
     * A method to determine if a value is not a valid artifact field
     * @param value the Object to check
     * @return true if it's not valid, and false if it's valid
     */
    private boolean isBlank(Object value) {
        return !(value instanceof String) || ((String) value).isBlank();
    }
}