package com.example.trojan0project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Purpose:
 * The `PeopleFiltersActivity` class provides an interface for filtering and managing people associated with an event.
 * It includes navigation to view the final list of entrants for a specific event.
 *
 * Design Rationale:
 * - Serves as an intermediary activity for managing event-related people filters and actions.
 * - Retrieves the event ID passed from the previous activity to ensure the context is maintained across navigation.
 * - Provides a seamless user experience with intuitive navigation to the `ViewFinalEntrantsEventActivity`.
 *
 * Outstanding Issues:
 * - No known issues at this time.
 */

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
