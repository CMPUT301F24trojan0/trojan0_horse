package com.example.trojan0project.entrantUITests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import android.provider.Settings;
import android.util.Log;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Map;

@RunWith(AndroidJUnit4.class)
public class Entrant116VerifyLeftWaitlistEvent {
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
    public void verifyLeftWaitlistUser() throws InterruptedException {
        // Wait for async Firestore operations to complete
        Thread.sleep(3000);  // Consider using a better approach like CountDownLatch

        // Fetch the event document from Firestore using the event ID
        eventsRef.document("150").get()
                .addOnSuccessListener(documentSnapshot -> {
                    // Check if the document exists
                    if (documentSnapshot.exists()) {
                        // Fetch the users associated with the event (assuming it's stored as a map or list)
                        Map<String, Object> usersMap = (Map<String, Object>) documentSnapshot.get("users");

                        // Check if the usersMap is not null and if it contains the deviceID
                        if (usersMap != null && usersMap.containsKey(deviceID)) {
                            // If the deviceID is found, fail the test
                            fail("EventID '150' should not be associated with the device ID " + deviceID);
                        } else {
                            // If the deviceID is not found, the test passes
                            Log.d("verifyEventNotAssociatedWithUser", "EventID '150' is not associated with device ID " + deviceID);
                        }
                    } else {
                        Log.e("verifyEventNotAssociatedWithUser", "Event document does not exist for EventID '150'");
                        fail("Event document does not exist for EventID '150'");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("verifyEventNotAssociatedWithUser", "Failed to verify event: " + e.getMessage());
                    fail("Failed to fetch event document: " + e.getMessage());
                });
    }
}
