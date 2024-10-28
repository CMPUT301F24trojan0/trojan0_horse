package com.example.trojan0project;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class EventArrayAdapter extends ArrayAdapter<Event> {
    public EventArrayAdapter(Context context, ArrayList<Event> events) {
        super(context, 0, events);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup
            parent) {
        View view;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.content,
                    parent, false);
        } else {
            view = convertView;
        }

        Event event = getItem(position);
        TextView EventName = view.findViewById(R.id.text_view_event);
        ImageView eventImage = view.findViewById(R.id.event_image);

        EventName.setText(event.getEventName());
        eventImage.setImageResource(event.getImageResId());
        //OpenAI, (2024, October 26), "How do I deal with a QR code once ive deleted it/ set it to null?", ChatGPT
        // Check if the QR code image is null and handle visibility
        if (event.hasQRCode()) {
            eventImage.setImageResource(event.getImageResId());
            eventImage.setVisibility(View.VISIBLE);
        } else {
            eventImage.setVisibility(View.GONE);
        }

        return view;
    }


}

