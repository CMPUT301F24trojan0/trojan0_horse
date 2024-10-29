package com.example.trojan0project;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class ProfileAdapter extends ArrayAdapter<Profile> {
    public ProfileAdapter(Context context, ArrayList<Profile> profiles){
        super(context, 0, profiles);
    }

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
            profileName.setText(profile.getName());
            imageView.setImageResource(profile.getProfileImage());

        }

        return view;
    }
}
