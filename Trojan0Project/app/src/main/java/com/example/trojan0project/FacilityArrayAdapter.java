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

/**
 * Purpose:
 * Hold and handle objects of type facility.

 * Design Rationale:
 *Extends ArrayAdapter to take some of its functionality and use it to hand facility objects.

 * Outstanding Issues:
 * - No Issues.
 */

public class FacilityArrayAdapter extends ArrayAdapter<Facility> {
    /**
     * Constructor to initialize the adapter with a context and a list of facilities.
     *
     * @param context    The current context.
     * @param facilities The list of facilities to be displayed.
     */
    public FacilityArrayAdapter(Context context, ArrayList<Facility> facilities) {
        super(context, 0, facilities);
    }

    /**
     * Provides a view for an adapter view (ListView, GridView, etc.).
     *
     * @param position    The position of the item within the adapter's data set.
     * @param convertView The old view to reuse, if possible.
     * @param parent      The parent view that this view will eventually be attached to.
     * @return A View corresponding to the data at the specified position.
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup
            parent) {
        View view;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.content_facility,
                    parent, false);
        } else {
            view = convertView;
        }

        Facility facility = getItem(position);
        TextView FacilityName = view.findViewById(R.id.text_view_facility);

        FacilityName.setText(facility.getFacilityName());


        return view;
    }


}