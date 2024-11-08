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

public class FacilityArrayAdapter extends ArrayAdapter<Facility> {
    public FacilityArrayAdapter(Context context, ArrayList<Facility> facilities) {
        super(context, 0, facilities);
    }

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