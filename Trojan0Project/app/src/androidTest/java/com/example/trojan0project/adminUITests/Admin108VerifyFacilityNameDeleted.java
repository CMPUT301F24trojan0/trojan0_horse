package com.example.trojan0project.adminUITests;

import static org.junit.Assert.fail;

import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import org.junit.Before;
import org.junit.Test;

public class Admin108VerifyFacilityNameDeleted {
    private FirebaseFirestore db;
    private FirebaseStorage sb;
    private CollectionReference devicesRef;
    private CollectionReference eventsRef;
    private static final String LOCAL_HOST = "10.0.2.2";  // Emulator's IP on Android
    private final String organizerDeviceID = "300";

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
    public void verifyFacilityDeleted() throws InterruptedException {
        // Wait for async Firestore operations to complete
        Thread.sleep(3000);  // Replace with CountDownLatch for better async handling

        // Fetch the facility document from Firestore using the organizerDeviceID
        devicesRef.document(organizerDeviceID).get()
                .addOnSuccessListener(documentSnapshot -> {
                    // Check if the document exists
                    if (documentSnapshot.exists()) {
                        Log.i("verifyFacilityDeleted", "Facility document exists for ID: " + organizerDeviceID);

                        // Check if the facilityName field is deleted
                        if (!documentSnapshot.contains("facilityName")) {
                            Log.i("verifyFacilityDeleted", "Facility name successfully deleted for ID: " + organizerDeviceID);
                            // Test passes since the facilityName field is deleted
                        } else {
                            Object facilityName = documentSnapshot.get("facilityName");
                            if (facilityName == null) {
                                Log.i("verifyFacilityDeleted", "Facility name field is null for ID: " + organizerDeviceID);
                                // Test passes since facilityName is explicitly set to null
                            } else {
                                Log.e("verifyFacilityDeleted", "Facility name still exists and is not null for ID: " + organizerDeviceID);
                                fail("Facility name should be deleted or null for ID: " + organizerDeviceID);
                            }
                        }
                    } else {
                        Log.e("verifyFacilityDeleted", "Facility document does not exist for ID: " + organizerDeviceID);
                        fail("Facility document should exist for ID: " + organizerDeviceID);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("verifyFacilityDeleted", "Failed to verify facility document: " + e.getMessage());
                    fail("Failed to fetch facility document: " + e.getMessage());
                });
    }
}
