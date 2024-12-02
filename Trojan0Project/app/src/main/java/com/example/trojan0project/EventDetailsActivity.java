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
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.util.Date;

public class EventDetailsActivity extends AppCompatActivity {

    private static final String TAG = "EventDetailsActivity";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;

    private ImageView posterImageView;
    private TextView eventNameTextView, descriptionTextView, timeTextView, deadlineTextView, maxEntrantsTextView;
    private Button cancelButton, signUpButton;

    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        Log.d(TAG, "onCreate: Activity started");

        // Initialize views
        posterImageView = findViewById(R.id.posterImageView);
        eventNameTextView = findViewById(R.id.eventNameTextView);
        descriptionTextView = findViewById(R.id.descriptionTextView);
        timeTextView = findViewById(R.id.timeTextView);
        deadlineTextView = findViewById(R.id.deadlineTextView);
        maxEntrantsTextView = findViewById(R.id.maxEntrantsTextView);
        cancelButton = findViewById(R.id.cancelButton);
        signUpButton = findViewById(R.id.signUpButton);

        // Initialize location client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Retrieve and display event details
        Intent intent = getIntent();
        if (intent != null) {
            String eventName = intent.getStringExtra("eventName");
            String description = intent.getStringExtra("description");
            String posterUrl = intent.getStringExtra("posterPath");
            String time = intent.getStringExtra("time");
            String eventId = intent.getStringExtra("eventId");
            double latitude = intent.getDoubleExtra("latitude", 0.0);
            double longitude = intent.getDoubleExtra("longitude", 0.0);
            long deadlineTimestamp = intent.getLongExtra("deadline", -1);
            int maxNumberOfEntrants = intent.getIntExtra("maxNumberOfEntrants", 0);
            Date deadline = deadlineTimestamp != -1 ? new Date(deadlineTimestamp) : null;

            // Log received details
            Log.d(TAG, "Received event details: " + eventName + ", " + description);

            // Populate views
            eventNameTextView.setText(eventName != null ? eventName : "N/A");
            descriptionTextView.setText(description != null ? description : "N/A");
            timeTextView.setText(time != null ? String.format("Time: %s", time) : "N/A");
            deadlineTextView.setText(deadline != null ? String.format("Deadline: %s", deadline.toString()) : "N/A");
            maxEntrantsTextView.setText(maxNumberOfEntrants > 0 ? String.format("Max Entrants: %d", maxNumberOfEntrants) : "N/A");

            if (posterUrl != null) {
                Glide.with(this).load(posterUrl).into(posterImageView);
                Log.d(TAG, "Poster image loaded");
            }

            // Set up Cancel button
            cancelButton.setOnClickListener(v -> {
                Log.d(TAG, "Cancel button clicked");
                finish();
            });

            // Set up Sign Up button
            signUpButton.setOnClickListener(v -> handleSignUp(
                    eventName, description, posterUrl, time, eventId, latitude, longitude, deadlineTimestamp, maxNumberOfEntrants
            ));
        } else {
            Log.e(TAG, "Intent is null");
        }
    }

    private void handleSignUp(String eventName, String description, String posterUrl, String time,
                              String eventId, double latitude, double longitude, long deadlineTimestamp, int maxEntrants) {

        if (latitude != 0.0 && longitude != 0.0) {
            Log.d(TAG, "Valid location. Checking permissions...");

            // Check location permissions
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        LOCATION_PERMISSION_REQUEST_CODE);
            } else {
                // Get current location
                fusedLocationClient.getLastLocation()
                        .addOnSuccessListener(location -> {
                            if (location != null) {
                                double currentLatitude = location.getLatitude();
                                double currentLongitude = location.getLongitude();
                                Log.d(TAG, "Current location: " + currentLatitude + ", " + currentLongitude);

                                // Navigate to JoinWaitlistActivity
                                Intent joinWaitlistIntent = new Intent(EventDetailsActivity.this, JoinWaitlist.class);
                                joinWaitlistIntent.putExtra("eventName", eventName);
                                joinWaitlistIntent.putExtra("description", description);
                                joinWaitlistIntent.putExtra("posterPath", posterUrl);
                                joinWaitlistIntent.putExtra("time", time);
                                joinWaitlistIntent.putExtra("eventId", eventId);
                                joinWaitlistIntent.putExtra("latitude", latitude);
                                joinWaitlistIntent.putExtra("longitude", longitude);
                                joinWaitlistIntent.putExtra("deadline", deadlineTimestamp); // Pass as long
                                joinWaitlistIntent.putExtra("maxNumberOfEntrants", maxEntrants);
                                joinWaitlistIntent.putExtra("currentLatitude", currentLatitude);
                                joinWaitlistIntent.putExtra("currentLongitude", currentLongitude);

                                Log.d(TAG, "Navigating to JoinWaitlistActivity with eventId: " + eventId);
                                startActivity(joinWaitlistIntent);
                            } else {
                                Log.e(TAG, "Failed to retrieve current location");
                            }
                        });
            }
        } else {
            Log.e(TAG, "Latitude or Longitude is invalid");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Location permission granted");
            } else {
                Log.e(TAG, "Location permission denied");
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "EventDetailsActivity destroyed");
    }
}