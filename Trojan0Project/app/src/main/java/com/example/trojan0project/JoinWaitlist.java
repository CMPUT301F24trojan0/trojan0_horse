/**
 * Purpose:
 * This retrieves events and user profile information from firestore and displays it.
 * Users view event details and join the events waitlist by pressing confirm.
 *
 * Design Rationale:
 * Uses Firebase Firestore to get event and user data. Uses JoinWaitlistFragment dialog to confirm
 * if the user wants to join the waitlist.
 *
 * Outstanding issues:
 * If user wants to sign someone other than them, the code does not do that.
 * A QR code scanner has not been created yet so the eventID has been hard coded for the halfway checkpoint
 */

package com.example.trojan0project;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.trojan0project.JoinWaitlistFragment;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class JoinWaitlist extends AppCompatActivity implements JoinWaitlistFragment.JoinWaitlistListener {

    private static final String TAG = "JoinWaitlist";
    private FirebaseFirestore db;
    private String deviceId;
    private String eventId;
    private String eventName;
    private Double latitude;
    private Double longitude;
    private Double userLatitude;
    private Double userLongitude;
    private String time;
    private String description;

    private TextView eventTitle;
    private TextView eventLocation;
    private TextView eventTime;
    private TextView eventMoreInfo;
    private Button joinWaitlistButton;


    /**
     * Initializes the activity and loads the event details.
     *
     * @param savedInstanceState The saved state of the activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_join_waitlist);

        db = FirebaseFirestore.getInstance();

        // Get data from Intent
        deviceId = getIntent().getStringExtra("DEVICE_ID");
        eventId = getIntent().getStringExtra("eventId");
        eventName = getIntent().getStringExtra("eventName");
        latitude = getIntent().getDoubleExtra("latitude", 0.0);
        longitude = getIntent().getDoubleExtra("longitude", 0.0);
        userLatitude = getIntent().getDoubleExtra("currentLatitude", 0.0);
        userLongitude = getIntent().getDoubleExtra("currentLongitude", 0.0);
        time = getIntent().getStringExtra("time");
        description = getIntent().getStringExtra("description");

        Log.d(TAG, "onCreate: Event Details: eventId=" + eventId + ", eventName=" + eventName + ", latitude=" + latitude + ", longitude=" + longitude + ", time=" + time + ", description=" + description);

        // Reference views
        eventTitle = findViewById(R.id.event_title);
        eventLocation = findViewById(R.id.location_label);
        eventTime = findViewById(R.id.time_label);
        eventMoreInfo = findViewById(R.id.more_info_label);
        joinWaitlistButton = findViewById(R.id.join_waitlist_button);

        loadEventDetails();

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
     * Loads the event details from Firestore and displays them in the UI.
     */
    private void loadEventDetails() {
        db.collection("events").document(eventId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Get event details and set them in the UI
                        String title = documentSnapshot.getString("name");
                        Double latitude = documentSnapshot.getDouble("latitude");
                        Double longitude = documentSnapshot.getDouble("longitude");
                        String time = documentSnapshot.getString("time");
                        String description = documentSnapshot.getString("description");

                        if (latitude != null && longitude != null) {
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
        db.collection("users").document(deviceId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String userType = documentSnapshot.getString("user_type");
                        if ("entrant".equals(userType)) {
                            Map<String, Object> eventsMap = new HashMap<>();
                            eventsMap.put(eventId, 0);
                            db.collection("users").document(deviceId)
                                    .set(Collections.singletonMap("events", eventsMap), SetOptions.merge())
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(this, "You have been waitlisted for the event.", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(this, "Failed to add to waitlist: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });

                            db.collection("events").document(eventId)
                                    .update("waitlisted", FieldValue.arrayUnion(deviceId))
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(this, "Event waitlist updated.", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(this, "Failed to update event waitlist: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        } else {
                            Log.d("JoinWaitlist", "User is not an entrant; skipping waitlist addition.");
                        }
                    } else {
                        Log.d("JoinWaitlist", "User document does not exist.");
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to retrieve user information: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
