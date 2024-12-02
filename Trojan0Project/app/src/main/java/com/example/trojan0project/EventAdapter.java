/**
 * Purpose:
 * custom adapter for displaying a list of Event objects in a RecyclerView
 * allows each event item to display its name and includes a click listener for item selection.
 *
 * Design Rationale:
 * Extends RecyclerView.Adapter, uses `ViewHolder` to cache view references
 * OnEventClickListener interface to handle click events on individual items
 *
 * Outstanding Issues:
 * No issues
 */
package com.example.trojan0project;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private List<Event> eventList;
    private OnEventClickListener onEventClickListener;

    /**
     * Constructor for initializing the adapter with an event list.
     *
     * @param eventList The list of events to display in the RecyclerView.
     */
    public EventAdapter(List<Event> eventList) {
        this.eventList = eventList;
    }
    /**
     * Interface for handling click events on individual event items.
     */
    public interface OnEventClickListener {
        void onEventClick(Event event);
    }

    /**
     * Sets a click listener for event items.
     *
     * @param listener The listener to handle click events.
     */
    public void setOnEventClickListener(OnEventClickListener listener) {
        this.onEventClickListener = listener;
    }
    /**
     * Called when RecyclerView needs a new ViewHolder of the given type to represent an item.
     *
     * @param parent The ViewGroup into which the new View will be added.
     * @param viewType The view type of the new View.
     * @return A new EventViewHolder that holds a View for an event item.
     */
    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event, parent, false);
        return new EventViewHolder(view);
    }
    /**
     * Binds the data for an event to the specified ViewHolder.
     *
     * @param holder   The ViewHolder to be updated with the event data.
     * @param position The position of the event within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = eventList.get(position);

        // Set the event name immediately (as it is already in the local list)
        holder.eventNameTextView.setText(event.getEventName());

        // Fetch description from Firestore dynamically
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("events")
                .document(event.getEventId()) // Assuming eventId matches Firestore document ID
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String description = documentSnapshot.getString("description");
                        if (description != null) {
                            holder.eventDescriptionText.setText(description);
                            holder.eventDescriptionText.setVisibility(View.VISIBLE);
                        } else {
                            holder.eventDescriptionText.setVisibility(View.GONE); // Hide if null
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle Firestore error (optional logging or fallback behavior)
                    holder.eventDescriptionText.setText("Description unavailable");
                    holder.eventDescriptionText.setVisibility(View.VISIBLE);
                });

        // Set click listener on the item
        holder.itemView.setOnClickListener(v -> {
            if (onEventClickListener != null) {
                onEventClickListener.onEventClick(event);
            }
        });
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The number of events in the event list.
     */
    @Override
    public int getItemCount() {
        return eventList.size();
    }
    /**
     * ViewHolder class for event items.
     * Holds references to the views in each item layout for quick access.
     */
    static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView eventNameTextView;
        TextView eventDescriptionText;
        /**
         * Constructor for EventViewHolder.
         *
         * @param itemView The item view layout for an individual event.
         */
        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            eventNameTextView = itemView.findViewById(R.id.eventNameTextView);
            eventDescriptionText = itemView.findViewById(R.id.event_description_text);
        }
    }
}
