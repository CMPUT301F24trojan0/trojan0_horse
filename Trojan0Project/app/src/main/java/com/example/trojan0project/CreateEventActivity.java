package com.example.trojan0project;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;


public class CreateEventActivity{


    private EditText eventNameEditText;
    private Switch geolocationSwitch;
    private Button addPosterButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event); // Ensure this matches your XML file name

        // Initialize views
        eventNameEditText = findViewById(R.id.event_name);
        geolocationSwitch = findViewById(R.id.switch_geolocation);
        addPosterButton = findViewById(R.id.add_poster_button);

        // Set click listener for the "Add Poster" button
        addPosterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the poster addition here (e.g., open an image picker)
            }
        });
    }

}
