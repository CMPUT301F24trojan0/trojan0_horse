package com.example.trojan0project.entrantUITests;

import static androidx.test.espresso.intent.Intents.release;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import android.provider.Settings;
import android.util.Log;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Map;

@RunWith(AndroidJUnit4.class)
public class Entrant113VerifyDeclinedEvent {
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
    public void verifyUserStatusDeclinedEvent() throws InterruptedException {
        // Wait for async Firestore operations to complete
        Thread.sleep(3000);  // Consider using a better approach like CountDownLatch

        // Fetch the user document from Firestore using the deviceID
        eventsRef.document("150").get()
                .addOnSuccessListener(documentSnapshot -> {
                    // Check if the document exists
                    if (documentSnapshot.exists()) {
                        // Fetch the events map stored in the user document
                        Map<String, Object> usersMap = (Map<String, Object>) documentSnapshot.get("users");

                        // Check if the eventID "150" exists and verify if the integer value is 2
                        if (usersMap != null && usersMap.containsKey(deviceID)) {
                            Long eventIDValue = (Long) usersMap.get(deviceID);
                            assertEquals("Device ID value does not match", Long.valueOf(3), eventIDValue);
                        } else {
                            fail("Device ID not found in the events map");
                        }
                    } else {
                        Log.e("verifyEntrantSignedUp", "Document does not exist for event: " + deviceID);
                        fail("Document does not exist for event: " + deviceID);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("verifyEntrantSignedUp", "Failed to verify user: " + e.getMessage());
                    fail("Failed to fetch document: " + e.getMessage());
                });
    }
}
