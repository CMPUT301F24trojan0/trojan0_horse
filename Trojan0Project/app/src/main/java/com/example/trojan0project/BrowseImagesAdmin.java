package com.example.trojan0project;

import android.os.Bundle;
import android.widget.GridView;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class BrowseImagesAdmin extends MainActivity {
    private GridView imagesGridView;
    private ImageAdapter imageAdapter;
    private ArrayList<Image> images;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.browse_images_admin);


        images = new ArrayList<Image>();
        images.add(new Image(R.drawable.baseline_image_24));
        images.add(new Image(R.drawable.baseline_image_24));
        images.add(new Image(R.drawable.baseline_image_24));
        images.add(new Image(R.drawable.baseline_image_24));
        images.add(new Image(R.drawable.baseline_image_24));
        images.add(new Image(R.drawable.baseline_image_24));
        images.add(new Image(R.drawable.baseline_image_24));
        images.add(new Image(R.drawable.baseline_image_24));
        images.add(new Image(R.drawable.baseline_image_24));

        imagesGridView = findViewById(R.id.images_list);

        //From https://www.youtube.com/watch?v=Dyix8I3bXIw by Master Coding, 2024-10-29
        imageAdapter = new ImageAdapter(this, images);
        imagesGridView.setAdapter(imageAdapter);
        imagesGridView.setNumColumns(2);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

}
