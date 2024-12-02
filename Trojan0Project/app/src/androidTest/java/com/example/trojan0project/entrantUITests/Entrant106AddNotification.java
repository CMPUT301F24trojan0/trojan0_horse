package com.example.trojan0project.entrantUITests;

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
public class Entrant106AddNotification {
    private FirebaseFirestore db;
    private FirebaseStorage sb;
    private CollectionReference devicesRef;
    private CollectionReference eventsRef;
    private static final String LOCAL_HOST = "10.0.2.2";  // Emulator's IP on Android
    private Notification notificationService;
    private String EventID;
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

        eventsRef = db.collection("events");

        // Add the event data to Firestore
        EventID = "150";
        String deviceID = Settings.Secure.getString(ApplicationProvider.getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);

        // Event data for the events collection
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("eventName", "trojan0event");
        eventData.put("description", "Team trojan0's event");
        eventData.put("qrContent", "{\"id\":\"101\",\"name\":\"trojan0event\"}");

        // Add the event data to the "events" collection
        eventsRef.document(EventID).set(eventData)
                .addOnSuccessListener(aVoid -> {
                    Log.d("ViewingAllSignedUpEvents", "Event added to events collection successfully!");

                    // Add the event reference to the user's "events" map
                    devicesRef.document(deviceID).get()
                            .addOnSuccessListener(documentSnapshot -> {
                                if (documentSnapshot.exists()) {
                                    // Update the existing "events" map or create a new one
                                    Map<String, Object> userData = documentSnapshot.getData();
                                    Map<String, Object> userEvents = (Map<String, Object>) userData.get("events");
                                    if (userEvents == null) {
                                        userEvents = new HashMap<>();
                                    }
                                    userEvents.put(EventID, 1); // Example value for user signup

                                    // Update the Firestore document with user's events
                                    devicesRef.document(deviceID)
                                            .update("events", userEvents)
                                            .addOnSuccessListener(updateVoid -> {
                                                Log.d("ViewingAllSignedUpEvents", "Event added to user's events map!");

                                                // Add the user to the event's "users" map
                                                eventsRef.document(EventID).get()
                                                        .addOnSuccessListener(eventSnapshot -> {
                                                            if (eventSnapshot.exists()) {
                                                                Map<String, Object> eventUsers = (Map<String, Object>) eventSnapshot.get("users");
                                                                if (eventUsers == null) {
                                                                    eventUsers = new HashMap<>();
                                                                }
                                                                eventUsers.put(deviceID, 1); // Add the deviceID with a value of 1 (indicating user signup)

                                                                // Update the event document to include the user in the "users" map
                                                                eventsRef.document(EventID)
                                                                        .update("users", eventUsers)
                                                                        .addOnSuccessListener(userUpdateVoid -> {
                                                                            Log.d("ViewingAllSignedUpEvents", "User added to event's users map!");
                                                                        })
                                                                        .addOnFailureListener(e -> Log.e("ViewingAllSignedUpEvents", "Failed to add user to event's users map: " + e.getMessage()));
                                                            } else {
                                                                Log.e("ViewingAllSignedUpEvents", "Event document does not exist: " + EventID);
                                                            }
                                                        })
                                                        .addOnFailureListener(e -> Log.e("ViewingAllSignedUpEvents", "Failed to fetch event document: " + e.getMessage()));
                                            })
                                            .addOnFailureListener(e -> Log.e("ViewingAllSignedUpEvents", "Failed to update user's events map: " + e.getMessage()));
                                } else {
                                    // If the document does not exist, create a new one with the "events" map
                                    Map<String, Object> newUserData = new HashMap<>();
                                    Map<String, Object> newUserEvents = new HashMap<>();
                                    newUserEvents.put(EventID, 1); // Example value for user signup
                                    newUserData.put("events", newUserEvents);

                                    devicesRef.document(deviceID).set(newUserData)
                                            .addOnSuccessListener(createVoid -> Log.d("ViewingAllSignedUpEvents", "New user document created with events map!"))
                                            .addOnFailureListener(e -> Log.e("ViewingAllSignedUpEvents", "Failed to create new user document: " + e.getMessage()));
                                }
                            })
                            .addOnFailureListener(e -> Log.e("ViewingAllSignedUpEvents", "Failed to fetch user document: " + e.getMessage()));
                })
                .addOnFailureListener(e -> Log.e("ViewingAllSignedUpEvents", "Failed to add event to events collection: " + e.getMessage()));

    }

    @Test
    public void addNotificationToQueue() throws InterruptedException {
        // Define test notification data
        String title = "Test Notification";
        String message = "This is a test notification.";

        // Add notification to queue
        notificationService.addNotificationToDevice(deviceID, EventID, title, message);

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
                                .anyMatch(key -> key.startsWith(EventID));
                        assert containsNotification;

                        Log.d("addNotificationToQueue", "Notification added successfully!");
                    } else {
                        Log.e("addNotificationToQueue", "Document does not exist for device: " + deviceID);
                    }
                })
                .addOnFailureListener(e -> Log.e("addNotificationToQueue", "Failed to verify notification: " + e.getMessage()));
    }
}
