

package com.example.trojan0project;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
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

        eventsRecyclerView = findViewById(R.id.events_recycler_view);
        eventsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        eventsList = getIntent().getStringArrayListExtra("events_list");

        eventsAdapterOrganizer = new EventsAdapterOrganizer(eventsList);
        eventsRecyclerView.setAdapter(eventsAdapterOrganizer);
    }
}