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

public class EventDetailsActivity extends AppCompatActivity {

    private static final String TAG = "EventDetailsActivity";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;

    private ImageView posterImageView;
    private TextView eventNameTextView, descriptionTextView, timeTextView;
    private Button cancelButton, signUpButton;

    private FusedLocationProviderClient fusedLocationClient;

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
        cancelButton = findViewById(R.id.cancelButton);
        signUpButton = findViewById(R.id.signUpButton);

        // Initialize location client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Retrieve event details passed via intent
        Intent intent = getIntent();
        String eventName = null, description = null, posterUrl = null, time = null, eventId = null;
        Double latitude = null, longitude = null;

        if (intent != null) {
            eventName = intent.getStringExtra("eventName");
            description = intent.getStringExtra("description");
            posterUrl = intent.getStringExtra("posterPath");
            time = intent.getStringExtra("time");
            eventId = intent.getStringExtra("eventId");
            latitude = intent.getDoubleExtra("latitude", 0.0);
            longitude = intent.getDoubleExtra("longitude", 0.0);

            Log.d(TAG, "onCreate: Received event details");
            Log.d(TAG, "onCreate: eventName = " + eventName);
            Log.d(TAG, "onCreate: description = " + description);
            Log.d(TAG, "onCreate: posterUrl = " + posterUrl);
            Log.d(TAG, "onCreate: time = " + time);
            Log.d(TAG, "onCreate: eventId = " + eventId);
            Log.d(TAG, "onCreate: latitude = " + latitude);
            Log.d(TAG, "onCreate: longitude = " + longitude);

            // Set values in views
            eventNameTextView.setText(eventName != null ? eventName : "N/A");
            descriptionTextView.setText(description != null ? description : "N/A");
            timeTextView.setText(time != null ? String.format("Time: %s", time) : "N/A");

            if (posterUrl != null) {
                Glide.with(this).load(posterUrl).into(posterImageView);
                Log.d(TAG, "onCreate: Loaded poster image");
            }
        } else {
            Log.e(TAG, "onCreate: Intent is null");
        }

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
        Double finalLatitude = latitude;
        Double finalLongitude = longitude;

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: EventDetailsActivity destroyed");
    }
}
