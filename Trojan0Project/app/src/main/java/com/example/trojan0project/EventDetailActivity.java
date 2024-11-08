// EventDetailActivity.java
package com.example.trojan0project;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;

public class EventDetailActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private TextView eventNameTextView;
    private ImageView qrCodeImageView;

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
