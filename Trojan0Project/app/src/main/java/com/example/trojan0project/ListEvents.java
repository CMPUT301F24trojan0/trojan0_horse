/**
 * Purpose:
 * Displays list of events from Firestore in a RecyclerView and when an event is clicked
 * to goes to a mapping page for that event
 *
 * Design Rationale:
 * Uses Firestore to get event data, displays using RecyclerView with an EventAdapter
 *
 * Outstanding Issues:
 * No issues
 */

package com.example.trojan0project;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.widget.AdapterView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ListEvents extends AppCompatActivity {
    private RecyclerView recyclerView;
    private EventAdapter eventListAdapter;
    private List<Event> listEvent;
    private FirebaseFirestore db;

    /**
     * Initializes Firebase, and configures the RecyclerView and gets events
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.events_list);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        listEvent = new ArrayList<>();
        eventListAdapter = new EventAdapter(listEvent);
        recyclerView.setAdapter(eventListAdapter);

        db = FirebaseFirestore.getInstance();

        fetchEvents();

        //eventListAdapter.setOnEventClickListener(event -> {
            //Intent intent = new Intent(ListEvents.this, MapEntrants.class);
            //intent.putExtra("eventID", event.getEventId());
            //startActivity(intent);
        //});
    }

    /**
     * Gets a list of events from Firestore, extracts details
     */
    private void fetchEvents(){

        db.collection("events")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots){
                        String eventName = document.getString("eventName");
                        String documentId = document.getId();
                        if (eventName != null){
                            Event event = new Event(eventName);
                            event.setEventId(documentId);
                            listEvent.add(event);
                        }
                        Log.d("Firestore", "Event: " + eventName + ", ID: " + documentId);

                    }
                    eventListAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->{
                    Log.e("Firestore", "Error getting events", e);
                });
    }
}