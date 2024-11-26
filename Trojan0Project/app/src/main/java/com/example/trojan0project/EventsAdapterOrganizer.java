package com.example.trojan0project;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class EventsAdapterOrganizer extends RecyclerView.Adapter<EventsAdapterOrganizer.EventViewHolder> {

    private List<String> eventsList;

    public EventsAdapterOrganizer(List<String> eventsList) {
        this.eventsList = eventsList;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        String eventId = eventsList.get(position);
        holder.eventIdText.setText(eventId);
    }

    @Override
    public int getItemCount() {
        return eventsList.size();
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView eventIdText;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            eventIdText = itemView.findViewById(R.id.event_id_text);
        }
    }
}
