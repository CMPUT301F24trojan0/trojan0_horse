/**
 * Activity that displays a list of events in a RecyclerView for the event organizer.
 * <p>This activity retrieves a list of event IDs from the previous activity and uses a {@link RecyclerView}
 * with an {@link EventsAdapterOrganizer} to display each event. The events are displayed in a vertical list format
 * using a {@link LinearLayoutManager}.</p>
 */

package com.example.trojan0project;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

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