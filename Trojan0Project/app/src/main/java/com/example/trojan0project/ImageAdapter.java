package com.example.trojan0project;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class ImageAdapter extends ArrayAdapter<Image> {
    public ImageAdapter(Context context, ArrayList<Image> images){
        super(context, 0, images);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;
        if(convertView == null){
            view = LayoutInflater.from(getContext()).inflate(R.layout.image_layout, parent, false);


        } else {
            view = convertView;
        }
        Image image = getItem(position);
        ImageView imageView = view.findViewById(R.id.image_view);



        if(image !=null){
            Glide.with(getContext()).load(image.getImageId()).into(imageView);


        }

        return view;
    }
}

