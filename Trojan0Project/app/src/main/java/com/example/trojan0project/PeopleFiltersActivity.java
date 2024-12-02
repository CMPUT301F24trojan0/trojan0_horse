package com.example.trojan0project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class PeopleFiltersActivity extends AppCompatActivity {

    private Button viewFinalEntrantsButton; // Button to navigate to ViewFinalEntrantsEventActivity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people_filters); // Create this layout file

        // Initialize UI elements
        viewFinalEntrantsButton = findViewById(R.id.view_final_entrants_button);

        // Get the event ID passed from the previous activity
        String eventId = getIntent().getStringExtra("eventId");

        // Set up the button click listener to navigate to ViewFinalEntrantsEventActivity
        viewFinalEntrantsButton.setOnClickListener(v -> {
            Intent intent = new Intent(PeopleFiltersActivity.this, ViewFinalEntrantsEventActivity.class);
            intent.putExtra("eventId", eventId); // Pass the eventId
            startActivity(intent);
        });
    }
}
