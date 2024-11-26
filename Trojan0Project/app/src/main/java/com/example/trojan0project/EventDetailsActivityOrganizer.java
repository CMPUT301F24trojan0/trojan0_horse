package com.example.trojan0project;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;

public class EventDetailsActivityOrganizer extends AppCompatActivity {

    private TextView eventNameTextView, eventDescriptionTextView, eventTimeTextView;
    private ImageView eventPosterImageView;
    private FirebaseFirestore firestore;
    private static final String TAG = "EventDetailsOrganizer";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_activity_event_detail);

        // Initialize UI elements
        eventNameTextView = findViewById(R.id.event_name_text_view);
        eventDescriptionTextView = findViewById(R.id.event_description_text_view);
        eventTimeTextView = findViewById(R.id.event_time_text_view);
        eventPosterImageView = findViewById(R.id.event_poster_image_view); // Add this for the poster

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance();

        // Get the event ID passed through the intent
        String eventId = getIntent().getStringExtra("eventId");
        Log.d(TAG, "Received event ID: " + eventId);

        if (eventId != null) {
            // Fetch event details from Firestore
            firestore.collection("events").document(eventId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            // Populate UI with event details
                            String eventName = documentSnapshot.getString("eventName");
                            String description = documentSnapshot.getString("description");
                            String time = documentSnapshot.getString("time");
                            String posterPath = documentSnapshot.getString("posterPath");

                            eventNameTextView.setText(eventName);
                            eventDescriptionTextView.setText(description);
                            eventTimeTextView.setText(time);

                            // Load poster image using Glide
                            if (posterPath != null) {
                                Glide.with(this)
                                        .load(posterPath)
                                        .placeholder(R.drawable.placeholder_image) // Optional placeholder

                                        .into(eventPosterImageView);
                            } else {
                                Log.d(TAG, "No posterPath provided for event: " + eventId);
                            }

                        } else {
                            Toast.makeText(this, "Event not found!", Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "No event data found for ID: " + eventId);
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed to fetch event details", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Error fetching event details: " + e.getMessage());
                    });
        } else {
            Toast.makeText(this, "Invalid event ID!", Toast.LENGTH_SHORT).show();
        }
    }
}
