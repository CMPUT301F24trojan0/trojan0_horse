package com.example.trojan0project;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class DisplayEventDetails extends AppCompatActivity {
    private FirebaseFirestore db;
    private String deviceId;
    private String eventId;
    private TextView eventTitle;
    private TextView eventLocation;
    private TextView eventTime;
    private TextView eventMoreInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.display_event_details_admin);
        db = FirebaseFirestore.getInstance();
        deviceId = getIntent().getStringExtra("DEVICE_ID");



        eventTitle = findViewById(R.id.event_title);
        eventLocation = findViewById(R.id.location_label);
        eventTime = findViewById(R.id.time_label);
        eventMoreInfo = findViewById(R.id.more_info_label);


        loadEventDetails();



        //OpenAI, (2024, November 6 2024), "How do I transfer the text from one activity to another?", ChatGPT
        Intent intent = getIntent();
        String selectedEventTitle = intent.getStringExtra("event_title");
        Event selectedEvent = (Event) getIntent().getSerializableExtra("clicked_event");
        eventId = selectedEvent.getEventId();

        //TextView eventTextView = findViewById(R.id.event_title);
        //eventTextView.setText(selectedEventTitle);




    }

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


