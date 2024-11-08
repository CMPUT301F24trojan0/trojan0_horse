/**
 * Purpose:
 * This retrieves user profile pictures and event poster pictures from Firebase
 * and then displays in a grid layout
 *
 * Design Rationale:
 * User firebase storage to access the images and then downloads the URL of the images.
 * Then it stores the images in a list and displays them in a gridview using the ImageAdapter
 *
 * Outstanding Issues:
 * No issues
 */

package com.example.trojan0project;

import android.os.Bundle;
import android.util.Log;
import android.widget.GridView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class BrowseImagesAdmin extends MainActivity {
    private GridView imagesGridView;
    private ImageAdapter imageAdapter;
    private ArrayList<Image> images;
    private FirebaseFirestore db;
    private FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.browse_images_admin);

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        images = new ArrayList<>();
        imagesGridView = findViewById(R.id.images_list);

        getUserProfilePicture();
        getEventImages();

        //From https://www.youtube.com/watch?v=Dyix8I3bXIw by Master Coding, 2024-10-29
        imageAdapter = new ImageAdapter(this, images);
        imagesGridView.setAdapter(imageAdapter);
        imagesGridView.setNumColumns(2);

    }

    public void getUserProfilePicture(){
        db.collection("users")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots){
                        String deviceId = document.getString("profile_picture_url");
                        if (deviceId != null){
                            images.add(new Image(deviceId));

                        }
                    }
                    imageAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(BrowseImagesAdmin.this, "Error loading user pictures", Toast.LENGTH_SHORT).show());
    }



    public void getEventImages(){
        db.collection("events")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots){
                        String posterPath = document.getString("posterPath");
                        if (posterPath != null){
                            images.add(new Image(posterPath));
                        }
                    }
                    imageAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(BrowseImagesAdmin.this, "Error loading poster pictures", Toast.LENGTH_SHORT).show());

    }

}
