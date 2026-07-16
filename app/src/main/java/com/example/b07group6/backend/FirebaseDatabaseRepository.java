package com.example.b07group6.backend;

import androidx.annotation.NonNull;

import com.example.b07group6.construct.Artifact;
import com.example.b07group6.construct.Comment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseDatabaseRepository implements DatabaseRepository {

    private final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
    private final DatabaseReference artifactsRef = rootRef.child("artifacts");
    private final DatabaseReference likesRef = rootRef.child("likes");
    private final DatabaseReference commentsRef = rootRef.child("comments");
    private final DatabaseReference savedRef = rootRef.child("savedArtifacts");

    // ---------- Artifacts ----------

    @Override
    public void getAllArtifacts(ArtifactListCallback callback) {
        artifactsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Artifact> result = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    Artifact artifact = child.getValue(Artifact.class);
                    if (artifact != null) {
                        artifact.setLotNumber(child.getKey());
                        result.add(artifact);
                    }
                }
                callback.onSuccess(result);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailure(error.getMessage());
            }
        });
    }

    @Override
    public void getArtifact(String lotNumber, ArtifactCallback callback) {
        artifactsRef.child(lotNumber).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Artifact artifact = snapshot.getValue(Artifact.class);
                if (artifact == null) {
                    callback.onFailure("Artifact not found.");
                    return;
                }
                artifact.setLotNumber(snapshot.getKey());
                callback.onSuccess(artifact);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailure(error.getMessage());
            }
        });
    }

    @Override
    public void checkLotNumberExists(String lotNumber, BooleanCallback callback) {
        artifactsRef.child(lotNumber).get()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        callback.onFailure("Could not check lot number.");
                        return;
                    }
                    DataSnapshot snapshot = task.getResult();
                    callback.onResult(snapshot != null && snapshot.exists());
                });
    }

    @Override
    public void addArtifact(String lotNumber, Map<String, Object> artifactData, SimpleCallback callback) {
        artifactsRef.child(lotNumber).setValue(artifactData)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callback.onSuccess();
                    } else {
                        callback.onFailure("Failed to add artifact.");
                    }
                });
    }

    @Override
    public void updateArtifact(String lotNumber, Map<String, Object> artifactData, SimpleCallback callback) {
        artifactsRef.child(lotNumber).updateChildren(artifactData)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callback.onSuccess();
                    } else {
                        callback.onFailure("Failed to update artifact.");
                    }
                });
    }

    @Override
    public void deleteArtifact(String lotNumber, SimpleCallback callback) {
        artifactsRef.child(lotNumber).removeValue()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callback.onSuccess();
                    } else {
                        callback.onFailure("Failed to delete artifact.");
                    }
                });
    }

    // ---------- Likes ----------

    @Override
    public void getLikeStatus(String lotNumber, String uid, LikeStatusCallback callback) {
        likesRef.child(lotNumber).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long count = snapshot.getChildrenCount();
                boolean liked = snapshot.hasChild(uid);
                callback.onResult(count, liked);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailure(error.getMessage());
            }
        });
    }

    @Override
    public void toggleLike(String lotNumber, String uid, SimpleCallback callback) {
        DatabaseReference userLikeRef = likesRef.child(lotNumber).child(uid);
        userLikeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    userLikeRef.removeValue()
                            .addOnCompleteListener(task -> completeSimple(task, callback));
                } else {
                    userLikeRef.setValue(true)
                            .addOnCompleteListener(task -> completeSimple(task, callback));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailure(error.getMessage());
            }
        });
    }

    // ---------- Comments ----------

    @Override
    public void getComments(String lotNumber, CommentListCallback callback) {
        commentsRef.child(lotNumber).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Comment> result = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    Comment comment = child.getValue(Comment.class);
                    if (comment != null) {
                        comment.setId(child.getKey());
                        result.add(comment);
                    }
                }
                callback.onSuccess(result);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailure(error.getMessage());
            }
        });
    }

    @Override
    public void addComment(String lotNumber, String text, String username, String uid, SimpleCallback callback) {
        DatabaseReference newCommentRef = commentsRef.child(lotNumber).push();
        Map<String, Object> comment = new HashMap<>();
        comment.put("text", text);
        comment.put("username", username);
        comment.put("uid", uid);
        comment.put("timestamp", ServerValue.TIMESTAMP);

        newCommentRef.setValue(comment)
                .addOnCompleteListener(task -> completeSimple(task, callback));
    }

    @Override
    public void deleteComment(String lotNumber, String commentId, SimpleCallback callback) {
        commentsRef.child(lotNumber).child(commentId).removeValue()
                .addOnCompleteListener(task -> completeSimple(task, callback));
    }

    // ---------- Saved artifacts ----------

    @Override
    public void getSavedArtifacts(String uid, StringListCallback callback) {
        savedRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> lotNumbers = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    lotNumbers.add(child.getKey());
                }
                callback.onSuccess(lotNumbers);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailure(error.getMessage());
            }
        });
    }

    @Override
    public void toggleSaved(String uid, String lotNumber, SimpleCallback callback) {
        DatabaseReference savedItemRef = savedRef.child(uid).child(lotNumber);
        savedItemRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    savedItemRef.removeValue()
                            .addOnCompleteListener(task -> completeSimple(task, callback));
                } else {
                    savedItemRef.setValue(true)
                            .addOnCompleteListener(task -> completeSimple(task, callback));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailure(error.getMessage());
            }
        });
    }

    // ---------- Helper ----------

    private void completeSimple(com.google.android.gms.tasks.Task<Void> task, SimpleCallback callback) {
        if (task.isSuccessful()) {
            callback.onSuccess();
        } else {
            callback.onFailure("Operation failed.");
        }
    }
}