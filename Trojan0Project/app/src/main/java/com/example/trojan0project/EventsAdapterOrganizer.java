

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
/**
 * Purpose:
 * The `EventsAdapterOrganizer` class is a custom RecyclerView adapter designed for organizers
 * to display and manage a list of events. It fetches event details from Firestore using event IDs
 * and displays them in a RecyclerView. Each item can be clicked to navigate to the event's detailed view.
 *
 * Design Rationale:
 * - Integrates with Firestore to dynamically fetch and display event details.
 * - Uses a `RecyclerView` for efficient, scalable UI rendering.
 * - Provides a user-friendly interface for event management, allowing organizers to view detailed information
 *   about each event by tapping on the item.
 *
 * Outstanding Issues:
 * - No known issues at this time.
 */

public class EventsAdapterOrganizer extends RecyclerView.Adapter<EventsAdapterOrganizer.EventViewHolder> {
    // Initialize Firestore
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final List<String> eventIds; // Assuming you are passing a list of event IDs

    public EventsAdapterOrganizer(List<String> eventIds) {
        this.eventIds = eventIds;
    }

    /**
     * Creates a new {@link EventViewHolder} for displaying an event item in the RecyclerView.
     *
     * <p>This method inflates the layout for an event item and creates a new ViewHolder to manage the event view.</p>
     *
     * @param parent The ViewGroup into which the new view will be added.
     * @param viewType The view type of the new view (unused here).
     * @return A new {@link EventViewHolder} instance for the event item view.
     */
    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event, parent, false);
        return new EventViewHolder(view);
    }

    /**
     * Binds the event data to the {@link EventViewHolder} at the specified position in the RecyclerView.
     *
     * <p>This method sets the event name (or ID if the name is unavailable) to the TextView and sets up a click
     * listener on the item view to navigate to {@link EventDetailsActivityOrganizer} when clicked.</p>
     *
     * @param holder The {@link EventViewHolder} that holds the views for the event item.
     * @param position The position of the item in the data set.
     */
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

    /**
     * Returns the total number of items in the data set.
     *
     * @return The total number of events (size of the eventIds list).
     */
    @Override
    public int getItemCount() {
        return eventIds.size();
    }

    /**
     * ViewHolder for holding views associated with an event item in the RecyclerView.
     *
     * <p>This class holds references to the views in an event item (such as the event name) and is responsible
     * for efficiently binding data to those views.</p>
     */
    public static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView eventNameTextView;

        /**
         * Creates a new {@link EventViewHolder} and binds the views.
         *
         * @param itemView The view representing an event item.
         */
        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            eventNameTextView = itemView.findViewById(R.id.eventNameTextView);
        }
    }
}
