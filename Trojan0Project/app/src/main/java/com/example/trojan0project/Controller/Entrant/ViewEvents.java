package com.example.trojan0project.Controller.Entrant;

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

import com.example.trojan0project.Model.Event;
import com.example.trojan0project.R;
import com.example.trojan0project.View.CommonViews.EventAdapter;
import com.example.trojan0project.View.Entrant.StatusFragment;
import com.example.trojan0project.View.Entrant.WaitlistFragment;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

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

public class ViewEvents extends AppCompatActivity implements EventAdapter.OnEventClickListener {

    private static final String TAG = "ViewEvents";
    private FirebaseFirestore db;
    private RecyclerView eventsRecyclerView;
    private EventAdapter eventAdapter;
    private List<Event> eventList;
    private String deviceId;
    private Spinner statusSpinner;
    private int selectedStatus = -1; // Default: All
    private Long participationStatus;

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
        eventAdapter.setOnEventClickListener(this);  // 'this' refers to the ViewEvents activity
        eventsRecyclerView.setAdapter(eventAdapter);

        // Set up Spinner
        statusSpinner = findViewById(R.id.statusSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.participation_statuses, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statusSpinner.setAdapter(adapter);

        statusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            /**
             * Called when an item is selected in the status spinner.
             * It sets the `selectedStatus` variable according to the selected position
             * and triggers the `retrieveEvents()` method to reload events based on the updated filter.
             *
             * @param parent The AdapterView where the selection was made.
             * @param view The view within the AdapterView that was clicked.
             * @param position The position of the item clicked in the spinner.
             * @param id The row ID of the item clicked.
             */
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        selectedStatus = -1; // All
                        break;
                    case 1:
                        selectedStatus = 0;
                        break;
                    case 2:
                        selectedStatus = 1;
                        break;
                    case 3:
                        selectedStatus = 2;
                        break;
                    case 4:
                        selectedStatus = 3;
                        break;
                }
                retrieveEvents(); // Reload events based on filter
            }

            /**
             * Called when no item is selected in the status spinner.
             * This method sets the `selectedStatus` to -1 (All) and triggers
             * the `retrieveEvents()` method to reload all events without a filter.
             *
             * @param parent The AdapterView where no item was selected.
             */
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Default to All
                selectedStatus = -1;
                retrieveEvents();
            }
        });
    }

    /**
     * Handles the click event on an event item.
     * Opens a StatusFragment to allow the user to accept or decline the event.
     * Opens a WaitlistFragment to allow the user to choose to leave the waitlist for the event if they are currently on it.
     * @param event The event that was clicked.
     */
    @Override
    public void onEventClick(Event event) {
        Log.d(TAG, "onEventClick triggered for Event: " + event.getEventId() + ", Name: " + event.getEventName());

        // Fetch the participation status from Firestore dynamically using the eventId
        db.collection("users").document(deviceId) // Use the deviceId to locate the user
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists() && document.contains("events")) {
                            Log.d(TAG, "User events found in Firestore document.");

                            Map<String, Long> eventsMap = (Map<String, Long>) document.get("events");
                            if (eventsMap != null && eventsMap.containsKey(event.getEventId())) {
                                // Get participation status for the specific eventId
                                Long participationStatus = eventsMap.get(event.getEventId());

                                // Check if participation status matches the condition
                                if (participationStatus != null && participationStatus == 1) {
                                    Log.d(TAG, "Event matches participation status 1. Proceeding to show StatusFragment.");

                                    // Create and show StatusFragment
                                    StatusFragment statusFragment = new StatusFragment();

                                    // Pass deviceId and eventId to fragment
                                    Bundle args = new Bundle();
                                    args.putString("DEVICE_ID", deviceId);
                                    args.putString("EVENT_ID", event.getEventId());
                                    statusFragment.setArguments(args);

                                    // Show the fragment
                                    statusFragment.show(getSupportFragmentManager(), "StatusFragment");
                                } else if (participationStatus != null && participationStatus == 0) {
                                    // Open WaitlistFragment for waitlisted events
                                    WaitlistFragment waitlistFragment = new WaitlistFragment();

                                    Bundle args = new Bundle();
                                    args.putString("DEVICE_ID", deviceId);
                                    args.putString("EVENT_ID", event.getEventId());
                                    waitlistFragment.setArguments(args);

                                    waitlistFragment.show(getSupportFragmentManager(), "WaitlistFragment");
                                }
                                else {
                                    Log.d(TAG, "Event does not match participation status 0 or 1. Ignoring click.");
                                }
                            } else {
                                Log.d(TAG, "No participation status found for eventId: " + event.getEventId());
                            }
                        } else {
                            Log.w(TAG, "No events found for Device ID: " + deviceId);
                        }
                    } else {
                        Log.e(TAG, "Error retrieving user data: ", task.getException());
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching user data: ", e);
                });
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
     * Updates the RecyclerView with the retrieved events and shows a StatusFragment
     * for events with participation status 1.
     */
    private void retrieveEvents() {
        if (deviceId == null) {
            Log.e(TAG, "Device ID is null. Cannot retrieve events.");
            Toast.makeText(this, "Error: Device ID is missing.", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d(TAG, "Retrieving events for Device ID: " + deviceId);

        db.collection("users").document(deviceId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists() && document.contains("events")) {
                            Log.d(TAG, "Events found in Firestore document.");

                            Map<String, Long> eventsMap = (Map<String, Long>) document.get("events");
                            eventList.clear(); // Clear current events
                            eventAdapter.notifyDataSetChanged(); // Update RecyclerView

                            for (Map.Entry<String, Long> entry : eventsMap.entrySet()) {
                                String eventId = entry.getKey();
                                participationStatus = entry.getValue();

                                Log.d(TAG, "Processing eventId: " + eventId + ", Status: " + participationStatus);
                                Log.d(TAG, "participationStatus " + participationStatus);
                                if (selectedStatus == -1 || participationStatus == selectedStatus) {
                                    Log.d(TAG, "Event matches filter criteria. Fetching event details for: " + eventId);

                                    // Fetch event details
                                    db.collection("events").document(eventId)
                                            .get()
                                            .addOnCompleteListener(eventTask -> {
                                                if (eventTask.isSuccessful()) {
                                                    DocumentSnapshot eventDocument = eventTask.getResult();
                                                    if (eventDocument.exists()) {
                                                        String eventName = eventDocument.getString("eventName");
                                                        if (eventName != null) {
                                                            Log.d(TAG, "Event details retrieved: " + eventName);

                                                            double defaultLatitude = 0.0;
                                                            double defaultLongitude = 0.0;
                                                            String defaultPosterPath = "";
                                                            Event event = new Event(eventName, eventId, defaultLatitude, defaultLongitude, defaultPosterPath);
                                                            eventList.add(event);

                                                            Log.d(TAG, "Event added to the list: " + event.getEventId());
                                                        }
                                                    } else {
                                                        Log.w(TAG, "Event document does not exist for ID: " + eventId);
                                                    }
                                                } else {
                                                    Log.e(TAG, "Error fetching event details for ID: " + eventId, eventTask.getException());
                                                }
                                                eventAdapter.notifyDataSetChanged(); // Update RecyclerView
                                            });
                                } else {
                                    Log.d(TAG, "Event does not match filter criteria. Skipping eventId: " + eventId);
                                }
                            }
                        } else {
                            Log.w(TAG, "No events found for Device ID: " + deviceId);
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
