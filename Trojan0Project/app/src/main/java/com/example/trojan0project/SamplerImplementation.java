package com.example.trojan0project;

import static android.content.ContentValues.TAG;

import android.util.Log;
import android.widget.ArrayAdapter;

import androidx.annotation.Nullable;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
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

    public SamplerImplementation() {
        db = FirebaseFirestore.getInstance();  // Initialize Firestore
    }

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

        //Toast.makeText(this, numAttendees + " attendees have been registered.", Toast.LENGTH_SHORT).show();
    }



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
                                events.put(eventId, 1);
                                doc.getReference().update("events", events)
                                        .addOnSuccessListener(aVoid -> {
                                            Log.d(TAG, "Event status updated successfully for " + deviceId);
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





}
