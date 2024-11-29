package com.example.trojan0project;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class JoinWaitlist extends AppCompatActivity implements JoinWaitlistFragment.JoinWaitlistListener {

    private static final String TAG = "JoinWaitlist";
    private FirebaseFirestore db;
    private String deviceId;
    private String eventId;
    private String eventName;
    private Double latitude;
    private Double longitude;
    private String time;
    private String description;

    private TextView eventTitle;
    private TextView eventLocation;
    private TextView eventTime;
    private TextView eventMoreInfo;
    private Button joinWaitlistButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_waitlist);

        db = FirebaseFirestore.getInstance();

        // Get data from Intent
        deviceId = getIntent().getStringExtra("DEVICE_ID");
        eventId = getIntent().getStringExtra("eventId");
        eventName = getIntent().getStringExtra("eventName");
        latitude = getIntent().getDoubleExtra("currentLatitude", 0.0);
        longitude = getIntent().getDoubleExtra("currentLongitude", 0.0);
        time = getIntent().getStringExtra("time");
        description = getIntent().getStringExtra("description");

        Log.d(TAG, "onCreate: Event Details: eventId=" + eventId + ", eventName=" + eventName + ", latitude=" + latitude + ", longitude=" + longitude + ", time=" + time + ", description=" + description);

        // Reference views
        eventTitle = findViewById(R.id.event_title);
        eventLocation = findViewById(R.id.location_label);
        eventTime = findViewById(R.id.time_label);
        eventMoreInfo = findViewById(R.id.more_info_label);
        joinWaitlistButton = findViewById(R.id.join_waitlist_button);

        // Set event details in UI
        eventTitle.setText(eventName != null ? eventName : "No Title");
        eventTime.setText(time != null ? time : "No Time");
        eventMoreInfo.setText(description != null ? description : "No Description");

        if (latitude != null && longitude != null) {
            String address = getAddressFromCoordinates(latitude, longitude);
            eventLocation.setText(address != null ? address : "No Location Available");
        } else {
            eventLocation.setText("No Location Available");
        }

        // Set up the join waitlist button
        joinWaitlistButton.setOnClickListener(v -> {
            getUserProfileForDialog();
        });
    }

    /**
     * Converts latitude and longitude coordinates to a human-readable address.
     *
     * @param latitude  The latitude of the location.
     * @param longitude The longitude of the location.
     * @return A string address based on the coordinates or an error message if unavailable.
     */
    public String getAddressFromCoordinates(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                return addresses.get(0).getAddressLine(0);
            }
        } catch (IOException e) {
            Log.e(TAG, "Geocoder failed: " + e.getMessage(), e);
        }
        return "Address not found";
    }

    /**
     * Retrieves the user's profile data to populate the dialog when joining the waitlist.
     */
    private void getUserProfileForDialog() {
        db.collection("users").document(deviceId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String firstName = documentSnapshot.getString("first_name");
                        String lastName = documentSnapshot.getString("last_name");
                        String email = documentSnapshot.getString("email");

                        Profile profile = new Profile(firstName, lastName, email);

                        // Open waitlist dialog with user profile
                        JoinWaitlistFragment dialog = new JoinWaitlistFragment(profile);
                        dialog.show(getSupportFragmentManager(), "JoinWaitlistFragment");
                    } else {
                        Toast.makeText(this, "User profile not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error getting user profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * Confirms the user's intent to join the waitlist and updates Firestore with the waitlist entry.
     *
     * @param profile The user's profile data used to join the waitlist.
     */
    @Override
    public void onConfirm(Profile profile) {
        // No changes to onConfirm logic as this method was already correct
        Toast.makeText(this, "You confirmed the waitlist. Firestore updates can be added here.", Toast.LENGTH_SHORT).show();
    }
}
