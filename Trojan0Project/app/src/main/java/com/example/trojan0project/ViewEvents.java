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
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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

    /**
     * Initializes the activity, retrieves the device ID, sets up Firestore, and initializes the RecyclerView.
     *
     * @param savedInstanceState The saved state of the activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_events);

        Toolbar toolbar = findViewById(R.id.view_events_toolbar);
        setSupportActionBar(toolbar);

        // Set the title of the action bar to be empty
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);  // Enable the "up" button
        }

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

    /**
     * Handles the click event on an event item.
     * Opens a StatusFragment to allow the user to accept or decline the event.
     *
     * @param event The event that was clicked.
     */
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

    /**
     * Handles the selection of menu items, specifically the "home" button (up navigation).
     * This method is called when an item in the options menu is selected.
     *
     * @param item The menu item that was selected.
     * @return True if the menu item is handled, false otherwise.
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Save profile data before navigating back
            finish(); // Finish the current activity and return to the previous one
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Retrieves events for the user from Firestore based on their device ID.
     * Updates the RecyclerView with the retrieved events.
     */
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
                                int totalEventsToFetch = eventsMap.size(); // Total number of events to fetch

                                // Fetch each event detail
                                for (Map.Entry<String, Long> entry : eventsMap.entrySet()) {
                                    String eventId = entry.getKey();
                                    Long participationStatus = entry.getValue();

                                    // Fetch event details from the "events" collection using eventId
                                    db.collection("events").document(eventId)
                                            .get()
                                            .addOnCompleteListener(eventTask -> {
                                                if (eventTask.isSuccessful()) {
                                                    DocumentSnapshot eventDocument = eventTask.getResult();
                                                    if (eventDocument.exists()) {
                                                        String eventName = eventDocument.getString("eventName");
                                                        if (eventName != null) {
                                                            double defaultLatitude = 0.0;
                                                            double defaultLongitude = 0.0;
                                                            String defaultPosterPath = "";
                                                            eventList.add(new Event(eventName, eventId, defaultLatitude, defaultLongitude, defaultPosterPath));

                                                            // Trigger the fragment popup only for events with participation status 1
                                                            if (participationStatus == 1) {
                                                                // You can add a listener for clicks here (the assumption is you want to handle the click in your adapter)
                                                                eventAdapter.setOnEventClickListener(event -> {
                                                                    if (event.getEventId().equals(eventId) && participationStatus == 1) {
                                                                        // Create a new StatusFragment
                                                                        StatusFragment statusFragment = new StatusFragment();

                                                                        // Create a bundle to pass the deviceId and eventId
                                                                        Bundle args = new Bundle();
                                                                        args.putString("DEVICE_ID", deviceId);  // Pass device ID to fragment
                                                                        args.putString("EVENT_ID", eventId); // Pass the unique event ID to the fragment
                                                                        statusFragment.setArguments(args);

                                                                        // Show the fragment as a dialog (overlay)
                                                                        statusFragment.show(getSupportFragmentManager(), "StatusFragment");
                                                                    }
                                                                });
                                                            }
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
                                                    // Notify the adapter only once all events are added
                                                    eventAdapter.notifyDataSetChanged();
                                                }
                                            });
                                }

                                if (totalEventsToFetch == 0) {
                                    Toast.makeText(ViewEvents.this, "No events found for this user.", Toast.LENGTH_SHORT).show();
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