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

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event, parent, false);
        return new EventViewHolder(view);
    }

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
