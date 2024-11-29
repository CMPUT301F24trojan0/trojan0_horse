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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

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
                            Long maxAttendees = document.getLong("max_attendees");
                            if (maxAttendees != null) {
                                numAttendees = maxAttendees.intValue(); // Set numAttendees from Firestore
                                Log.d(TAG, "Max Attendees: " + numAttendees);
                            } else {
                                Log.e(TAG, "max_attendees field does not exist");
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





    }

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




    //not using atm

    // Method to resample applicants if there are spots available in any event
    private void resampleApplicants() {
        db.collection("events")  // Fetch all events
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot eventsSnapshot = task.getResult();
                        for (QueryDocumentSnapshot eventDoc : eventsSnapshot) {
                            String eventId = eventDoc.getId();  // Get the event ID
                            Long maxAttendees = eventDoc.getLong("max_attendees");
                            Map<String, Long> users = (Map<String, Long>) eventDoc.get("users");

                            if (maxAttendees != null && users != null) {
                                int selectedCount = 0;
                                int acceptedCount = 0;

                                // Count the number of selected (1) and accepted (2) users
                                for (Long status : users.values()) {
                                    if (status == 1) selectedCount++;
                                    if (status == 2) acceptedCount++;
                                }

                                int totalAttendees = selectedCount + acceptedCount;
                                int spotsLeft = maxAttendees.intValue() - totalAttendees;

                                // If there are spots left, resample applicants from the waitlist
                                if (spotsLeft > 0) {
                                    Log.d(TAG, "Event " + eventId + " has " + spotsLeft + " spots left. Resampling applicants...");
                                    //sampleWaitlist(spotsLeft);  // Call the resampling function for this event
                                } else {
                                    Log.d(TAG, "Event " + eventId + " is full. No resampling needed.");
                                }
                            }
                        }
                    } else {
                        Log.e(TAG, "Failed to get events: ", task.getException());
                    }
                });
    }






}

