/**
 * This adapter helps display a list of profiles in a ListView for the waitlist. It shows each profile's
 * first name, last name, and email.
 *
 * Purpose:
 * The purpose of this adapter is to manage and display profiles on the waitlist in a ListView. The adapter
 * takes a list of profiles and shows important information like the first name, last name, and email of each person.
 * It also includes a method to add new profiles to the waitlist.
 *
 * Design Rationale:
 * This adapter uses the `ArrayAdapter` class for displaying data in a ListView. It keeps the
 * code for displaying the profiles separate from the rest of the app. The `addToWaitlist` method allows the app
 * to add new profiles to the list and automatically update the display.
 *
 * Outstanding Issues:
 * No Issues
 */
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
