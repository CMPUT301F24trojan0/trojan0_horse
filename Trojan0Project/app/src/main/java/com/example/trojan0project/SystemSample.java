package com.example.trojan0project;

//farza: user stories:  02.02.01, 02.05.02
import static android.content.ContentValues.TAG;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

/**
 * Purpose:
 * This activity allows users to view and sample a waitlist for event entrants.
 * It interacts with Firestore to fetch event and user data and displays a waitlist of users
 * who have shown interest in an event.
 *
 * Design Rationale:
 * The app retrieves data from Firebase Firestore to build a waitlist for an event based on user type.
 * Once data is fetched, it is displayed in a ListView. Sampling functionality is provided for event organizers
 * to select users for an event based on the waitlist.
 *
 * Outstanding Issues:
 * No issues at the moment.
 */

public class SystemSample extends AppCompatActivity {

    private FirebaseFirestore db;
    private ArrayAdapter<Profile> ProfileAdapter;
    private String targetEventId = "9AOwqyKOPMUO7rCZIF6V";
    //private String targetEventId = "9AOwqyKOPMUO7rCZIF6V";
    private String deviceId;



    private int numAttendees;
    ListView entrantsWaitlist;

    private ListView entrantWaitlist;
    private static ArrayAdapter<Profile> profileArrayAdapter;
    public ArrayList<Profile> waitList;

    /**
     * Initializes the activity, sets up Firebase Firestore, and configures the layout with
     * appropriate listeners for fetching and sampling the waitlist.
     *
     * @param savedInstanceState The saved state of the activity.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entrants_join_waitlist);
        db = FirebaseFirestore.getInstance();

        waitList = new ArrayList<>();

        entrantsWaitlist = findViewById(R.id.entrants_wait_list);
        profileArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, waitList);
        entrantsWaitlist.setAdapter(profileArrayAdapter);

        Button fetchWaitlistButton = findViewById(R.id.fetchWaitlistButton);
        Button sampleWaitlistButton = findViewById(R.id.sampleWaitlistButton);

        // getting the number of attendees for that specifc event
        db.collection("events")
                .document(targetEventId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            // Retrieve the max_attendees field
                            Long maxAttendees = document.getLong("maxNumberofEntrants");
                            if (maxAttendees != null) {
                                numAttendees = maxAttendees.intValue(); // Set numAttendees from Firestore
                                Log.d(TAG, "Max Attendees: " + numAttendees);
                            } else {
                                Log.e(TAG, "maxNumberofEntrants field does not exist");
                            }
                        } else {
                            Log.e(TAG, "Event document does not exist");
                        }
                    } else {
                        Log.e(TAG, "Failed to get event document: ", task.getException());
                    }
                });




        fetchWaitlistButton.setOnClickListener(v -> {

            getWaitlist();

        });
        Log.d("WaitlistActivity", "Calling getWaitlist() method");

        sampleWaitlistButton.setOnClickListener(v -> {

            SamplerImplementation sampler = new SamplerImplementation();
            sampler.sampleWaitlist(waitList, numAttendees, targetEventId, profileArrayAdapter);


        });
        Log.d("sampleWaitlistActivity", "Calling sampleWaitlist() method");



        resamplingTwo(targetEventId);



    }
    /**
     * Fetches the waitlist of entrants for the specified event from Firestore.
     * It retrieves users marked as entrants for the event and adds them to the waitlist.
     * The adapter is notified of changes to update the ListView display.
     */
    private void getWaitlist() {
        final CollectionReference collectionReference = db.collection("users");



        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {


                Log.d("Waitlist", "onEvent triggered");

                waitList.clear();


                for (QueryDocumentSnapshot doc: queryDocumentSnapshots){
                    String userType = doc.getString("user_type");
                    if ("entrant".equals(userType)){
                        Map<String, Long> events = (Map<String, Long>) doc.get("events");

                        if (events != null) {
                            for (Map.Entry<String, Long> entry : events.entrySet()) {

                                if (entry.getValue() == 0 && entry.getKey().equals(targetEventId)) {
                                    String eventId = entry.getKey(); // This is the event ID
                                    Log.d("Waitlist", "Event ID with 0: " + eventId);
                                    String firstName = doc.getString("first_name");
                                    String lastName = doc.getString("last_name");
                                    String email = doc.getString("email");
                                    String deviceId = doc.getId();

                                   //create profile
                                    Profile profile = new Profile(firstName, lastName, email, deviceId);
                                    waitList.add(profile);
                                    Log.d("Waitlist", "Added Profile: " + profile.getFirstName() + " " + profile.getLastName());



                                }



                            }
                        }

                    }
                }
                profileArrayAdapter.notifyDataSetChanged();

                for (Profile profile : waitList) {
                    Log.d("Waitlist", "First Name: " + profile.getFirstName() +
                            ", Last Name: " + profile.getLastName() +
                            ", Email: " + profile.getEmail() +
                            ", Device ID: " + profile.getDeviceId());
                }
            }


        });




    }


    /**
     * Purpose:
     * This method listens for changes to the specified event document in Firestore.
     * It checks if there are available spots for attendees in the event ( if
     * the number of sampled attendees is less than the maximum number of attendees).
     * If there are still spots available, it triggers the resampling process by
     * selecting more attendees from the waitlist and incrementing the `num_sampled` field
     * in Firestore. The resampling process continues until the maximum number of attendees
     * is reached.
     *
     * Design Rationale:
     * The method uses a snapshot listener to actively listen for changes to the event
     * document in real-time, ensuring that updates to the event’s data are reflected
     * immediately in the application. This enables active resampling based on the
     * number of attendees already sampled and the available spots in the event.
     *
     * Outstanding Issues:
     * No issues
     *
     * @param targetEventId The ID of the target event for which attendees are being sampled.
     */

    private void resamplingTwo(String targetEventId) {
        // Use snapshot listener to actively listen for changes to the event document
        db.collection("events")
                .document(targetEventId)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.e(TAG, "Error fetching event document: ", error);
                            return;
                        }

                        if (documentSnapshot != null && documentSnapshot.exists()) {
                            Long maxAttendees = documentSnapshot.getLong("maxNumberofEntrants");
                            Long numSampled = documentSnapshot.getLong("num_sampled");

                            Log.d(TAG, "maxAttendees: " + maxAttendees);
                            Log.d(TAG, "numSampled: " + numSampled);

                            // Ensure that the fields are available and check if resampling is needed
                            if (maxAttendees != null && numSampled != null) {
                                if (numSampled < maxAttendees) {
                                    // Trigger resampling if maxAttendees is not reached
                                    int remainingAttendees = maxAttendees.intValue() - numSampled.intValue();
                                    Log.d(TAG, "Resampling " + remainingAttendees + " attendees...");

                                    SamplerImplementation sampler = new SamplerImplementation();
                                    sampler.sampleWaitlist(waitList, remainingAttendees, targetEventId, profileArrayAdapter);

                                    // Increment num_sampled field in Firestore after resampling
                                    db.collection("events")
                                            .document(targetEventId)
                                            .update("num_sampled", FieldValue.increment(remainingAttendees))
                                            .addOnSuccessListener(aVoid -> {
                                                Log.d(TAG, "num_sampled field incremented by " + remainingAttendees);
                                            })
                                            .addOnFailureListener(e -> {
                                                Log.e(TAG, "Error incrementing num_sampled field for event: " + targetEventId, e);
                                            });
                                } else {
                                    Log.d(TAG, "Max attendees reached. No need to resample.");
                                }
                            } else {
                                Log.e(TAG, "Failed to retrieve maxAttendees or numSampled from event document.");
                            }
                        } else {
                            Log.e(TAG, "Event document is null or does not exist.");
                        }
                    }
                });
    }






}

