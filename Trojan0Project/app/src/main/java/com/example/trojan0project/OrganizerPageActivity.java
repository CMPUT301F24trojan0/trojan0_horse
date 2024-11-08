package com.example.trojan0project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class OrganizerPageActivity extends AppCompatActivity implements EditFacilityFragment.OnFacilityNameUpdatedListener {

    private Button editFacilityButton, viewEventsButton;
    private TextView facilityNameText;
    private FirebaseFirestore firestore;
    private Organizer organizer;
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
        facilityNameText = findViewById(R.id.facility_name_text);

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance();

        // Get the organizer ID passed from OrganizerSignUpActivity
        String organizerId = getIntent().getStringExtra("organizerId");

        // Retrieve the Organizer data from Firestore
        firestore.collection("organizers").document(organizerId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    organizer = documentSnapshot.toObject(Organizer.class);
                    if (organizer != null) {
                        facilityNameText.setText(organizer.getFacilityName());
                    } else {
                        facilityNameText.setText("No facility name provided");
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(OrganizerPageActivity.this, "Failed to load organizer", Toast.LENGTH_SHORT).show());

        editFacilityButton.setOnClickListener(v -> {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new EditFacilityFragment())
                    .addToBackStack(null)
                    .commit();
        });

        viewEventsButton.setOnClickListener(v -> {
            // Navigate to EventsListActivity and pass the organizer's events
            if (organizer != null && !organizer.getEvents().isEmpty()) {
                Intent intent = new Intent(OrganizerPageActivity.this, EventsListActivity.class);
                intent.putParcelableArrayListExtra("events_list", new ArrayList<>(organizer.getEvents()));
                startActivity(intent);
            } else {
                Toast.makeText(this, "No events created yet", Toast.LENGTH_SHORT).show();
            }
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
            firestore.collection("organizers").document(getIntent().getStringExtra("organizerId"))
                    .update("facilityName", newFacilityName)
                    .addOnSuccessListener(aVoid -> Toast.makeText(this, "Facility name updated", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(this, "Failed to update facility name", Toast.LENGTH_SHORT).show());
        }
    }
}
