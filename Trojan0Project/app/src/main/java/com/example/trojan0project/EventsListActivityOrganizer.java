package com.example.trojan0project;

import static android.content.Intent.getIntent;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

/**
 * Purpose:
 * The `EventsListActivityOrganizer` class provides a user interface for organizers to view a list of events.
 * It utilizes a RecyclerView to display event data in a scrollable, vertical list format, allowing organizers
 * to interact with individual events.
 *
 * Design Rationale:
 * - Uses a `RecyclerView` for efficient display of event data, leveraging its adapter-based architecture.
 * - Integrates with `EventsAdapterOrganizer` to dynamically fetch and bind event details.
 * - Accepts a list of event IDs passed via an intent, ensuring seamless data flow from the previous activity.
 *
 * Outstanding Issues:
 * - No known issues at this time.
 */

public class EventsListActivityOrganizer extends AppCompatActivity {

    private RecyclerView eventsRecyclerView;
    private EventsAdapterOrganizer eventsAdapterOrganizer;
    private ArrayList<String> eventsList;

    /**
     * Initializes the activity and sets up the RecyclerView with a list of events.
     *
     * <p>This method retrieves the list of event IDs passed from the previous activity and sets up a RecyclerView with
     * an adapter to display the events. It also configures the RecyclerView's layout manager to display the items vertically.</p>
     *
     * @param savedInstanceState The saved state of the activity (unused in this case).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events_list);

        Toolbar toolbar = findViewById(R.id.leave_view_all_events_toolbar);
        setSupportActionBar(toolbar);

        // Set the title of the action bar to be empty
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);  // Enable the "up" button
        }

        eventsRecyclerView = findViewById(R.id.events_recycler_view);
        eventsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Get the list of events from the Intent
        eventsList = getIntent().getStringArrayListExtra("events_list");

        // Initialize the adapter with the event list
        eventsAdapterOrganizer = new EventsAdapterOrganizer(eventsList);
        eventsRecyclerView.setAdapter(eventsAdapterOrganizer);
    }


    /**
     * Handles the selection of menu items, specifically the "home" button (up navigation).
     * This method is called when an item in the options menu is selected.
     *
     * @param item The menu item that was selected.
     * @return True if the menu item is handled, false otherwise.
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // Finish the current activity and return to the previous one
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}