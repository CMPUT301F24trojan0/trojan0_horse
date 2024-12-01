/**
 * Purpose:
 * This class handles the sampling process for selecting attendees from a waitlist for an event.
 * It randomly selects profiles from the waitlist, updates their registration status in Firestore,
 * and updates the event document with the selected attendees.
 *
 * Design Rationale:
 * Firestore is used to store and retrieve user and event data. The sampling process is done randomly,
 * and batch updates are used to ensure efficient updating of the event document with the selected attendees.
 *
 * Outstanding Issues:
 * No issues at the moment.
 */
package com.example.trojan0project;

import static android.content.ContentValues.TAG;

import android.util.Log;
import android.widget.ArrayAdapter;

import androidx.annotation.Nullable;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.Map;
import java.util.Random;


public class SamplerImplementation {

    private FirebaseFirestore db;
    /**
     * Initializes the SamplerImplementation instance and sets up Firestore.
     */
    public SamplerImplementation() {
        db = FirebaseFirestore.getInstance();  // Initialize Firestore
    }

    /**
     * Samples attendees from the waitlist based on the specified number of attendees.
     * Updates the status of the selected users and the event document with the new attendees.
     *
     * @param waitList The list of profiles representing the waitlist of users.
     * @param numAttendees The number of attendees to sample from the waitlist.
     * @param targetEventId The ID of the target event for which attendees are being sampled.
     * @param profileArrayAdapter The ArrayAdapter used to update the ListView displaying the waitlist.
     */

    public void sampleWaitlist(ArrayList<Profile> waitList, int numAttendees, String targetEventId, ArrayAdapter<Profile> profileArrayAdapter) {


        ArrayList<Profile> sampledProfiles = new ArrayList<>();
        ArrayList<String> deviceIdsToUpdate = new ArrayList<>();

        //OpenAI, (2024, November 24), "how to randomly select the people in the waiting list??", ChatGPT

        Random random = new Random();
        for (int i = 0; i < numAttendees; i++) {
            if (waitList.size() == 0) break;
            int index = random.nextInt(waitList.size());
            Profile sampledProfile = waitList.get(index);
            sampledProfiles.add(sampledProfile);
            waitList.remove(index);

            String deviceId = sampledProfile.getDeviceId();
            if (deviceId != null) {
                deviceIdsToUpdate.add(deviceId);  // Add deviceId to the list
            } else {
                Log.e(TAG, "Device ID is null for profile: " + sampledProfile.getFirstName() + " " + sampledProfile.getLastName());
            }
        }
        updateNumSampledInEvent(targetEventId, numAttendees);
        updateEventsStatusInEvent(targetEventId, deviceIdsToUpdate);
        // show profiles
        for (Profile profile : sampledProfiles) {
            Log.d("Sampled Profile", "Registered: " + profile.getFirstName() + " " + profile.getLastName());
            String deviceId = profile.getDeviceId();

            if (deviceId != null) {
                // Update user status for each profile
                updateUserStatusAfterSampling(deviceId, targetEventId);
            }

        }
        profileArrayAdapter.notifyDataSetChanged();

        // After updating sampled users, notify those with status 0
        notifyDevicesWithStatusZero(targetEventId);

        //Toast.makeText(this, numAttendees + " attendees have been registered.", Toast.LENGTH_SHORT).show();
    }

    private void notifyDevicesWithStatusZero(String eventId) {
        // Fetch the event name from the events collection
        db.collection("events")
                .document(eventId)
                .get()
                .addOnSuccessListener(eventDoc -> {
                    if (eventDoc.exists()) {
                        String eventName = eventDoc.getString("eventName");
                        if (eventName != null) {
                            // Event name is successfully retrieved
                            String title = "You've Lost!";
                            String message = "Better luck next time for the event: " + eventName + "!";

                            // Now fetch all users and check their status for the event
                            db.collection("users")
                                    .get()
                                    .addOnSuccessListener(queryDocumentSnapshots -> {
                                        for (DocumentSnapshot doc : queryDocumentSnapshots) {
                                            String deviceId = doc.getId(); // Device ID is the document ID
                                            Map<String, Object> events = (Map<String, Object>) doc.get("events");

                                            if (events != null && events.containsKey(eventId)) {
                                                Long status = (Long) events.get(eventId);
                                                if (status != null && status == 0) {
                                                    // Send "You've Lost" notification to users with status 0
                                                    Notification notificationHelper = new Notification();
                                                    notificationHelper.addNotificationToDevice(deviceId, eventId, title, message);
                                                }
                                            }
                                        }
                                    })
                                    .addOnFailureListener(e -> Log.e(TAG, "Error fetching users for notification: ", e));
                        } else {
                            Log.e(TAG, "Event name is missing for event ID: " + eventId);
                        }
                    } else {
                        Log.e(TAG, "Event document does not exist for event ID: " + eventId);
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error fetching event details: ", e));
    }


    /**
     * Updates the registration status of a user after they are selected from the waitlist.
     *
     * @param deviceId The device ID of the user to update.
     * @param eventId The ID of the event for which the user's status is being updated.
     */

    private void updateUserStatusAfterSampling(String deviceId, String eventId) {
        db.collection("users")
                .document(deviceId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        DocumentSnapshot doc = task.getResult();
                        Log.d(TAG, "Device ID: " + deviceId);
                        Map<String, Object> events = (Map<String, Object>) doc.get("events");

                        if (events != null && events.containsKey(eventId)) {
                            if ((Long) events.get(eventId) == 0) {
                                // Update the event status to 1
                                events.put(eventId, 1);
                                doc.getReference().update("events", events)
                                        .addOnSuccessListener(aVoid -> {
                                            Log.d(TAG, "Event status updated successfully for " + deviceId);

                                            // After updating status, fetch event name from the events collection
                                            db.collection("events").document(eventId)
                                                    .get()
                                                    .addOnSuccessListener(eventDocument -> {
                                                        if (eventDocument.exists()) {
                                                            String eventName = eventDocument.getString("eventName");
                                                            if (eventName != null) {
                                                                // Construct the message
                                                                String title = "Lottery Win!";
                                                                String message = "You have won the lottery for " + eventName + "!";

                                                                // Call the addNotificationToDevice function
                                                                Notification notificationHelper = new Notification();
                                                                notificationHelper.addNotificationToDevice(deviceId, eventId, title, message);
                                                            } else {
                                                                Log.d(TAG, "Event name is missing for event ID: " + eventId);
                                                            }
                                                        } else {
                                                            Log.d(TAG, "Event document does not exist for event ID: " + eventId);
                                                        }
                                                    })
                                                    .addOnFailureListener(e -> Log.e(TAG, "Error fetching event details: ", e));
                                        })
                                        .addOnFailureListener(e -> {
                                            Log.e(TAG, "Error updating event status for " + deviceId, e);
                                        });
                            }
                        }
                    } else {
                        Log.e(TAG, "Error fetching document: ", task.getException());
                    }
                });
    }


    //OpenAI, (2024, November 29), "how update multiple fields of a map in the same document at the same time", ChatGPT
    /**
     * Updates the status of selected users in the event document using batch processing.
     * This method ensures that multiple user statuses can be updated in one operation.
     *
     * @param eventId The ID of the event.
     * @param deviceIds A list of device IDs to update their status in the event document.
     */

    private void updateEventsStatusInEvent(String eventId, ArrayList<String> deviceIds) {
        // Create a WriteBatch to group all updates together
        WriteBatch batch = db.batch();

        // Reference to the event document
        DocumentReference eventRef = db.collection("events").document(eventId);

        // Loop through each deviceId and add the update operation to the batch
        for (String deviceId : deviceIds) {
            // Add update operation for each deviceId to set their status in the event document
            batch.update(eventRef, "users." + deviceId, 1);  // Update the user status to 1
        }

        // Commit the batch
        batch.commit()
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Batch update successful for event: " + eventId);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error during batch update for event: " + eventId, e);
                });
    }

    /**
     * Updates the number of sampled attendees in the event document.
     * This method will add the new `num_sampled` field if it doesn't exist.
     *
     * @param eventId The ID of the event.
     * @param numSampled The number of sampled attendees to update in the event document.
     */
    private void updateNumSampledInEvent(String eventId, int numSampled) {
        DocumentReference eventRef = db.collection("events").document(eventId);

        // Get the document to check if num_sampled already exists
        eventRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                DocumentSnapshot document = task.getResult();
                Long existingNumSampled = document.getLong("num_sampled");

                // Check if num_sampled is already set (greater than 0)
                if (existingNumSampled == null || existingNumSampled == 0) {
                    // Only update if num_sampled hasn't been set or is 0
                    eventRef.update("num_sampled", FieldValue.increment(numSampled))
                            .addOnSuccessListener(aVoid -> {
                                Log.d(TAG, "Number of sampled attendees updated successfully for event: " + eventId);
                            })
                            .addOnFailureListener(e -> {
                                Log.e(TAG, "Error updating num_sampled field for event: " + eventId, e);
                            });
                } else {
                    Log.d(TAG, "num_sampled already set. Skipping update.");
                }
            } else {
                Log.e(TAG, "Error fetching event document: ", task.getException());
            }
        });
    }






}
