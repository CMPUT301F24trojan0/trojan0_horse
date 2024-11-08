package com.example.trojan0project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

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
        createEventButton = findViewById(R.id.create_event_button); // Initialize the new button
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
            if (organizer != null && organizer.getEvents() != null && !organizer.getEvents().isEmpty()) {
                Intent intent = new Intent(OrganizerPageActivity.this, EventsListActivity.class);
                intent.putParcelableArrayListExtra("events_list", new ArrayList<>(organizer.getEvents()));
                startActivity(intent);
            } else {
                Toast.makeText(this, "No events created yet", Toast.LENGTH_SHORT).show();
            }
        });

        // Set up the Create Event button to navigate to CreateEventActivity
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
            facilityNameText.setText(newFacilityName);

            // Update Firestore with the new facility name
            firestore.collection("users").document(getIntent().getStringExtra("organizerId"))
                    .update("facilityName", newFacilityName)
                    .addOnSuccessListener(aVoid -> Toast.makeText(this, "Facility name updated", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(this, "Failed to update facility name", Toast.LENGTH_SHORT).show());
        }
    }
}
