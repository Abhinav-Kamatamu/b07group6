package com.example.b07group6.backend;
import com.example.b07group6.construct.Artifact;

public interface ArtifactOperator {

    interface ExistsCallback {
        void onResult(boolean exists);
        void onFailure(String errorMessage);
    }

    interface SaveCallback {
        void onSuccess();
        void onFailure(String errorMessage);
    }

    interface GetCallback {
        void onSuccess(Artifact artifact);
        void onFailure(String errorMessage);
    }

    void checkLotNumberExists(String lotNumber, ExistsCallback callback);

    void saveArtifact(Artifact artifact, SaveCallback callback);

    void getArtifact(String lotNumber, GetCallback callback);
}