package com.example.trojan0project.Controller.Organizer;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.trojan0project.Controller.Entrant.Notification;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.trojan0project.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Purpose:
 * The `EventDetailsActivityOrganizer` class provides detailed information about an event
 * for organizers. It allows organizers to view event details, change the event's poster image,
 * and view entrants associated with the event.
 *
 * Design Rationale:
 * - Integrates with Firebase Firestore to fetch and display event details.
 * - Utilizes Firebase Storage for managing poster image uploads and updates.
 * - Provides a user-friendly interface with buttons for changing posters and viewing entrants.
 * - Implements efficient data retrieval and update mechanisms for seamless user interaction.
 *
 * Outstanding Issues:
 * - No known issues at this time.
 */

public class EventDetailsActivityOrganizer extends AppCompatActivity {

    private TextView eventNameTextView, eventDescriptionTextView, eventTimeTextView;
    private ImageView eventPosterImageView;
    private Button changePosterButton, viewPeopleButton, geolocationButton;
    private FirebaseFirestore firestore;
    private FirebaseStorage firebaseStorage;
    private String eventId;
    private String passId;
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

    /**
     * Initializes the activity, setting up the UI elements, fetching event details from Firestore,
     * and setting up listeners for user interactions.
     * <p>It performs the following:</p>
     * <ul>
     *     <li>Initializes UI components such as TextViews, ImageView, and Buttons.</li>
     *     <li>Retrieves the event ID and pass ID from the Intent and conditionally displays buttons based on passId.</li>
     *     <li>Fetches event details (name, description, time, poster) from Firestore and updates the UI.</li>
     *     <li>Sets up listeners for the Change Poster button and View People button.</li>
     * </ul>
     *
     * @param savedInstanceState The saved state of the activity, if any.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_activity_event_detail);

        Toolbar toolbar = findViewById(R.id.leave_view_event_details_toolbar);
        setSupportActionBar(toolbar);

        // Set the title of the action bar to be empty
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);  // Enable the "up" button
        }

        // Initialize UI elements
        eventNameTextView = findViewById(R.id.event_name_text_view);
        eventDescriptionTextView = findViewById(R.id.event_description_text_view);
        eventTimeTextView = findViewById(R.id.event_time_text_view);
        eventPosterImageView = findViewById(R.id.event_poster_image_view);
        changePosterButton = findViewById(R.id.change_poster_button);
        viewPeopleButton = findViewById(R.id.view_people_button);
        geolocationButton = findViewById(R.id.view_geolocation_button);

        // Initialize Firebase services
        firestore = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();

        // Get the event ID and pass ID passed through the intent
        eventId = getIntent().getStringExtra("eventId");
        Log.d(TAG, "Received event ID: " + eventId);
        passId = getIntent().getStringExtra("passId");
        Log.d(TAG, "Received event ID: " + passId);

        // Conditionally set button visibility based on passId
        if (passId == null) {
            changePosterButton.setVisibility(Button.VISIBLE);
            viewPeopleButton.setVisibility(Button.VISIBLE);
            geolocationButton.setVisibility(View.VISIBLE);
        } else {
            changePosterButton.setVisibility(Button.GONE);
            viewPeopleButton.setVisibility(Button.GONE);
            geolocationButton.setVisibility(View.GONE);
        }

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
                            Double longitude = documentSnapshot.getDouble("longitude");
                            Double latitude = documentSnapshot.getDouble("latitude");

                            eventNameTextView.setText(eventName);
                            eventDescriptionTextView.setText(description);
                            eventTimeTextView.setText(time);

                            if (longitude != null && latitude != null && longitude != 0.0 && latitude != 0.0) {
                                geolocationButton.setVisibility(View.VISIBLE);
                                geolocationButton.setOnClickListener(v -> {
                                    Intent intent = new Intent(EventDetailsActivityOrganizer.this, MapEntrants.class);
                                    intent.putExtra("eventID", eventId);
                                    startActivity(intent);
                                });
                            }

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
            Intent intent = new Intent(EventDetailsActivityOrganizer.this, ViewFinalEntrantsEventActivity.class);
            intent.putExtra("eventId", eventId);
            startActivity(intent);
        });

    }

    /**
     * Opens the image picker to allow the organizer to select a new poster image from the device's gallery.
     *
     * <p>This method uses the {@link ActivityResultLauncher} to start an activity for result, which will pick an image
     * and pass the URI to the {@link #uploadPosterToFirebase(Uri)} method.</p>
     */
    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        pickImageLauncher.launch(intent);
    }

    /**
     * Uploads the selected poster image to Firebase Storage and updates the event's poster path in Firestore.
     *
     * <p>This method uploads the image to Firebase Storage, retrieves its download URL, and updates the Firestore
     * document with the new poster URL.</p>
     *
     * @param imageUri The URI of the selected image to be uploaded.
     */
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

    /**
     * Updates the event's poster path in Firestore with the new URL from Firebase Storage.
     *
     * <p>This method updates the Firestore document for the event with the new poster path and then loads the new
     * poster into the ImageView.</p>
     *
     * @param newPosterPath The URL of the new poster image.
     */
    private void updatePosterPathInFirestore(String newPosterPath) {
        firestore.collection("events").document(eventId)
                .update("posterPath", newPosterPath)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Poster updated successfully", Toast.LENGTH_SHORT).show();
                    // Load the new poster into the ImageView
                    Glide.with(this)
                            .load(newPosterPath)
                            .into(eventPosterImageView);

                    // Fetch the event details to notify users
                    firestore.collection("events").document(eventId)
                            .get()
                            .addOnSuccessListener(documentSnapshot -> {
                                if (documentSnapshot.exists()) {
                                    String eventName = documentSnapshot.getString("eventName"); // Assuming there's a "name" field for the event
                                    Map<String, Object> usersMap = (Map<String, Object>) documentSnapshot.get("users");

                                    if (usersMap != null) {
                                        List<String> deviceIds = new ArrayList<>(usersMap.keySet());
                                        Notification notifications = new Notification();
                                        for (String deviceId : deviceIds) {
                                            notifications.addNotificationToDevice(
                                                    deviceId,
                                                    eventId,
                                                    "Poster Updated",
                                                    "The poster for event \"" + eventName + "\" has been updated."
                                            );
                                        }
                                    } else {
                                        Log.w(TAG, "Users map is empty or null.");
                                    }
                                } else {
                                    Log.e(TAG, "Event not found in Firestore.");
                                }
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(this, "Failed to fetch event details", Toast.LENGTH_SHORT).show();
                                Log.e(TAG, "Error fetching event details: " + e.getMessage());
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to update poster path in Firestore", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error updating Firestore: " + e.getMessage());
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
            finish(); // Finish the current activity and return to the previous one
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
