package com.example.trojan0project.View.CommonViews;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.trojan0project.Model.Profile;
import com.example.trojan0project.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

/**
 * Purpose:
 * To hold profile objects.. Each item in the list displays a profile name and an associated image.
 * The images are loaded using the Glide library to handle image caching and efficient loading.
 *
 * Design Rationale:
 * Designed to hold profile objects.
 * Glide is used to load images into the ImageView.
 *
 * Outstanding Issues:
 * No Issues.
 */

public class ProfileAdapter extends ArrayAdapter<Profile> {
    /**
     * Constructs a ProfileAdapter with the specified context and list of profiles.
     *
     * @param context The context in which the adapter is created.
     * @param profiles The list of profiles to be displayed.
     */
    public ProfileAdapter(Context context, ArrayList<Profile> profiles){
        super(context, 0, profiles);
    }

    /**
     * Gets the view for a specific position in the list.
     *
     * @param position The position of the item within the adapter's data set.
     * @param convertView A recycled view that can be reused, or null if no view is available.
     * @param parent The parent view that this view will eventually be attached to.
     * @return The view for the specified position.
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;
        if(convertView == null){
            view = LayoutInflater.from(getContext()).inflate(R.layout.profile_layout, parent, false);
        } else {
            view = convertView;
        }
        Profile profile = getItem(position);
        TextView profileName = view.findViewById(R.id.profile_name);
        ImageView imageView = view.findViewById(R.id.profile_image);


        if(profile !=null){
            profileName.setText(profile.getUsername());
            //Glide.with(getContext()).load(profile.getProfileImage()).into(imageView);

        }

        return view;
    }
}
