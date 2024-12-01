package com.example.trojan0project;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;
import androidx.core.app.NotificationCompat;

import android.app.NotificationManager;

public class Notification {

    private final FirebaseFirestore db;

    // Constructor to initialize the Firestore instance
    public Notification() {
        db = FirebaseFirestore.getInstance();
        Log.d("Notification", "Firestore instance initialized.");
    }

    /**
     * Adds a notification to a device's notification map if notifications are enabled.
     *
     * @param deviceId The ID of the device.
     * @param eventId The ID of the related event.
     * @param title The title of the notification.
     * @param message The message of the notification.
     */
    public void addNotificationToDevice(@NonNull String deviceId, @NonNull String eventId,
                                        @NonNull String title, @NonNull String message) {
        Log.d("addNotification", "Attempting to add notification for device: " + deviceId);

        // Reference the device's document
        db.collection("users").document(deviceId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    Log.d("addNotification", "Successfully fetched device document: " + deviceId);
                    if (documentSnapshot.exists()) {
                        Boolean notificationsEnabled = documentSnapshot.getBoolean("notifications");

                        if (notificationsEnabled != null && notificationsEnabled) {
                            Log.d("addNotification", "Notifications are enabled for device: " + deviceId);

                            // Notifications are enabled; add to the notifications map
                            Map<String, Object> notification = new HashMap<>();
                            notification.put("eventId", eventId);
                            notification.put("title", title);
                            notification.put("message", message);

                            // Add the notification to the device's notifications map
                            Map<String, Object> updates = new HashMap<>();
                            // Generate a unique ID by combining eventId and current timestamp
                            String uniqueNotificationId = eventId + "_" + System.currentTimeMillis();

                            updates.put("notificationsQueue." + uniqueNotificationId, notification);
                            Log.d("addNotification", "Notification data prepared for update: " + uniqueNotificationId);

                            db.collection("users").document(deviceId)
                                    .update(updates)
                                    .addOnSuccessListener(aVoid -> Log.d("addNotification", "Notification added successfully for device: " + deviceId))
                                    .addOnFailureListener(e -> Log.e("addNotification", "Failed to add notification for device: " + deviceId + ", error: " + e));
                        } else {
                            Log.d("addNotification", "Notifications are disabled for device: " + deviceId);
                        }
                    } else {
                        Log.e("addNotification", "Device document does not exist for ID: " + deviceId);
                    }
                })
                .addOnFailureListener(e -> Log.e("addNotification", "Failed to fetch device document for ID: " + deviceId + ", error: " + e));
    }

}
