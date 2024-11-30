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

    /**
     * Retrieves all notifications for a device.
     *
     * @param deviceId The ID of the device.
     */
    public void getNotificationsForDevice(@NonNull Context context, @NonNull String deviceId) {
        Log.d("getNotifications", "Attempting to fetch notifications for device: " + deviceId);

        db.collection("users").document(deviceId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    Log.d("getNotifications", "Successfully fetched device document for notifications: " + deviceId);

                    if (documentSnapshot.exists()) {
                        Map<String, Object> notificationsQueue =
                                (Map<String, Object>) documentSnapshot.get("notificationsQueue");

                        if (notificationsQueue != null && !notificationsQueue.isEmpty()) {
                            Log.d("getNotifications", "Found notifications for device: " + deviceId);
                            // Iterate through the notifications map
                            for (Map.Entry<String, Object> entry : notificationsQueue.entrySet()) {
                                Map<String, Object> notificationData = (Map<String, Object>) entry.getValue();
                                String title = (String) notificationData.get("title");
                                String message = (String) notificationData.get("message");
                                String eventId = (String) notificationData.get("eventId");
                                String notificationId = entry.getKey();  // The key from the notificationsQueue map is the unique ID

                                Log.d("getNotifications", "Preparing to show notification with ID: " + notificationId);

                                // Create and display the notification using the unique ID
                                showNotification(deviceId, context, notificationId, title, message);
                            }
                        } else {
                            Log.d("getNotifications", "No notifications found for device: " + deviceId);
                        }
                    } else {
                        Log.e("getNotifications", "Device document does not exist for ID: " + deviceId);
                    }
                })
                .addOnFailureListener(e -> Log.e("getNotifications", "Failed to fetch notifications for device: " + deviceId + ", error: " + e));
    }

    private void showNotification(@NonNull String deviceId, Context context, @NonNull String title, @NonNull String message, @NonNull String eventId) {
        Log.d("showNotification", "Attempting to show notification for device: " + deviceId);

        // Create and display the notification using the passed title, message, and eventId
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "default")
                .setSmallIcon(android.R.drawable.ic_dialog_info) // Set a small icon
                .setContentTitle(title) // Set the notification title
                .setContentText(message) // Set the notification message
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true); // Automatically removes the notification when clicked

        // Get the NotificationManager system service
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Show the notification with a unique ID (e.g., use eventId as the ID to ensure uniqueness)
        if (notificationManager != null) {
            // Use eventId or a generated unique ID for the notification ID
            int notificationId = eventId.hashCode(); // Hash eventId to get a unique integer for the notification
            notificationManager.notify(notificationId, builder.build()); // Display the notification
            Log.d("showNotification", "Notification displayed for device: " + deviceId + " with notification ID: " + notificationId);
        } else {
            Log.e("showNotification", "NotificationManager is null, unable to display notification.");
        }
    }

}
