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

import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

public class SystemSample extends AppCompatActivity {

    private FirebaseFirestore db;
    private ArrayAdapter<Profile> ProfileAdapter;
    private String targetEventId = "9AOwqyKOPMUO7rCZIF6V";
    //private String targetEventId = "9AOwqyKOPMUO7rCZIF6V";
    private String deviceId;

    ;

    private int numAttendees = 1;
    ListView entrantsWaitlist;

    private ListView entrantWaitlist;
    private ArrayAdapter<Profile> profileArrayAdapter;
    public ArrayList<Profile> waitList;
    public ArrayList<Profile> declinedList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entrants_join_waitlist);
        db = FirebaseFirestore.getInstance();

        waitList = new ArrayList<>();
        declinedList = new ArrayList<>();
        entrantsWaitlist = findViewById(R.id.entrants_wait_list);
        profileArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, waitList);
        entrantsWaitlist.setAdapter(profileArrayAdapter);

        Button fetchWaitlistButton = findViewById(R.id.fetchWaitlistButton);
        Button sampleWaitlistButton = findViewById(R.id.sampleWaitlistButton);



        fetchWaitlistButton.setOnClickListener(v -> {

            getWaitlist();

        });
        Log.d("WaitlistActivity", "Calling getWaitlist() method");

        sampleWaitlistButton.setOnClickListener(v -> {

            sampleWaitlist(numAttendees);

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

    private void sampleWaitlist(int numAttendees) {


        ArrayList<Profile> sampledProfiles = new ArrayList<>();

        //OpenAI, (2024, November 24), "how to randomly select the people in the waiting list??", ChatGPT

        Random random = new Random();
        for (int i = 0; i < numAttendees; i++) {
            if (waitList.size() == 0) break;
            int index = random.nextInt(waitList.size());
            Profile sampledProfile = waitList.get(index);
            sampledProfiles.add(sampledProfile);
            waitList.remove(index);
        }
       // show profiles
        for (Profile profile : sampledProfiles) {
            Log.d("Sampled Profile", "Registered: " + profile.getFirstName() + " " + profile.getLastName());
            String deviceId = profile.getDeviceId();
            if (deviceId != null) {
                updateUserStatusAfterSampling(deviceId, targetEventId);
            } else {
                Log.e(TAG, "Device ID is null for profile: " + profile.getFirstName() + " " + profile.getLastName());
            }
            updateEventsStatusInEvent(targetEventId, deviceId);

        }
        profileArrayAdapter.notifyDataSetChanged();

        Toast.makeText(this, numAttendees + " attendees have been registered.", Toast.LENGTH_SHORT).show();
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


    private void updateEventsStatusInEvent(String eventId, String deviceId) {
        // Query the 'events' collection for the document with the specified eventId
        db.collection("events")
                .whereEqualTo(FieldPath.documentId(), eventId) // Use documentId as the eventId
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        DocumentSnapshot eventDoc = task.getResult().getDocuments().get(0);

                        // Get the 'users' field, which is expected to be a map with deviceIds as keys
                        Map<String, Object> users = (Map<String, Object>) eventDoc.get("users");

                        // Check if the users map exists and contains the deviceId
                        if (users != null && users.containsKey(deviceId)) {
                            if ((Long) users.get(deviceId) == 0) {
                                users.put(deviceId, 1);
                            }

                            // Update the 'users' field in the event document
                            eventDoc.getReference().update("users", users)
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d(TAG, "Successfully updated user status in event " + eventId);
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e(TAG, "Error updating user status in event " + eventId, e);
                                    });
                        } else {
                            Log.e(TAG, "Users field not found in the event document");
                        }
                    } else {
                        Log.e(TAG, "Event not found with eventId: " + eventId);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching event document: ", e);
                });
    }






}

