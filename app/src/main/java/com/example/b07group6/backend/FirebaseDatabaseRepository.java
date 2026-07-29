package com.example.b07group6.backend;

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
        artifactsRef.child(lotNumber).child("artifactName").get()
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
                        callback.onFailure("Failed to create artifact.");
                    }
                });
    }


    /** Note: This deletes the artifact and its associated data, but NOT the image  */
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
                    updates.put("comments/byCommentID/" + child.getKey(), null);
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
     * if type == Artifact, then typeID is the lotNumber
     * if type == Comment, then typeID is the commentID
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
                        callback.onResult(count, liked);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callback.onFailure(error.getMessage());
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
                callback.onFailure(error.getMessage());
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
                    callback.onSuccess(new ArrayList<>());
                    return;
                }
                List<Task<DataSnapshot>> fetches = new ArrayList<>();
                for (String id : commentIds) {
                    fetches.add(commentsRef.child("byCommentID").child(id).get());
                }
                Tasks.whenAllSuccess(fetches)
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
                            callback.onSuccess(result);
                        })
                        .addOnFailureListener(e -> callback.onFailure("Could not load comments."));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailure(error.getMessage());
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
                    callback.onFailure("Artifact not found.");
                    return;
                }
                comment.setId(snapshot.getKey());
                callback.onSuccess(comment);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailure(error.getMessage());
            }
        });
    }

    @Override
    public void addComment(String lotNumber, String text, String username, String uid, SimpleCallback callback) {
        String newCommentId = commentsRef.child("byCommentID").push().getKey();
        if (newCommentId == null) {
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

    @Override
    public void getSavedArtifacts(String uid, StringListCallback callback) {
        savedRef.child("byUserID").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
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