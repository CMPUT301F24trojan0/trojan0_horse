package com.example.trojan0project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Purpose:
 * The `OrganizerPageActivity` class serves as the main interface for organizers, allowing them to manage their facility,
 * view associated events, and create new events. It integrates with Firestore to fetch and update organizer-specific data.
 *
 * Design Rationale:
 * - Provides a centralized interface for organizing key functionalities like editing facility names, viewing events, and creating events.
 * - Fetches and updates data dynamically from Firestore to ensure real-time synchronization.
 * - Integrates with `EditFacilityFragment` for modular and reusable UI components to handle facility name edits.
 * - Ensures seamless navigation between different features with appropriate error handling and debugging logs.
 *
 * Outstanding Issues:
 * - No known issues at this time.
 */

public class OrganizerPageActivity extends AppCompatActivity implements EditFacilityFragment.OnFacilityNameUpdatedListener {

    private Button editFacilityButton, viewEventsButton, createEventButton;
    private TextView facilityNameText;
    private FirebaseFirestore firestore;
    private Organizer organizer;
    private static final String TAG = "OrganizerPageActivity";

    /**
     * Initializes the activity and sets up UI elements and Firestore.
     *
     * @param savedInstanceState The saved state of the activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer_page);

        editFacilityButton = findViewById(R.id.edit_facility_button);
        viewEventsButton = findViewById(R.id.view_events_button);
        createEventButton = findViewById(R.id.create_event_button);
        facilityNameText = findViewById(R.id.facility_name_text);

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance();

        // Get the organizer ID passed from the intent
        String organizerId = getIntent().getStringExtra("organizerId");
        Log.d(TAG, "Organizer ID: " + organizerId);

        if (organizerId != null) {
            // Retrieve the Organizer data from Firestore
            firestore.collection("users").document(organizerId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            organizer = documentSnapshot.toObject(Organizer.class);
                            if (organizer != null && organizer.getFacilityName() != null) {
                                String welcomeMessage = "Welcome " + organizer.getFacilityName() + "!";
                                facilityNameText.setText(welcomeMessage);
                                Log.d(TAG, "Facility name: " + organizer.getFacilityName());
                            } else {
                                facilityNameText.setText("No facility name provided");
                                Log.d(TAG, "Facility name not found in organizer data");
                            }
                        } else {
                            Toast.makeText(this, "Organizer data not found", Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "No document found for organizer ID");
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed to load organizer", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Error retrieving organizer data: " + e.getMessage());
                    });
        } else {
            Toast.makeText(this, "Invalid organizer ID", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Organizer ID is null");
        }

        editFacilityButton.setOnClickListener(v -> {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new EditFacilityFragment())
                    .addToBackStack(null)
                    .commit();
        });

        viewEventsButton.setOnClickListener(v -> {
            if (organizerId != null) {
                // Fetch events dynamically from Firestore
                firestore.collection("users").document(organizerId).get()
                        .addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot.exists()) {
                                // Log the entire document for debugging
                                Log.d(TAG, "Document Snapshot: " + documentSnapshot.getData());

                                // Retrieve events from organizer_details
                                Object organizerDetails = documentSnapshot.get("organizer_details");
                                if (organizerDetails instanceof Map) {
                                    Map<String, Object> detailsMap = (Map<String, Object>) organizerDetails;
                                    Object eventsObject = detailsMap.get("events");
                                    if (eventsObject instanceof List) {
                                        List<String> events = (List<String>) eventsObject;
                                        if (!events.isEmpty()) {
                                            // Launch EventsListActivityOrganizer with events
                                            Intent intent = new Intent(this, EventsListActivityOrganizer.class);
                                            intent.putStringArrayListExtra("events_list", new ArrayList<>(events));
                                            startActivity(intent);
                                        } else {
                                            Toast.makeText(this, "No events found", Toast.LENGTH_SHORT).show();
                                            Log.d(TAG, "Events array is empty.");
                                        }
                                    } else {
                                        Toast.makeText(this, "Invalid events data format", Toast.LENGTH_SHORT).show();
                                        Log.e(TAG, "Events field is not a list: " + eventsObject);
                                    }
                                } else {
                                    Toast.makeText(this, "No events to be found", Toast.LENGTH_SHORT).show();
                                    Log.e(TAG, "organizer_details is not a map: " + organizerDetails);
                                }
                            } else {
                                Toast.makeText(this, "No data found for organizer", Toast.LENGTH_SHORT).show();
                                Log.e(TAG, "No organizer document found in Firestore.");
                            }
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, "Failed to fetch events", Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "Error fetching events: " + e.getMessage());
                        });
            } else {
                Toast.makeText(this, "Organizer ID is invalid", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Organizer ID is null");
            }
        });

        createEventButton.setOnClickListener(v -> {
            Intent intent = new Intent(OrganizerPageActivity.this, CreateEventActivity.class);
            intent.putExtra("organizerId", organizerId);
            startActivity(intent);
        });
    }

    /**
     * Updates the facility name displayed in the UI and in Firestore when the name is changed.
     *
     * @param newFacilityName The new facility name provided by the user.
     */
    @Override
    public void onFacilityNameUpdated(String newFacilityName) {
        if (organizer != null) {
            organizer.setFacilityName(newFacilityName);
            String welcomeMessage = "Welcome " + newFacilityName + "!";
            facilityNameText.setText(welcomeMessage);

            // Update Firestore with the new facility name
            firestore.collection("users").document(getIntent().getStringExtra("organizerId"))
                    .update("facilityName", newFacilityName)
                    .addOnSuccessListener(aVoid -> Toast.makeText(this, "Facility name updated", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(this, "Failed to update facility name", Toast.LENGTH_SHORT).show());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
