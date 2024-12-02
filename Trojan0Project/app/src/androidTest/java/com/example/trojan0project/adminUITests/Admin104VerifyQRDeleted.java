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
public class Admin104VerifyQRDeleted {
    private FirebaseFirestore db;
    private FirebaseStorage sb;
    private CollectionReference devicesRef;
    private CollectionReference eventsRef;
    private static final String LOCAL_HOST = "10.0.2.2";  // Emulator's IP on Android
    private final String EventID = "150";

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
    public void verifyEventQRDeleted() throws InterruptedException {
        // Wait for async Firestore operations to complete
        Thread.sleep(3000);  // Replace with CountDownLatch for better async handling

        // Fetch the event document from Firestore using the organizerDeviceID
        eventsRef.document(EventID).get()
                .addOnSuccessListener(documentSnapshot -> {
                    // Check if the document exists
                    if (documentSnapshot.exists()) {
                        Log.i("verifyEventQRDeleted", "Document exists for event: " + EventID);

                        String eventNameFromFirestore = documentSnapshot.getString("eventName");
                        assertNotNull("Username should not be null", eventNameFromFirestore);
                        assertEquals("Event Name does not match", "trojan0event", eventNameFromFirestore);

                        // Check if the QR code field exists and is null
                        if (documentSnapshot.contains("qrContent")) {
                            Object qrContent = documentSnapshot.get("qrContent");
                            if (qrContent == null) {
                                Log.i("verifyEventQRDeleted", "QR code field exists but is set to null for event: " + EventID);
                                // Test passes since the QR code is correctly set to null
                            } else {
                                Log.e("verifyEventQRDeleted", "QR code field exists and is not null for event: " + EventID);
                                fail("QR code field should be null for event: " + EventID);
                            }
                        } else {
                            Log.e("verifyEventQRDeleted", "QR code field does not exist for event: " + EventID);
                            fail("QR code field should exist but be null for event: " + EventID);
                        }
                    } else {
                        Log.e("verifyEventQRDeleted", "Event document does not exist: " + EventID);
                        fail("Event document should exist for event: " + EventID);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("verifyEventQRDeleted", "Failed to verify event document: " + e.getMessage());
                    fail("Failed to fetch event document: " + e.getMessage());
                });
    }

}
