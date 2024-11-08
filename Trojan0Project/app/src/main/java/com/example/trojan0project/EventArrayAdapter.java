/**
 * Purpose:
 * custom adapter for displaying a list of events in a ListView
 * allows each event item to display its name and associated QR code image.
 *
 * Design Rationale:
 * custom layout for each item to show the event name and an image
 *
 * Outstanding Issues:
 * No issues
 */

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
    /**
     * Constructor to initialize the adapter with the context and list of events.
     *
     * @param context The current context (typically the calling activity).
     * @param events The list of Event objects to display in the ListView.
     */
    public EventArrayAdapter(Context context, ArrayList<Event> events) {
        super(context, 0, events);
    }

    /**
     * Provides a view for an adapter view (ListView, GridView, etc.).
     *
     * @param position The position of the item within the adapter's data set.
     * @param convertView The old view to reuse, if possible.
     * @param parent The parent view that this view will eventually be attached to.
     * @return A View corresponding to the data at the specified position.
     */
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
        //OpenAI, (2024, November 6), "How do I set the Image as a Bitmap?", ChatGPT
        eventImage.setImageBitmap(event.getQrCodeBitmap());




        return view;
    }


}

