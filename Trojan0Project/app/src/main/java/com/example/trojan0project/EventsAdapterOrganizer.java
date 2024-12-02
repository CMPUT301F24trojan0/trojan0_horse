/**
 * RecyclerView Adapter for displaying a list of event IDs in the organizer's event list.
 * <p>This adapter takes a list of event IDs and displays them in a RecyclerView. When an item is clicked,
 * it opens the {@link EventDetailsActivityOrganizer} activity to show details of the selected event.</p>
 *
 * <p>The adapter uses a ViewHolder pattern to efficiently manage the event list items and their corresponding views.</p>
 */

package com.example.trojan0project;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class EventsAdapterOrganizer extends RecyclerView.Adapter<EventsAdapterOrganizer.EventViewHolder> {

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

        // Set event name (or ID if the name isn't available)
        holder.eventNameTextView.setText(eventId);

        // Add click listener to open EventDetailsActivityOrganizer
        holder.itemView.setOnClickListener(v -> {
            Context context = v.getContext(); // Get context from itemView
            Intent intent = new Intent(context, EventDetailsActivityOrganizer.class);
            intent.putExtra("eventId", eventId); // Pass the event ID
            context.startActivity(intent); // Start the activity
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
