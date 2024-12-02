package com.example.trojan0project.entrantUITests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import android.provider.Settings;
import android.util.Log;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Map;

@RunWith(AndroidJUnit4.class)
public class Entrant109VerifyAcceptedUser {
    private FirebaseFirestore db;
    private FirebaseStorage sb;
    private CollectionReference devicesRef;
    private CollectionReference eventsRef;
    private static final String LOCAL_HOST = "10.0.2.2";  // Emulator's IP on Android
    private final String username = "trojan0";
    private final String email = "trojan0@gmail.com";
    private final String deviceID = Settings.Secure.getString(ApplicationProvider.getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);

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
    public void verifyEventStatusAcceptedUser() throws InterruptedException {
        // Wait for async Firestore operations to complete
        Thread.sleep(3000);  // Consider using a better approach like CountDownLatch

        // Fetch the user document from Firestore using the deviceID
        devicesRef.document(deviceID).get()
                .addOnSuccessListener(documentSnapshot -> {
                    // Check if the document exists
                    if (documentSnapshot.exists()) {
                        // Fetch the events map stored in the user document
                        Map<String, Object> eventsMap = (Map<String, Object>) documentSnapshot.get("events");

                        // Check if the eventID "150" exists and verify if the integer value is 2
                        if (eventsMap != null && eventsMap.containsKey("150")) {
                            Long eventIDValue = (Long) eventsMap.get("150");
                            assertEquals("Event ID value does not match", Long.valueOf(2), eventIDValue);
                        } else {
                            fail("EventID '150' not found in the events map");
                        }
                    } else {
                        Log.e("verifyEntrantSignedUp", "Document does not exist for device: " + deviceID);
                        fail("Document does not exist for device: " + deviceID);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("verifyEntrantSignedUp", "Failed to verify user: " + e.getMessage());
                    fail("Failed to fetch document: " + e.getMessage());
                });
    }
}
