package com.example.trojan0project.Controller.Admin;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.trojan0project.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Purpose:
 * DisplayEventDetails is an activity that retrieves and displays the details of a selected event from Firestore.
 * It shows the event's title, location, time, and description
 *
 * Design Rationale:
 * The activity uses Firestore to load event data
 * Geolocation data is converted to a readable address format using reverse geocoding
 *
 * Outstanding Issues:
 * No issues
 */

public class DisplayEventDetails extends AppCompatActivity {
    private FirebaseFirestore db;
    private String deviceId;
    private String eventId;
    private TextView eventTitle;
    private TextView eventLocation;
    private TextView eventTime;
    private TextView eventMoreInfo;
    /**
     * Initializes the activity and sets up Firestore and UI components.
     *
     * @param savedInstanceState The saved state of the activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.display_event_details_admin);
        db = FirebaseFirestore.getInstance();
        deviceId = getIntent().getStringExtra("DEVICE_ID");
        eventId = getIntent().getStringExtra("eventId");

        if (eventId == null ) {
            Toast.makeText(this, "Event ID not found", Toast.LENGTH_SHORT).show();
            finish(); // Close the activity if no valid eventId is provided
            return;
        }

        eventTitle = findViewById(R.id.event_title);
        eventLocation = findViewById(R.id.location_label);
        eventTime = findViewById(R.id.time_label);
        eventMoreInfo = findViewById(R.id.more_info_label);

        loadEventDetails();
    }

    /**
     * Converts latitude and longitude coordinates to a readable address.
     *
     * @param latitude  The latitude of the location.
     * @param longitude The longitude of the location.
     * @return The address as a String or an error message if unavailable.
     */
    //From https://www.geeksforgeeks.org/reverse-geocoding-in-android/ , 2024-11-07
    public String getAddressFromCoordinates(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                return address.getAddressLine(0);
            } else {
                return "Address not found";
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "Geocoder service not available";
        }
    }
    /**
     * Loads event details from Firestore and populates the UI components with the retrieved data.
     * This includes event title, location, time, and description.
     */
    private void loadEventDetails() {
        db.collection("events").document(eventId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Get event details and set them in the UI
                        String title = documentSnapshot.getString("eventName");
                        Double latitude = documentSnapshot.getDouble("latitude");
                        Double longitude = documentSnapshot.getDouble("longitude");
                        String time = documentSnapshot.getString("time");
                        String description = documentSnapshot.getString("description");

                        if (latitude != null && longitude != null){
                            String address = getAddressFromCoordinates(latitude, longitude);
                            eventLocation.setText(address);
                        }

                        eventTitle.setText(title != null ? title : "No Title");
                        eventTime.setText(time != null ? time : "No Time");
                        eventMoreInfo.setText(description != null ? description : "No Description");
                    } else {
                        Toast.makeText(this, "Event not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load event details: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

}