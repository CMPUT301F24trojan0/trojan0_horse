package com.example.trojan0project;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class CancelledEntrantsAdapter extends ArrayAdapter<Profile> {

    public CancelledEntrantsAdapter(@NonNull Context context, @NonNull ArrayList<Profile> profiles) {
        super(context, 0, profiles);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_user, parent, false);
        }

        Profile profile = getItem(position);

        TextView firstNameView = convertView.findViewById(R.id.firstNameTextView);
        TextView lastNameView = convertView.findViewById(R.id.lastNameTextView);
        TextView emailView = convertView.findViewById(R.id.emailTextView);

        if (profile != null) {
            firstNameView.setText(profile.getFirstName());
            lastNameView.setText(profile.getLastName());
            emailView.setText(profile.getEmail());
        }

        return convertView;
    }
}

