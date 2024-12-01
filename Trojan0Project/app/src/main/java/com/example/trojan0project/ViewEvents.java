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
    /**
     * Initializes the activity, retrieves the device ID, sets up Firestore, and initializes the RecyclerView.
     *
     * @param savedInstanceState The saved state of the activity.
     */
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
    /**
     * Handles the click event on an event item.
     * Opens a StatusFragment to allow the user to accept or decline the event.
     *Opens a WaitlistFragment to allow the user to choose to leave the waitlist for the event if they are currently on it.
     * @param event The event that was clicked.
     */
    @Override
    public void onEventClick(Event event) {
        db.collection("users").document(deviceId)
                .get()
                .addOnSuccessListener(document -> {
                    if (document.exists() && document.contains("events")) {
                        Map<String, Long> eventsMap = (Map<String, Long>) document.get("events");
                        Long status = eventsMap.get(event.getEventId());

                        if (status != null) {
                            if (status == 0) { // Waitlisted
                                // Open WaitlistFragment for waitlisted events
                                WaitlistFragment waitlistFragment = new WaitlistFragment();

                                Bundle args = new Bundle();
                                args.putString("DEVICE_ID", deviceId);
                                args.putString("EVENT_ID", event.getEventId());
                                waitlistFragment.setArguments(args);

                                waitlistFragment.show(getSupportFragmentManager(), "WaitlistFragment");
                            } else if (status == 1) { // Selected
                                // Open StatusFragment for selected events
                                StatusFragment statusFragment = new StatusFragment();

                                Bundle args = new Bundle();
                                args.putString("DEVICE_ID", deviceId);
                                args.putString("EVENT_ID", event.getEventId());
                                statusFragment.setArguments(args);

                                statusFragment.show(getSupportFragmentManager(), "StatusFragment");
                            } else {
                                Toast.makeText(this, "This event has no associated action.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(this, "Event status not found.", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error fetching event status.", Toast.LENGTH_SHORT).show();
                });
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
                                // Safely cast the events map
                                Map<String, Object> eventsMap = (Map<String, Object>) document.get("events");

                                for (Map.Entry<String, Object> entry : eventsMap.entrySet()) {
                                    String eventId = entry.getKey();

                                    // Safely convert the value to Long
                                    Long value = null;
                                    try {
                                        value = ((Number) entry.getValue()).longValue();
                                    } catch (ClassCastException e) {
                                        Log.e(TAG, "Error casting event value to Long for eventId: " + eventId, e);
                                    }

                                    if (value != null) {
                                        // Fetch event details from the "events" collection using eventId
                                        db.collection("events").document(eventId)
                                                .get()
                                                .addOnCompleteListener(eventTask -> {
                                                    if (eventTask.isSuccessful()) {
                                                        DocumentSnapshot eventDocument = eventTask.getResult();
                                                        if (eventDocument.exists()) {
                                                            String eventName = eventDocument.getString("eventName");
                                                            double latitude = eventDocument.contains("latitude")
                                                                    ? eventDocument.getDouble("latitude")
                                                                    : 0.0;
                                                            double longitude = eventDocument.contains("longitude")
                                                                    ? eventDocument.getDouble("longitude")
                                                                    : 0.0;
                                                            String posterPath = eventDocument.getString("posterPath");

                                                            // Create Event object and add to the list
                                                            Event event = new Event(eventName, eventId, latitude, longitude, posterPath);
                                                            eventList.add(event);
                                                        }
                                                    }
                                                    eventAdapter.notifyDataSetChanged();
                                                });
                                    }
                                }
                            } else {
                                Toast.makeText(ViewEvents.this, "No events found for this user.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(ViewEvents.this, "No events found for this user.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(ViewEvents.this, "Failed to retrieve events.", Toast.LENGTH_SHORT).show();
                    }
                });
    }



}

