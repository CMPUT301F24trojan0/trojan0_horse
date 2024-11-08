/**
 * Purpose:
 * This activity retrieves and displays a list of events associated with a specific device ID.
 * It fetches events from Firestore based on the device ID, and allows the user to view event details
 * by clicking on any event in the list, which opens a dialog to show the event status.
 *
 * Design Rationale:
 * The activity uses a RecyclerView to display a list of events and an EventAdapter
 * to bind the event data to the list. When an event is clicked, a StatusFragment is displayed
 * to show additional details about the selected event. Events are retrieved from Firestore
 * and displayed based on their participation status.
 *
 * Outstanding Issues:
 * No Issues.
 */
package com.example.trojan0project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ViewEvents extends AppCompatActivity implements EventAdapter.OnEventClickListener {

    private static final String TAG = "ViewEvents";
    private FirebaseFirestore db;
    private RecyclerView eventsRecyclerView;
    private EventAdapter eventAdapter;
    private List<Event> eventList;
    private String deviceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_events);

        // Retrieve the device ID from the intent
        Intent intent = getIntent();
        deviceId = intent.getStringExtra("DEVICE_ID");

        if (deviceId == null) {
            Log.e(TAG, "Device ID not received in ViewEvents");
            Toast.makeText(this, "Error: Missing Device ID.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize RecyclerView and event list
        eventsRecyclerView = findViewById(R.id.recyclerView);
        eventsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        eventList = new ArrayList<>();

        // Set up adapter and attach it to RecyclerView
        eventAdapter = new EventAdapter(eventList);
        eventAdapter.setOnEventClickListener(this);  // Set the click listener
        eventsRecyclerView.setAdapter(eventAdapter);

        // Retrieve events from Firestore
        retrieveEvents();
    }

    @Override
    public void onEventClick(Event event) {
        // Create a new StatusFragment
        StatusFragment statusFragment = new StatusFragment();

        // Create a bundle to pass the deviceId
        Bundle args = new Bundle();
        args.putString("DEVICE_ID", deviceId);  // Pass device ID to fragment
        args.putString("EVENT_ID", event.getEventId()); // Pass the unique event ID to the fragment
        statusFragment.setArguments(args);

        // Show the fragment as a dialog (overlay)
        statusFragment.show(getSupportFragmentManager(), "StatusFragment");
    }


    private void retrieveEvents() {
        if (deviceId == null) {
            Log.e(TAG, "Device ID is null. Cannot retrieve events.");
            Toast.makeText(this, "Error: Device ID is missing.", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("users").document(deviceId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Log.d(TAG, "Document retrieved: " + document.getData());
                            if (document.contains("events")) {
                                Map<String, Long> eventsMap = (Map<String, Long>) document.get("events");

                                int[] counter = {0}; // Initialize a counter to track processed events
                                int totalEventsToFetch = (int) eventsMap.entrySet().stream().filter(entry -> entry.getValue() == 1).count(); // Track total events with status 1

                                // Fetch each event detail
                                for (Map.Entry<String, Long> entry : eventsMap.entrySet()) {
                                    String eventId = entry.getKey();
                                    Long participationStatus = entry.getValue();

                                    // Check if the event has a value of 1
                                    if (participationStatus == 1) {
                                        Log.d(TAG, "Fetching event with ID: " + eventId);

                                        // Fetch event details from the "events" collection using eventId
                                        db.collection("events").document(eventId)
                                                .get()
                                                .addOnCompleteListener(eventTask -> {
                                                    if (eventTask.isSuccessful()) {
                                                        DocumentSnapshot eventDocument = eventTask.getResult();
                                                        if (eventDocument.exists()) {
                                                            String eventName = eventDocument.getString("name");
                                                            if (eventName != null) {
                                                                double defaultLatitude = 0.0;
                                                                double defaultLongitude = 0.0;
                                                                String defaultPosterPath = "";
                                                                eventList.add(new Event(eventId, eventName, defaultLatitude, defaultLongitude, defaultPosterPath));
                                                            } else {
                                                                Log.d(TAG, "Event name is missing for event ID: " + eventId);
                                                            }
                                                        } else {
                                                            Log.d(TAG, "Event document does not exist for event ID: " + eventId);
                                                        }
                                                    } else {
                                                        Log.e(TAG, "Error fetching event details: ", eventTask.getException());
                                                    }

                                                    // Increment the counter and check if all events are processed
                                                    counter[0]++;
                                                    if (counter[0] == totalEventsToFetch) {
                                                        eventList.add(new Event("end", "--End of events list--", 0.0, 0.0, ""));
                                                        // Notify the adapter only once all events are added
                                                        eventAdapter.notifyDataSetChanged();
                                                    }
                                                });
                                    }
                                }

                                if (totalEventsToFetch == 0) {
                                    Toast.makeText(ViewEvents.this, "No events found with status 1.", Toast.LENGTH_SHORT).show();
                                }

                            } else {
                                Log.d(TAG, "No events found for this user.");
                                Toast.makeText(ViewEvents.this, "No events found for this user.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.d(TAG, "Document does not exist.");
                            Toast.makeText(ViewEvents.this, "No events found for this user.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e(TAG, "Error retrieving events: ", task.getException());
                        Toast.makeText(ViewEvents.this, "Failed to retrieve events.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Firestore retrieval failed: ", e);
                    Toast.makeText(ViewEvents.this, "Firestore retrieval failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

}

