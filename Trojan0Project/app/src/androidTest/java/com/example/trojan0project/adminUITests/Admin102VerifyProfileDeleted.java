package com.example.trojan0project.adminUITests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import android.util.Log;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class Admin102VerifyProfileDeleted {
    private FirebaseFirestore db;
    private FirebaseStorage sb;
    private CollectionReference devicesRef;
    private CollectionReference eventsRef;
    private static final String LOCAL_HOST = "10.0.2.2";  // Emulator's IP on Android
    private final String entrantDeviceID = "100";

    @Before
    public void setup() {
        // Set up Firebase to use the emulator
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance();

        // Firebase Firestore and Firebase Storage setup
        db = FirebaseFirestore.getInstance();
        devicesRef = db.collection("users");
        eventsRef = db.collection("events");
    }

    @Test
    public void verifyUserDeleted() throws InterruptedException {
        // Wait for async Firestore operations to complete
        Thread.sleep(3000);  // Consider using a better approach like CountDownLatch

        // Fetch the user document from Firestore using the deviceID
        devicesRef.document(entrantDeviceID).get()
                .addOnSuccessListener(documentSnapshot -> {
                    // Check if the document exists
                    if (documentSnapshot.exists()) {
                        Log.e("verifyUserDeleted", "Document exists for device: " + entrantDeviceID);
                        fail("Document should not exist for device: " + entrantDeviceID);
                    } else {
                        Log.i("verifyUserDeleted", "Document successfully deleted for device: " + entrantDeviceID);
                        // Test passes since the document is deleted
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("verifyUserDeleted", "Failed to verify document deletion: " + e.getMessage());
                    fail("Failed to fetch document: " + e.getMessage());
                });
    }
}
