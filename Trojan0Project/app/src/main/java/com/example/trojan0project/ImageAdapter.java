/**
 * Purpose:
 * Is designed to hold image objects and display them.
 *
 * Design Rationale:
 * The adapter extends ArrayAdapter to take some of its functionality but instead to display data of type images.
 * Glide is used to handle image loading.
 *
 * Outstanding Issues:
 * - No Issues.
 */

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
    /**
     * Constructor for ImageAdapter.
     *
     * @param context The current context.
     * @param images  The list of Image objects to display.
     */
    public ImageAdapter(Context context, ArrayList<Image> images){
        super(context, 0, images);
    }

    /**
     * Creates and returns the view for each Image item in the list.
     *
     * @param position    The position of the item within the adapter's data set.
     * @param convertView The old view to reuse, if possible.
     * @param parent      The parent view that this view will eventually be attached to.
     * @return The constructed view for the Image item at the specified position.
     */
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

