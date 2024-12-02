package com.example.trojan0project;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;
import androidx.core.app.NotificationCompat;

import android.app.NotificationManager;

/**
 * Purpose:
 * The `Notification` class is responsible for managing user notifications in the application.
 * It interacts with Firebase Firestore to store, retrieve, and manage notifications for individual devices.
 * Notifications are displayed to the user and linked to specific events, allowing easy navigation to event details.
 *
 * Design Rationale:
 * - Utilizes Firebase Firestore for efficient storage and retrieval of notifications.
 * - Implements a queue-based approach for managing device notifications, ensuring scalability and flexibility.
 * - Integrates with Android's notification system using `NotificationCompat` to provide user-friendly, clickable notifications.
 * - Supports automatic removal of displayed notifications from Firestore to maintain a clean queue.
 *
 * Outstanding Issues:
 * - No known issues at this time.
 */

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
                                showNotification(context, deviceId, eventId, title, message, notificationId);

                                // After the notification is shown, remove the notification ID from the Firestore map
                                deleteNotificationFromQueue(deviceId, notificationId);
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

    private void deleteNotificationFromQueue(@NonNull String deviceId, @NonNull String notificationId) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("notificationsQueue." + notificationId, FieldValue.delete()); // Use FieldValue.delete() to remove the entry
        db.collection("users").document(deviceId)
                .update(updates)
                .addOnSuccessListener(aVoid -> Log.d("deleteNotification", "Notification removed successfully: " + notificationId))
                .addOnFailureListener(e -> Log.e("deleteNotification", "Failed to remove notification: " + notificationId + ", error: " + e));
    }

    private void showNotification(@NonNull Context context, @NonNull String deviceId,
                                  @NonNull String eventId, @NonNull String title, @NonNull String message,
                                  @NonNull String notificationId) {
        // Intent to open the event details activity and remove the notification when clicked
        Intent intent = new Intent(context, EventDetailsActivityOrganizer.class);
        intent.putExtra("eventId", eventId);
        intent.putExtra("passId", eventId);

        // Create a PendingIntent to launch the event details activity
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                eventId.hashCode(), // Unique request code for each notification
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "default")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)  // When clicked, it opens the event activity
                .setAutoCancel(true);  // Automatically dismiss the notification after being clicked

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager != null) {
            notificationManager.notify(notificationId.hashCode(), builder.build());
        }
    }
}