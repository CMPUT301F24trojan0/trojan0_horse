package com.example.trojan0project;

import android.os.Bundle;
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

    private void getUserProfilePicture(){
        db.collection("users")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots){
                        String deviceId = document.getString("deviceId");
                        if (deviceId != null){
                            loadImageFromStorage(deviceId);
                        }
                    }
                    imageAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(BrowseImagesAdmin.this, "Error loading user pictures", Toast.LENGTH_SHORT).show());
    }

    private void loadImageFromStorage(String deviceId){
        StorageReference imageRef = storage.getReference().child("profilePictures/" + deviceId);
        images.add(new Image(imageRef.toString()));
        imageAdapter.notifyDataSetChanged();
    }

    private void getEventImages(){
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
