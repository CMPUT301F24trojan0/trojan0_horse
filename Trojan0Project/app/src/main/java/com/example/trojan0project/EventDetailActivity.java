package com.example.trojan0project;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Purpose:
 * displays the details of a specific event, including the event name and QR code.
 * retrieves the event information from Firestore using the event ID passed to the activity and loads the details
 * onto the screen.
 *
 * Design Rationale:
 * Uses Firestore to retrieve event data based on a unique event ID
 * Uses Glide to load and display the QR code image
 *
 * Outstanding Issues:
 * No issues
 */

public class EventDetailActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private TextView eventNameTextView;
    private ImageView qrCodeImageView;

    /**
     * Initializes the activity, setting up UI elements and Firebase services.
     * Also retrieves the event ID passed in the intent and loads the event details.
     *
     * @param savedInstanceState The saved state of the activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        // Initialize Firestore and UI elements
        db = FirebaseFirestore.getInstance();
        eventNameTextView = findViewById(R.id.eventNameTextView);
        qrCodeImageView = findViewById(R.id.qrCodeImageView);

        // Retrieve the event ID passed to this activity
        String eventId = getIntent().getStringExtra("EVENT_ID");
        if (eventId != null) {
            loadEventDetails(eventId);
        }
    }

    /**
     * Loads the details of a specific event from Firestore and displays them in the UI.
     *
     * @param eventId The unique ID of the event to be displayed.
     */
    private void loadEventDetails(String eventId) {
        db.collection("events").document(eventId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Event event = documentSnapshot.toObject(Event.class);
                        if (event != null) {
                            // Set event name
                            eventNameTextView.setText(event.getEventName());

                            // Load and display QR code image if URL is available
                            if (event.getQrCodeUrl() != null) {
                                Glide.with(this)
                                        .load(event.getQrCodeUrl())
                                        .into(qrCodeImageView);
                            }
                        }
                    } else {
                        Toast.makeText(this, "Event not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to load event", Toast.LENGTH_SHORT).show());
    }
}
