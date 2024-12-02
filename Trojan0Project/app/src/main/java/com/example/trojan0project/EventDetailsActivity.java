/**
 * Represents the detailed view of an event, providing information such as the event's
 * name, description, poster, time, deadline, and maximum number of entrants.
 *
 * <p>This activity also allows users to:</p>
 * <ul>
 *     <li>Cancel and return to the previous screen.</li>
 *     <li>Sign up for the event, which requires location permissions to retrieve the user's current geolocation.</li>
 * </ul>
 *
 * <p>Design Features:</p>
 * <ul>
 *     <li>Integrates with the Fused Location Provider API for geolocation services.</li>
 *     <li>Uses Glide for efficient image loading.</li>
 *     <li>Handles event data passed via an Intent.</li>
 * </ul>
 *
 * <p>Extends {@link AppCompatActivity} to support modern Android UI and lifecycle management.</p>
 */

package com.example.trojan0project;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.Date;

public class EventDetailsActivity extends AppCompatActivity {

    private static final String TAG = "EventDetailsActivity";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;

    private ImageView posterImageView;
    private TextView eventNameTextView, descriptionTextView, timeTextView, deadlineTextView, maxEntrantsTextView;
    private Button cancelButton, signUpButton;

    private FusedLocationProviderClient fusedLocationClient;

    /**
     * Initializes the activity, setting up the UI elements and retrieving event details passed via Intent.
     *
     * <p>Performs the following tasks:</p>
     * <ul>
     *     <li>References and initializes UI components such as text views, image views, and buttons.</li>
     *     <li>Retrieves event details, such as name, description, and geolocation, from the Intent.</li>
     *     <li>Displays the event information on the screen.</li>
     *     <li>Sets up listeners for the "Cancel" and "Sign Up" buttons.</li>
     * </ul>
     *
     * @param savedInstanceState The saved state of the activity, if any.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        Log.d(TAG, "onCreate: Activity started");

        // Reference views
        posterImageView = findViewById(R.id.posterImageView);
        eventNameTextView = findViewById(R.id.eventNameTextView);
        descriptionTextView = findViewById(R.id.descriptionTextView);
        timeTextView = findViewById(R.id.timeTextView);
        deadlineTextView = findViewById(R.id.deadlineTextView); // New field
        maxEntrantsTextView = findViewById(R.id.maxEntrantsTextView); // New field
        cancelButton = findViewById(R.id.cancelButton);
        signUpButton = findViewById(R.id.signUpButton);

        // Initialize location client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Retrieve event details passed via intent
        Intent intent = getIntent();
        String eventName = null, description = null, posterUrl = null, time = null, eventId = null;
        Date deadline = null; // New field
        int maxNumberOfEntrants = 0; // New field
        Double latitude = null, longitude = null;

        if (intent != null) {
            eventName = intent.getStringExtra("eventName");
            description = intent.getStringExtra("description");
            posterUrl = intent.getStringExtra("posterPath");
            time = intent.getStringExtra("time");
            eventId = intent.getStringExtra("eventId");
            latitude = intent.getDoubleExtra("latitude", 0.0);
            longitude = intent.getDoubleExtra("longitude", 0.0);
            deadline = (Date) intent.getSerializableExtra("deadline"); // New field
            maxNumberOfEntrants = intent.getIntExtra("maxNumberOfEntrants", 0); // New field

            Log.d(TAG, "onCreate: Received event details");
            Log.d(TAG, "onCreate: eventName = " + eventName);
            Log.d(TAG, "onCreate: description = " + description);
            Log.d(TAG, "onCreate: posterUrl = " + posterUrl);
            Log.d(TAG, "onCreate: time = " + time);
            Log.d(TAG, "onCreate: eventId = " + eventId);
            Log.d(TAG, "onCreate: latitude = " + latitude);
            Log.d(TAG, "onCreate: longitude = " + longitude);
            Log.d(TAG, "onCreate: deadline = " + deadline); // Log new field
            Log.d(TAG, "onCreate: maxNumberOfEntrants = " + maxNumberOfEntrants); // Log new field

            // Set values in views
            eventNameTextView.setText(eventName != null ? eventName : "N/A");
            descriptionTextView.setText(description != null ? description : "N/A");
            timeTextView.setText(time != null ? String.format("Time: %s", time) : "N/A");
            deadlineTextView.setText(deadline != null ? String.format("Deadline: %s", deadline.toString()) : "N/A"); // New field
            maxEntrantsTextView.setText(maxNumberOfEntrants > 0 ? String.format("Max Entrants: %d", maxNumberOfEntrants) : "N/A"); // New field

            if (posterUrl != null) {
                Glide.with(this).load(posterUrl).into(posterImageView);
                Log.d(TAG, "onCreate: Loaded poster image");
            }
        } else {
            Log.e(TAG, "onCreate: Intent is null");
        }

        /**
         * Handles the click event for the "Cancel" button.
         *
         * <p>Closes the current activity and navigates back to the previous screen.</p>
         */
        // Cancel button listener
        cancelButton.setOnClickListener(v -> {
            Log.d(TAG, "Cancel button clicked");
            finish(); // Ensure activity is properly finished
        });

        // Sign Up button listener
        String finalEventName = eventName;
        String finalDescription = description;
        String finalPosterUrl = posterUrl;
        String finalTime = time;
        String finalEventId = eventId;
        Date finalDeadline = deadline; // New field
        int finalMaxNumberOfEntrants = maxNumberOfEntrants; // New field
        Double finalLatitude = latitude;
        Double finalLongitude = longitude;

        /**
         * Handles the click event for the "Sign Up" button.
         *
         * <p>Attempts to retrieve the user's current geolocation and navigates to the {@link JoinWaitlist}
         * activity with event details and the user's location.</p>
         *
         * <p>If location permissions are not granted, it requests the required permissions.</p>
         */
        signUpButton.setOnClickListener(v -> {
            Log.d(TAG, "Sign Up button clicked");

            if (finalLatitude != null && finalLongitude != null) {
                Log.d(TAG, "Latitude and Longitude are valid. Getting current geolocation...");

                // Check for location permissions
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "Requesting location permissions...");
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                            LOCATION_PERMISSION_REQUEST_CODE);
                } else {
                    // Get current location
                    fusedLocationClient.getLastLocation()
                            .addOnSuccessListener(new OnSuccessListener<Location>() {
                                @Override
                                public void onSuccess(Location location) {
                                    if (location != null) {
                                        double currentLatitude = location.getLatitude();
                                        double currentLongitude = location.getLongitude();
                                        Log.d(TAG, "Current location: Latitude = " + currentLatitude + ", Longitude = " + currentLongitude);

                                        // Navigate to JoinWaitlistActivity
                                        Intent joinWaitlistIntent = new Intent(EventDetailsActivity.this, JoinWaitlist.class);
                                        joinWaitlistIntent.putExtra("eventName", finalEventName);
                                        joinWaitlistIntent.putExtra("description", finalDescription);
                                        joinWaitlistIntent.putExtra("posterPath", finalPosterUrl);
                                        joinWaitlistIntent.putExtra("time", finalTime);
                                        joinWaitlistIntent.putExtra("eventId", finalEventId);
                                        joinWaitlistIntent.putExtra("latitude", finalLatitude);
                                        joinWaitlistIntent.putExtra("longitude", finalLongitude);
                                        joinWaitlistIntent.putExtra("deadline", finalDeadline); // Pass new field
                                        joinWaitlistIntent.putExtra("maxNumberOfEntrants", finalMaxNumberOfEntrants); // Pass new field
                                        joinWaitlistIntent.putExtra("currentLatitude", currentLatitude);
                                        joinWaitlistIntent.putExtra("currentLongitude", currentLongitude);

                                        Log.d(TAG, "onSignUpButtonClick: Passing event details and user location to JoinWaitlistActivity");
                                        startActivity(joinWaitlistIntent);
                                    } else {
                                        Log.e(TAG, "Failed to retrieve current location.");
                                    }
                                }
                            });
                }
            } else {
                Log.e(TAG, "Latitude or Longitude is null.");
            }
        });
    }

    /**
     * Cleans up resources and logs the destruction of the activity.
     *
     * <p>Called when the activity is destroyed.</p>
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: EventDetailsActivity destroyed");
    }
}
