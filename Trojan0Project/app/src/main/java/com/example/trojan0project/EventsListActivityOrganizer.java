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