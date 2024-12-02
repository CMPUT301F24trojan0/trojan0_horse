/**
 * Activity representing the organizer's main page. The activity allows the organizer to
 * edit their facility name, view events, and create a new event.
 * It retrieves the organizer's details from Firestore and updates the UI accordingly.
 */

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

public class OrganizerPageActivity extends AppCompatActivity implements EditFacilityFragment.OnFacilityNameUpdatedListener {

    private Button editFacilityButton, viewEventsButton, createEventButton;
    private TextView facilityNameText;
    private FirebaseFirestore firestore;
    private Organizer organizer;
    private static final String TAG = "OrganizerPageActivity";

    /**
     * Called when the activity is first created. Sets up the UI elements and initializes
     * Firestore to fetch and update data related to the organizer.
     *
     * @param savedInstanceState The saved instance state of the activity, if any.
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
                                facilityNameText.setText(organizer.getFacilityName());
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
                                    Toast.makeText(this, "No organizer details found", Toast.LENGTH_SHORT).show();
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
     * Called when the facility name is updated in the EditFacilityFragment. Updates the displayed facility
     * name in both the UI and Firestore.
     *
     * @param newFacilityName The new facility name entered by the user.
     */
    @Override
    public void onFacilityNameUpdated(String newFacilityName) {
        if (organizer != null) {
            organizer.setFacilityName(newFacilityName);
            facilityNameText.setText(newFacilityName);

            // Update Firestore with the new facility name
            firestore.collection("users").document(getIntent().getStringExtra("organizerId"))
                    .update("facilityName", newFacilityName)
                    .addOnSuccessListener(aVoid -> Toast.makeText(this, "Facility name updated", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(this, "Failed to update facility name", Toast.LENGTH_SHORT).show());
        }
    }
}
