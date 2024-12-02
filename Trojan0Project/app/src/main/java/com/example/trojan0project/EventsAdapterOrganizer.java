package com.example.trojan0project;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class EventsAdapterOrganizer extends RecyclerView.Adapter<EventsAdapterOrganizer.EventViewHolder> {
    // Initialize Firestore
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final List<String> eventIds; // Assuming you are passing a list of event IDs

    public EventsAdapterOrganizer(List<String> eventIds) {
        this.eventIds = eventIds;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        String eventId = eventIds.get(position);
        db.collection("events").document(eventId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            // Access the event name from the document
                            String eventName = document.getString("eventName");
                            Log.d("EventsAdapterOrganizer", "Event Name: " + eventName);

                            // Set event name (or ID if the name isn't available)
                            holder.eventNameTextView.setText(eventName != null ? eventName : eventId);

                            // Add click listener to open EventDetailsActivityOrganizer
                            holder.itemView.setOnClickListener(v -> {
                                Context context = v.getContext(); // Get context from itemView
                                Intent intent = new Intent(context, EventDetailsActivityOrganizer.class);
                                intent.putExtra("eventId", eventId); // Pass the event ID
                                context.startActivity(intent); // Start the activity
                            });
                        }
                    } else {
                        Log.e("EventsAdapterOrganizer", "Failed to fetch event: " + eventId, task.getException());
                    }
                });
    }

    @Override
    public int getItemCount() {
        return eventIds.size();
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView eventNameTextView;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            eventNameTextView = itemView.findViewById(R.id.eventNameTextView);
        }
    }
}
