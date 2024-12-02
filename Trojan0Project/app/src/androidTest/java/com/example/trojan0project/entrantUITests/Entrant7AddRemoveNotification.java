package com.example.trojan0project.temp;

import static org.junit.Assert.assertNotNull;

import android.provider.Settings;
import android.util.Log;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.trojan0project.Notification;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;

@RunWith(AndroidJUnit4.class)
public class Entrant7AddRemoveNotification {
    private FirebaseFirestore db;
    private FirebaseStorage sb;
    private CollectionReference devicesRef;
    private CollectionReference eventsRef;
    private static final String LOCAL_HOST = "10.0.2.2";  // Emulator's IP on Android
    private Notification notificationService;
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

        // Initialize the Notification service (ensure this is properly set up in your code)
        notificationService = new Notification();
    }

    @Test
    public void addNotificationToQueue() throws InterruptedException {
        // Define test notification data
        String eventId = "test_event_01";
        String title = "Test Notification";
        String message = "This is a test notification.";

        // Add notification to queue
        notificationService.addNotificationToDevice(deviceID, eventId, title, message);

        // Wait for async Firestore operations to complete
        Thread.sleep(3000);  // Consider using a better approach like CountDownLatch

        // Verify the notification was added
        devicesRef.document(deviceID).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Map<String, Object> notificationsQueue =
                                (Map<String, Object>) documentSnapshot.get("notificationsQueue");

                        assert notificationsQueue != null;
                        boolean containsNotification = notificationsQueue.keySet().stream()
                                .anyMatch(key -> key.startsWith(eventId));
                        assert containsNotification;

                        Log.d("addNotificationToQueue", "Notification added successfully!");
                    } else {
                        Log.e("addNotificationToQueue", "Document does not exist for device: " + deviceID);
                    }
                })
                .addOnFailureListener(e -> Log.e("addNotificationToQueue", "Failed to verify notification: " + e.getMessage()));
    }
}
