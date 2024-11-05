package com.example.trojan0project;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class WaitlistAdapter extends ArrayAdapter<Profile> {

    public WaitlistAdapter(Context context, ArrayList<Profile> profiles) {
        super(context, 0, profiles);
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.waitlist_items, parent, false);
        }

        Profile profile = getItem(position);
        TextView firstNameView = view.findViewById(R.id.first_name_waitlist);
        TextView lastNameView = view.findViewById(R.id.last_name);
        TextView emailView = view.findViewById(R.id.email);

        firstNameView.setText(profile.getFirstName());
        lastNameView.setText(profile.getLastName());
        emailView.setText(profile.getEmail());

        return view;
    }

    public void addToWaitlist(Profile profile){
        add(profile);
        notifyDataSetChanged();
    }


}
