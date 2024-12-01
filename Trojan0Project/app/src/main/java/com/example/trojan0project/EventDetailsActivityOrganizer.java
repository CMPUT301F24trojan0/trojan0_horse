package com.example.trojan0project;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.UUID;

public class EventDetailsActivityOrganizer extends AppCompatActivity {

    private TextView eventNameTextView, eventDescriptionTextView, eventTimeTextView;
    private ImageView eventPosterImageView;
    private Button changePosterButton, viewPeopleButton; // Added viewPeopleButton
    private FirebaseFirestore firestore;
    private FirebaseStorage firebaseStorage;
    private String eventId;
    private static final String TAG = "EventDetailsOrganizer";

    private final ActivityResultLauncher<Intent> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri selectedImageUri = result.getData().getData();
                    if (selectedImageUri != null) {
                        uploadPosterToFirebase(selectedImageUri);
                    }
                } else {
                    Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_activity_event_detail);

        // Initialize UI elements
        eventNameTextView = findViewById(R.id.event_name_text_view);
        eventDescriptionTextView = findViewById(R.id.event_description_text_view);
        eventTimeTextView = findViewById(R.id.event_time_text_view);
        eventPosterImageView = findViewById(R.id.event_poster_image_view);
        changePosterButton = findViewById(R.id.change_poster_button);
        viewPeopleButton = findViewById(R.id.view_people_button); // Initialize viewPeopleButton

        // Initialize Firebase services
        firestore = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();

        // Get the event ID passed through the intent
        eventId = getIntent().getStringExtra("eventId");
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

                            // Load poster into ImageView
                            if (posterPath != null && !posterPath.isEmpty()) {
                                Glide.with(this)
                                        .load(posterPath)
                                        .into(eventPosterImageView);
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

        // Set up Change Poster button click listener
        changePosterButton.setOnClickListener(v -> openImagePicker());

        // Set up View People button click listener
        viewPeopleButton.setOnClickListener(v -> {
            Intent intent = new Intent(EventDetailsActivityOrganizer.this, PeopleFiltersActivity.class);
            intent.putExtra("eventId", eventId); // Pass the eventId to the new activity
            startActivity(intent);
        });

    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        pickImageLauncher.launch(intent);
    }

    private void uploadPosterToFirebase(Uri imageUri) {
        String posterId = UUID.randomUUID().toString();
        StorageReference storageReference = firebaseStorage.getReference().child("posters/" + posterId);

        storageReference.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> storageReference.getDownloadUrl()
                        .addOnSuccessListener(uri -> {
                            // Update Firestore with the new poster path
                            updatePosterPathInFirestore(uri.toString());
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, "Failed to get download URL", Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "Error getting download URL: " + e.getMessage());
                        }))
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error uploading image: " + e.getMessage());
                });
    }

    private void updatePosterPathInFirestore(String newPosterPath) {
        firestore.collection("events").document(eventId)
                .update("posterPath", newPosterPath)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Poster updated successfully", Toast.LENGTH_SHORT).show();
                    // Load the new poster into the ImageView
                    Glide.with(this)
                            .load(newPosterPath)
                            .into(eventPosterImageView);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to update poster path in Firestore", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error updating Firestore: " + e.getMessage());
                });
    }
}
