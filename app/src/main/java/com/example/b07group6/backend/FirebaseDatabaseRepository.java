package com.example.b07group6.backend;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import com.example.b07group6.construct.Artifact;
import com.example.b07group6.construct.Comment;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FirebaseDatabaseRepository implements DatabaseRepository {

    private final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
    private final DatabaseReference artifactsRef = rootRef.child("artifacts");
    private final DatabaseReference likesRef = rootRef.child("likes");
    private final DatabaseReference likeCountsRef = rootRef.child("artifactLikeCounts");
    private final DatabaseReference commentsRef = rootRef.child("comments");
    private final DatabaseReference savedRef = rootRef.child("savedArtifacts");
    // We don't want to keep nesting callbacks, so instead, create a reference to
    // background (NOT MAIN) threadx, so we can use Google's task api to block on an
    // action in the background thread. By making this a class field, we give ourselves
    // the ability to delegate any action onto this specific thread pool when it is free.
    private final ExecutorService asyncExec = Executors.newCachedThreadPool();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    private List<Artifact> asyncGetAllArtifacts() throws Exception {
        DataSnapshot snapshot = Tasks.await(artifactsRef.get());
        List<Artifact> result = new ArrayList<>();
        for (DataSnapshot child : snapshot.getChildren()) {
            Artifact artifact = child.getValue(Artifact.class);
            if (artifact != null) {
                artifact.setLotNumber(child.getKey());
                result.add(artifact);
            }
        }
        return result;
    }

    private Artifact asyncGetArtifact(String lotNumber) throws Exception {
        DataSnapshot snapshot = Tasks.await(artifactsRef.child(lotNumber).get());
        Artifact artifact = snapshot.getValue(Artifact.class);
        if (artifact != null) {
            artifact.setLotNumber(snapshot.getKey());
        }
        return artifact;
    }

    @Override
    public void getAllArtifacts(ArtifactListCallback callback) {
        // asyncExec.execute(() -> {
        //     try {
        //         List<Artifact> artifacts = this.asyncGetAllArtifacts();
        //         mainHandler.post(() -> callback.onSuccess(artifacts));
        //     } catch (Exception e) {
        //         mainHandler.post(() -> callback.onFailure(e.getMessage()));
        //     }
        // });
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
        //asyncExec.execute(() -> {
        //    try {
        //        Artifact artifact = this.asyncGetArtifact(lotNumber);
        //        if (artifact == null)
        //            mainHandler.post(() -> callback.onFailure("Artifact not found."));
        //        else
        //            mainHandler.post(() -> callback.onSuccess(artifact));
        //    } catch (Exception e) {
        //        mainHandler.post(() -> callback.onFailure(e.getMessage()));
        //    }
        //});


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
    public void saveArtifact(String lotNumber, Map<String, Object> artifactData, SimpleCallback callback) {
        artifactsRef.child(lotNumber).updateChildren(artifactData)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callback.onSuccess();
                    } else {
                        callback.onFailure("Failed to save artifact.");
                    }
                });
    }


    @Override
    // Note: This deletes the artifact and its associated data, but NOT the image
    public void deleteArtifact(String lotNumber, SimpleCallback callback) {
        // When deleting an artifact, we must:
        asyncExec.execute(() -> {
            try {
                // Task to delete the artifact entry
                Task<Void> artifactTask = artifactsRef.child(lotNumber).removeValue();
                // Task to delete the likes associated with the artifact
                Task<Void> likesTask = likesRef.child("artifacts").child(lotNumber).removeValue();
                // Task to delete all comments under the artifact.
                Task<Void> commentsTask = commentsRef.child("byLotNumber").child(lotNumber).get().continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        mainHandler.post(() -> callback.onFailure("Could not find comments to delete."));
                        return null;
                    }
                    DataSnapshot dataSnapshot = task.getResult();
                    List<Task<Void>> commentTasks = new ArrayList<>();
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        String commentID = child.getValue(String.class);
                        if (commentID == null)
                            continue;
                        commentTasks.add(commentsRef.child("byID").child(commentID).removeValue());
                    }
                    Tasks.await(Tasks.whenAllComplete(commentTasks));
                    Tasks.await(commentsRef.child("byLotNumber").child(lotNumber).removeValue());
                    return null;
                });
                // Task to remove artifact from user's saved lists
                Task<Void> savedTask = savedRef.child("byLotNumber").child(lotNumber).get().continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        mainHandler.post(() -> callback.onFailure("Could not check saved."));
                        return null;
                    }
                    DataSnapshot dataSnapshot = task.getResult();
                    List<Task<Void>> uidTasks = new ArrayList<>();
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        String uid = child.getValue(String.class);
                        if (uid == null)
                            continue;
                        uidTasks.add(savedRef.child("byID").child(uid).removeValue());
                    }
                    Tasks.await(Tasks.whenAllComplete(uidTasks));
                    Tasks.await(savedRef.child("byLotNumber").child(lotNumber).removeValue());
                    return null;
                });
                Tasks.await(Tasks.whenAllComplete(artifactTask, likesTask, commentsTask, savedTask));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        });
    }

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

    @Override
    public void getSavedArtifacts(String uid, StringListCallback callback) {
        savedRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> lotNumbers = new ArrayList<>();
                List<String> staleLotNumbers = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    String lotNumber = child.getKey();
                    if (lotNumber == null)
                        continue;
                    artifactsRef.child(lotNumber).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Artifact artifact = snapshot.getValue(Artifact.class);
                            if (artifact == null) {
                                callback.onFailure("Artifact not found.");
                                return;
                            }
                            artifact.setLotNumber(snapshot.getKey());
                            //callback.onSuccess(artifact);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            callback.onFailure(error.getMessage());
                        }
                    });
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

    private void completeSimple(com.google.android.gms.tasks.Task<Void> task, SimpleCallback callback) {
        if (task.isSuccessful()) {
            callback.onSuccess();
        } else {
            callback.onFailure("Operation failed.");
        }
    }
}