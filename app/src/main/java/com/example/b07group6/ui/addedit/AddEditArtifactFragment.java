package com.example.b07group6.ui.addedit;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.b07group6.R;
import com.example.b07group6.backend.DatabaseRepository;
import com.example.b07group6.backend.FirebaseDatabaseRepository;
import com.example.b07group6.backend.SupabaseImageRepository;
import com.example.b07group6.construct.Artifact;
import com.example.b07group6.shared.UserViewModel;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class AddEditArtifactFragment extends Fragment implements AddEditArtifactContract.View {

    private AddEditArtifactContract.Presenter presenter;
    private UserViewModel userViewModel;

    private Artifact existingArtifact;
    private Uri selectedLocalImageUri;

    private EditText lotNumberField;
    private EditText nameField;
    private EditText descriptionField;
    private Spinner categorySpinner;
    private Spinner materialSpinner;
    private Spinner dynastyPeriodSpinner;
    private EditText culturalOriginField;
    private EditText dimensionsField;
    private EditText conditionReportField;
    private EditText currentLocationField;
    private EditText acquisitionMethodField;
    private EditText provenanceField;
    private EditText accessionNumberField;
    private EditText notesField;
    private View imagePickerContainer;
    private ImageView imagePreview;
    private View imagePlaceholderContent;
    private Button selectImageButton;
    private Button saveButton;
    private final ActivityResultLauncher<PickVisualMediaRequest> pickImageLauncher =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                Log.d("ImagePicker", "Picker returned: " + uri);
                if (uri != null) {
                    selectedLocalImageUri = uri;
                    // Note that you use this method to load images from a local Uri only.
                    // That is what we want.
                    imagePreview.setImageURI(uri);
                    imagePreview.setVisibility(View.VISIBLE);
                    imagePlaceholderContent.setVisibility(View.GONE);
                    // If we've selected an image, change the text to say "Change Image"
                    selectImageButton.setText("Change Image");
                }
            });

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_edit_artifact, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        NavBackStackEntry backStackEntry = Navigation.findNavController(view)
                .getBackStackEntry(R.id.navigation_graph);
        userViewModel = new ViewModelProvider(backStackEntry).get(UserViewModel.class);

        if (!userViewModel.getCurrentUser().isAdmin()) {
            navigateToHome();
            return;
        }

        defineVariablesFrom(view);

        FirebaseDatabaseRepository databaseRepository = new FirebaseDatabaseRepository();
        SupabaseImageRepository imageUploader = new SupabaseImageRepository(requireContext());
        String lotNumber = userViewModel.getArtifactEditingLotNumber();
        boolean isEditMode = lotNumber != null;

        presenter = new AddEditArtifactPresenter(
                this,
                databaseRepository,
                imageUploader,
                isEditMode
        );

        View.OnClickListener launchPicker = v -> pickImageLauncher.launch(
                new PickVisualMediaRequest.Builder()
                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                        .build()
        );
        imagePickerContainer.setOnClickListener(launchPicker);
        selectImageButton.setOnClickListener(launchPicker);

        if (isEditMode) {
            lotNumberField.setText(lotNumber);
            lotNumberField.setEnabled(false);
            // Retrieve our current data first before allowing edits and saves
            // disallow saving for now
            saveButton.setEnabled(false);

            databaseRepository.getArtifact(lotNumber, new DatabaseRepository.ArtifactCallback() {
                @Override
                public void onSuccess(Artifact artifact) {
                    existingArtifact = artifact;
                    populateFieldsForEdit(artifact);
                    // Enable saving once we've populated our fields
                    saveButton.setEnabled(true);
                    showSaving(false, true);
                }

                @Override
                public void onFailure(String errorMessage) {
                    showError("Could not fetch Artifact: " + errorMessage);
                    navigateToHome();
                }
            });
        } else {
            showSaving(false, false);
        }
        saveButton.setOnClickListener(v -> onSaveClicked());
    }

    private void defineVariablesFrom(View view) {
        lotNumberField = view.findViewById(R.id.lot_number_field);
        nameField = view.findViewById(R.id.name_field);
        descriptionField = view.findViewById(R.id.description_field);
        categorySpinner = view.findViewById(R.id.category_spinner);
        materialSpinner = view.findViewById(R.id.material_spinner);
        dynastyPeriodSpinner = view.findViewById(R.id.dynasty_period_spinner);
        culturalOriginField = view.findViewById(R.id.cultural_origin_field);
        dimensionsField = view.findViewById(R.id.dimensions_field);
        conditionReportField = view.findViewById(R.id.condition_report_field);
        currentLocationField = view.findViewById(R.id.current_location_field);
        acquisitionMethodField = view.findViewById(R.id.acquisition_method_field);
        provenanceField = view.findViewById(R.id.provenance_field);
        accessionNumberField = view.findViewById(R.id.accession_number_field);
        notesField = view.findViewById(R.id.notes_field);
        imagePickerContainer = view.findViewById(R.id.image_picker_container);
        imagePreview = view.findViewById(R.id.image_preview);
        imagePlaceholderContent = view.findViewById(R.id.image_placeholder_content);
        selectImageButton = view.findViewById(R.id.select_image_button);
        saveButton = view.findViewById(R.id.save_artifact_button);
    }

    private void populateFieldsForEdit(Artifact artifact) {
        nameField.setText(artifact.getArtifactName());
        descriptionField.setText(artifact.getDescription());

        setSpinnerSelection(categorySpinner, R.array.category_options, artifact.getCategory());
        setSpinnerSelection(materialSpinner, R.array.material_options, artifact.getMaterial());
        setSpinnerSelection(dynastyPeriodSpinner, R.array.dynasty_period_options, artifact.getDynastyPeriod());

        culturalOriginField.setText(artifact.getCulturalOrigin());
        dimensionsField.setText(artifact.getDimensions());
        conditionReportField.setText(artifact.getConditionReport());
        currentLocationField.setText(artifact.getCurrentLocation());
        acquisitionMethodField.setText(artifact.getAcquisitionMethod());
        provenanceField.setText(artifact.getProvenance());
        accessionNumberField.setText(artifact.getAccessionNumber());
        notesField.setText(artifact.getNotes());

        if (artifact.getImageUrl() != null && !artifact.getImageUrl().isBlank()) {
            Glide.with(this).load(artifact.getImageUrl()).into(imagePreview);
            imagePlaceholderContent.setVisibility(View.GONE);
            imagePreview.setVisibility(View.VISIBLE);
            // An image has been selected, so change the text from "Select Image" to this
            selectImageButton.setText("Change Image");
        }
    }

    private void setSpinnerSelection(Spinner spinner, int arrayResId, String value) {
        String[] options = getResources().getStringArray(arrayResId);
        int position = Arrays.asList(options).indexOf(value);
        if (position >= 0) {
            spinner.setSelection(position);
        }
    }

    private void onSaveClicked() {
        Map<String, Object> draftArtifact = new HashMap<>();
        String lotNumber = lotNumberField.getText().toString();
        draftArtifact.put("artifactName", nameField.getText().toString());
        draftArtifact.put("description", descriptionField.getText().toString());
        draftArtifact.put("category", categorySpinner.getSelectedItem().toString());
        draftArtifact.put("material", materialSpinner.getSelectedItem().toString());
        draftArtifact.put("dynastyPeriod", dynastyPeriodSpinner.getSelectedItem().toString());
        draftArtifact.put("culturalOrigin", culturalOriginField.getText().toString());
        draftArtifact.put("dimensions", dimensionsField.getText().toString());
        draftArtifact.put("conditionReport", conditionReportField.getText().toString());
        draftArtifact.put("currentLocation", currentLocationField.getText().toString());
        draftArtifact.put("acquisitionMethod", acquisitionMethodField.getText().toString());
        draftArtifact.put("provenance", provenanceField.getText().toString());
        draftArtifact.put("accessionNumber", accessionNumberField.getText().toString());
        draftArtifact.put("notes", notesField.getText().toString());
        draftArtifact.put("imageUrl", existingArtifact != null ? existingArtifact.getImageUrl(): null);
        presenter.onSaveClicked(lotNumber, draftArtifact, selectedLocalImageUri);
    }

    @Override
    public void showError(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showSaving(boolean isSaving, boolean alreadyExists) {
        saveButton.setEnabled(!isSaving);
        saveButton.setText(isSaving ? "Saving..." : (alreadyExists ? "Save Changes" : "Save Artifact"));
    }

    @Override
    public void navigateToHome() {
        userViewModel.setArtifactEditingLotNumber(null);
        Navigation.findNavController(requireView()).navigate(R.id.action_add_edit_artifact_to_home);
    }
}