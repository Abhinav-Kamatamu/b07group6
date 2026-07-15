package com.example.b07group6.backend;

import com.example.b07group6.construct.Artifact;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;

import java.util.Map;

public class FirebaseArtifactOperator implements ArtifactOperator {
    @Override
    public void checkLotNumberExists(String lotNumber, ExistsCallback callback) {
        FirebaseDatabase.getInstance().getReference("artifacts")
                .child(lotNumber)
                .get()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        callback.onFailure(task.getException() != null
                                ? task.getException().getMessage() : "Could not check lot number");
                        return;
                    }
                    callback.onResult(task.getResult() != null && task.getResult().exists());
                });

    }

    @Override
    public void saveArtifact(Artifact artifact, SaveCallback callback) {
        FirebaseDatabase.getInstance().getReference("artifacts")
                .child(artifact.getLotNumber())
                .setValue(artifact.toMap())
                .addOnCompleteListener(writeTask -> {
                    if (!writeTask.isSuccessful()) {
                        callback.onFailure("Could not save user record");
                        return;
                    }
                    callback.onSuccess();
                });
    }

    @Override
    public void getArtifact(String lotNumber, GetCallback callback) {
        FirebaseDatabase.getInstance().getReference("artifacts")
                .child(lotNumber)
                .get()
                .addOnCompleteListener(dbTask -> {
                    if (!dbTask.isSuccessful() || dbTask.getResult() == null || !dbTask.getResult().exists()) {
                        callback.onFailure("Could not load user data");
                        return;
                    }
                    GenericTypeIndicator<Map<String, Object>> typeToken = new GenericTypeIndicator<>() {};
                    Map<String, Object> map = dbTask.getResult().getValue(typeToken);
                    callback.onSuccess(new Artifact(map, lotNumber));
                });
    }
}
