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

public class FirebaseDatabaseRepository implements DatabaseRepository {

    private final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
    private final DatabaseReference artifactsRef = rootRef.child("artifacts");
    private final DatabaseReference likesRef = rootRef.child("likes");
    private final DatabaseReference commentsRef = rootRef.child("comments");
    private final DatabaseReference savedRef = rootRef.child("saved");
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

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
                mainHandler.post(() -> callback.onSuccess(result));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                mainHandler.post(() -> callback.onFailure(error.getMessage()));
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
                    mainHandler.post(() -> callback.onFailure("Artifact not found."));
                    return;
                }
                artifact.setLotNumber(snapshot.getKey());
                mainHandler.post(() -> callback.onSuccess(artifact));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                mainHandler.post(() -> callback.onFailure(error.getMessage()));
            }
        });
    }

    @Override
    public void checkLotNumberExists(String lotNumber, BooleanCallback callback) {
        artifactsRef.child(lotNumber).child("artifactName").get()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        mainHandler.post(() -> callback.onFailure("Could not check lot number."));
                        return;
                    }
                    DataSnapshot snapshot = task.getResult();
                    mainHandler.post(() -> callback.onSuccess(snapshot != null && snapshot.exists()));
                });
    }

    @Override
    public void saveArtifact(SaveArtifactMode mode, String lotNumber, Map<String, Object> artifactData, SimpleCallback callback) {
        Map<String, Object> updates = new HashMap<>();
        for (Map.Entry<String, Object> entry : artifactData.entrySet()) {
            updates.put("artifacts/" + lotNumber + "/" + entry.getKey(), entry.getValue());
        }
        if (mode == SaveArtifactMode.CREATE) {
            updates.put("likes/forArtifacts/" + lotNumber + "/count", 0);
        }
        rootRef.updateChildren(updates)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callback.onSuccess();
                    } else {
                        mainHandler.post(() -> callback.onFailure("Failed to create artifact."));
                    }
                });
    }


    /**
     * Note: This deletes the artifact and its associated data, but NOT the image of the artifact.
     * Please use the SupabaseImageRepository to delete the image first (or at least have the image
     * url) before deleting the artifact
     * */
    @Override
    public void deleteArtifact(String lotNumber, SimpleCallback callback) {
        Task<DataSnapshot> commentsTask = commentsRef.child("byLotNumber").child(lotNumber).get();
        Task<DataSnapshot> savedTask = savedRef.child("byLotNumber").child(lotNumber).get();
        Tasks.whenAllSuccess(commentsTask, savedTask)
            .addOnSuccessListener(results -> {
                DataSnapshot commentsSnapshot = (DataSnapshot) results.get(0);
                DataSnapshot savedSnapshot = (DataSnapshot) results.get(1);

                Map<String, Object> updates = new HashMap<>();
                updates.put("artifacts/" + lotNumber, null);
                updates.put("likes/forArtifacts/" + lotNumber, null);
                updates.put("comments/byLotNumber/" + lotNumber, null);
                for (DataSnapshot child : commentsSnapshot.getChildren()) {
                    String commentID = child.getKey();
                    updates.put("comments/byCommentID/" + commentID, null);
                    updates.put("likes/forComments/" + commentID, null);
                }
                updates.put("saved/byLotNumber/" + lotNumber, null);
                for (DataSnapshot child : savedSnapshot.getChildren()) {
                    updates.put("saved/byUserID/" + child.getKey() + "/" + lotNumber, null);
                }
                rootRef.updateChildren(updates)
                        .addOnCompleteListener(task -> completeSimple(task, callback));
            }).addOnFailureListener(
                    e -> callback.onFailure("Could not gather artifact data for deletion.")
            );
    }

    /**
     * If you want to get the number of like for an Artifact,
     * set type = LikeType.ARTIFACT and typeID = lotNumber.
     * If you want to get the number of like for a Comment,
     * set type = LikeType.COMMENT and typeID as the commentID
     * */
    @Override
    public void getLikeStatus(LikeType type, String typeID, String uid, LikeStatusCallback callback) {
        String childName = type == LikeType.ARTIFACT ? "forArtifacts" : "forComments";
        likesRef.child(childName).child(typeID).child("users")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        long count = snapshot.getChildrenCount();
                        boolean liked = snapshot.hasChild(uid);
                        mainHandler.post(() -> callback.onSuccess(count, liked));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        mainHandler.post(() -> callback.onFailure(error.getMessage()));
                    }
                });
    }

    /**
     * if type == Artifact, then typeID is the lotNumber
     * if type == Comment, then typeID is the commentID
     * */
    @Override
    public void toggleLike(LikeType type, String typeID, String uid, SimpleCallback callback) {
        String childName = type == LikeType.ARTIFACT ? "forArtifacts" : "forComments";
        DatabaseReference userLikeRef = likesRef.child(childName).child(typeID).child("users").child(uid);
        userLikeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean exists = snapshot.exists();
                Map<String, Object> updates = new HashMap<>();
                updates.put(childName + "/" + typeID + "/users/" + uid, exists ? null : true);
                likesRef.updateChildren(updates)
                        .addOnCompleteListener(task -> completeSimple(task, callback));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                mainHandler.post(() -> callback.onFailure(error.getMessage()));
            }
        });
    }

    @Override
    public void getAllComments(String lotNumber, CommentListCallback callback) {
        commentsRef.child("byLotNumber").child(lotNumber).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> commentIds = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    commentIds.add(child.getKey());
                }
                if (commentIds.isEmpty()) {
                    mainHandler.post(() -> callback.onSuccess(new ArrayList<>()));
                    return;
                }
                List<Task<DataSnapshot>> commentTasks = new ArrayList<>();
                for (String id : commentIds) {
                    commentTasks.add(commentsRef.child("byCommentID").child(id).get());
                }
                Tasks.whenAllSuccess(commentTasks)
                        .addOnSuccessListener(results -> {
                            List<Comment> result = new ArrayList<>();
                            for (Object obj : results) {
                                DataSnapshot commentSnap = (DataSnapshot) obj;
                                Comment comment = commentSnap.getValue(Comment.class);
                                if (comment != null) {
                                    comment.setId(commentSnap.getKey());
                                    result.add(comment);
                                }
                            }
                            mainHandler.post(() -> callback.onSuccess(result));
                        })
                        .addOnFailureListener(e -> mainHandler.post(() -> callback.onFailure("Could not load comments.")));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                mainHandler.post(() -> callback.onFailure(error.getMessage()));
            }
        });
    }

    @Override
    public void getComment(String commentID, CommentCallback callback) {
        commentsRef.child("byCommentID").child(commentID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Comment comment = snapshot.getValue(Comment.class);
                if (comment == null) {
                    mainHandler.post(() -> callback.onFailure("Artifact not found."));
                    return;
                }
                comment.setId(snapshot.getKey());
                mainHandler.post(() -> callback.onSuccess(comment));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                mainHandler.post(() -> callback.onFailure(error.getMessage()));
            }
        });
    }

    @Override
    public void addComment(String lotNumber, String text, String username, String uid, SimpleCallback callback) {
        String newCommentId = commentsRef.child("byCommentID").push().getKey();
        if (newCommentId == null) {
            // only use mainHandler in asynchronous listeners
            callback.onFailure("Could not generate comment ID.");
            return;
        }

        Map<String, Object> comment = new HashMap<>();
        comment.put("text", text);
        comment.put("username", username);
        comment.put("uid", uid);
        comment.put("timestamp", ServerValue.TIMESTAMP);

        Map<String, Object> updates = new HashMap<>();
        updates.put("byCommentID/" + newCommentId, comment);
        updates.put("byLotNumber/" + lotNumber + "/" + newCommentId, true);
        updates.put("likes/forComments/" + newCommentId + "/count", 0);

        commentsRef.updateChildren(updates)
                .addOnCompleteListener(task -> completeSimple(task, callback));
    }

    @Override
    public void deleteComment(String lotNumber, String commentId, SimpleCallback callback) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("byCommentID/" + commentId, null);
        updates.put("byLotNumber/" + lotNumber + "/" + commentId, null);

        commentsRef.updateChildren(updates)
                .addOnCompleteListener(task -> completeSimple(task, callback));
    }

    /**
     * if type == Artifact, then typeID is the lotNumber
     * if type == Comment, then typeID is the commentID
     * */
    public void getNumComments(String lotNumber, CommentCountCallback callback) {
        commentsRef.child("byLotNumber").child(lotNumber)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        long count = snapshot.getChildrenCount();
                        mainHandler.post(() -> callback.onSuccess(count));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        mainHandler.post(() -> callback.onFailure(error.getMessage()));
                    }
                });
    }

    @Override
    public void getSavedArtifacts(String uid, StringListCallback callback) {
        savedRef.child("byUserID").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> lotNumbers = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    lotNumbers.add(child.getKey());
                }
                mainHandler.post(() -> callback.onSuccess(lotNumbers));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                mainHandler.post(() -> callback.onFailure(error.getMessage()));
            }
        });
    }

    @Override
    public void getSavedArtifactsList(String uid, ArtifactListCallback callback) {
        savedRef.child("byUserID").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> lotNumbers = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    lotNumbers.add(child.getKey());
                }
                if (lotNumbers.isEmpty()) {
                    mainHandler.post(() -> callback.onSuccess(new ArrayList<>()));
                    return;
                }
                List<Task<DataSnapshot>> artifactTasks = new ArrayList<>();
                for (String lotNumber : lotNumbers) {
                    artifactTasks.add(artifactsRef.child(lotNumber).get());
                }
                Tasks.whenAllSuccess(artifactTasks)
                        .addOnSuccessListener(results -> {
                            List<Artifact> result = new ArrayList<>();
                            for (Object obj : results) {
                                DataSnapshot artifactSnap = (DataSnapshot) obj;
                                Artifact artifact = artifactSnap.getValue(Artifact.class);
                                if (artifact != null) {
                                    artifact.setLotNumber(artifactSnap.getKey());
                                    result.add(artifact);
                                }
                            }
                            mainHandler.post(() -> callback.onSuccess(result));
                        })
                        .addOnFailureListener(e -> mainHandler.post(() -> callback.onFailure("Could not load artifacts.")));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                mainHandler.post(() -> callback.onFailure(error.getMessage()));
            }
        });
    }

    @Override
    public void toggleSaved(String uid, String lotNumber, SimpleCallback callback) {
        DatabaseReference byUserRef = savedRef.child("byUserID").child(uid).child(lotNumber);
        byUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean exists = snapshot.exists();
                Map<String, Object> updates = new HashMap<>();
                updates.put("byUserID/" + uid + "/" + lotNumber, exists ? null : true);
                updates.put("byLotNumber/" + lotNumber + "/" + uid, exists ? null : true);
                savedRef.updateChildren(updates)
                        .addOnCompleteListener(task -> completeSimple(task, callback));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                mainHandler.post(() -> callback.onFailure(error.getMessage()));
            }
        });
    }

    private void completeSimple(com.google.android.gms.tasks.Task<Void> task, SimpleCallback callback) {
        if (task.isSuccessful()) {
            mainHandler.post(() -> callback.onSuccess());
        } else {
            mainHandler.post(() -> callback.onFailure("Operation failed."));
        }
    }
}