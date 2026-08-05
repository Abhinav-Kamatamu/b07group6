package com.example.b07group6;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * One-shot instrumented test that seeds fake comments and likes into Firebase.
 *
 * Place this file in:
 *   app/src/androidTest/java/com/example/b07group6/SeedDatabaseTest.java
 *
 * Run ONLY this file:
 *   ./gradlew connectedAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.example.b07group6.SeedDatabaseTest
 *
 * Or right-click -> Run in Android Studio.
 *
 * DELETE THIS FILE after seeding.
 */
public class SeedDatabaseTest {

    private static final String TAG = "SeedDB";
    private final Random random = new Random(42);

    private static final String[][] USERS = {
            {"0bye53tEBraGie0AAZzODE7OgII2", "adiDaBoss"},
            {"6VKr3ggudfNwpTF5nkvULmRidRh1", "admin"},
            {"8CMgFUbZOMhAB77ThsH8JsyAjm32", "dummy1"},
    };

    private static final String[] COMMENT_POOL = {
            "This artifact is incredible! The craftsmanship is remarkable.",
            "I saw something similar at the British Museum last year.",
            "Does anyone know the exact dating for this piece?",
            "The condition is surprisingly good for its age.",
            "Love the detail on this one. Truly a masterpiece.",
            "My professor mentioned this dynasty in our last lecture.",
            "Would love to see this in person some day!",
            "The provenance history is fascinating.",
            "How was this acquired? Very interesting piece.",
            "Reminds me of pieces from the Song dynasty collection.",
            "The material composition is quite unusual for this period.",
            "Beautiful preservation. Museum-quality for sure.",
            "I wrote a paper on artifacts like this one in undergrad.",
            "Can anyone compare this to the one in the Met?",
            "Stunning piece. The cultural significance is immense.",
            "Great find! This deserves more attention.",
            "The patina tells such a rich story.",
            "Interesting dimensions. Smaller than I expected.",
            "This belongs in a special exhibition.",
            "Absolutely breathtaking. Thanks for sharing!",
            "The symmetry on this is almost perfect.",
            "I wonder what tools were used to create this.",
            "Any related pieces in the same collection?",
            "The description could mention the regional style influence.",
            "One of the best examples I've seen from this period.",
    };

    @Test
    public void seedCommentsAndLikes() throws InterruptedException {
        // Firebase auto-initializes via FirebaseInitProvider — no manual init needed
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();

        // ── Step 1: Fetch all artifact lot numbers from the database ───
        List<String> lotNumbers = fetchAllLotNumbers(rootRef);
        if (lotNumbers.isEmpty()) {
            throw new AssertionError("No artifacts found in the database. Nothing to seed.");
        }
        Log.d(TAG, "Found " + lotNumbers.size() + " artifacts: " + lotNumbers);

        // ── Step 2: Build the entire seed payload ──────────────────────
        Map<String, Object> megaUpdate = new HashMap<>();
        List<String> allCommentIds = new ArrayList<>();

        for (String lotNumber : lotNumbers) {
            int numComments = 2 + random.nextInt(5);
            for (int i = 0; i < numComments; i++) {
                String commentId = rootRef.child("comments").child("byCommentID").push().getKey();
                if (commentId == null) continue;
                allCommentIds.add(commentId);

                String[] user = USERS[random.nextInt(USERS.length)];
                String text = COMMENT_POOL[random.nextInt(COMMENT_POOL.length)];
                long timestamp = System.currentTimeMillis()
                        - (long) random.nextInt(30 * 24 * 60 * 60) * 1000L;

                Map<String, Object> commentData = new HashMap<>();
                commentData.put("text", text);
                commentData.put("username", user[1]);
                commentData.put("uid", user[0]);
                commentData.put("timestamp", timestamp);

                megaUpdate.put("comments/byCommentID/" + commentId, commentData);
                megaUpdate.put("comments/byLotNumber/" + lotNumber + "/" + commentId, true);
                megaUpdate.put("likes/forComments/" + commentId + "/count", 0);

                Log.d(TAG, "Comment: " + lotNumber + " <- " + user[1] + ": "
                        + text.substring(0, Math.min(40, text.length())) + "...");
            }
        }

        for (String lotNumber : lotNumbers) {
            int numLikes = 1 + random.nextInt(USERS.length);
            List<Integer> indices = shuffledIndices();
            for (int i = 0; i < numLikes; i++) {
                String uid = USERS[indices.get(i)][0];
                megaUpdate.put("likes/forArtifacts/" + lotNumber + "/users/" + uid, true);
            }
            Log.d(TAG, "Artifact likes: " + lotNumber + " x" + numLikes);
        }

        for (String commentId : allCommentIds) {
            int numLikes = random.nextInt(USERS.length);
            List<Integer> indices = shuffledIndices();
            for (int i = 0; i < numLikes; i++) {
                String uid = USERS[indices.get(i)][0];
                megaUpdate.put("likes/forComments/" + commentId + "/users/" + uid, true);
            }
        }

        // ── Step 3: Push everything in one atomic write ────────────────
        Log.d(TAG, "Pushing " + megaUpdate.size() + " entries to Firebase...");
        pushUpdate(rootRef, megaUpdate);
        Log.d(TAG, "Done! You can delete SeedDatabaseTest.java now.");
    }

    private List<String> fetchAllLotNumbers(DatabaseReference rootRef) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        List<String> lotNumbers = new ArrayList<>();
        final String[] error = {null};

        rootRef.child("artifacts").get()
                .addOnSuccessListener(snapshot -> {
                    for (DataSnapshot child : snapshot.getChildren()) {
                        lotNumbers.add(child.getKey());
                    }
                    latch.countDown();
                })
                .addOnFailureListener(e -> {
                    error[0] = e.getMessage();
                    latch.countDown();
                });

        if (!latch.await(15, TimeUnit.SECONDS)) {
            throw new AssertionError("Timed out reading artifacts from Firebase.");
        }
        if (error[0] != null) {
            throw new AssertionError("Failed to read artifacts: " + error[0]);
        }
        return lotNumbers;
    }

    private void pushUpdate(DatabaseReference rootRef, Map<String, Object> update) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        final String[] error = {null};

        rootRef.updateChildren(update)
                .addOnSuccessListener(unused -> {
                    Log.d(TAG, "DATABASE SEEDED SUCCESSFULLY");
                    latch.countDown();
                })
                .addOnFailureListener(e -> {
                    error[0] = e.getMessage();
                    latch.countDown();
                });

        if (!latch.await(30, TimeUnit.SECONDS)) {
            throw new AssertionError("Firebase write timed out after 30 seconds.");
        }
        if (error[0] != null) {
            throw new AssertionError("Firebase write failed: " + error[0]);
        }
    }

    private List<Integer> shuffledIndices() {
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < USERS.length; i++) indices.add(i);
        Collections.shuffle(indices, random);
        return indices;
    }
}