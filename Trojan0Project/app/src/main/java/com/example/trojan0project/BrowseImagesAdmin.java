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

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
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

public class BrowseImagesAdmin extends MainActivity implements RemoveImageFragment.removeImageListener {
    private GridView imagesGridView;
    private ImageAdapter imageAdapter;
    private ArrayList<Image> images;
    private FirebaseFirestore db;
    private FirebaseStorage storage;


    /**
     * Initializes the activity, setting up Firebase services, loading images, and configuring the grid view.
     * Also provides navigation to the Facility page.
     *
     * @param savedInstanceState The saved state of the activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.browse_images_admin);

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        images = new ArrayList<>();
        imagesGridView = findViewById(R.id.images_list);

        ImageButton FacilityPage = findViewById(R.id.facility_button);

        getUserProfilePicture();
        getEventImages();

        //From https://www.youtube.com/watch?v=Dyix8I3bXIw by Master Coding, 2024-10-29
        imageAdapter = new ImageAdapter(this, images);
        imagesGridView.setAdapter(imageAdapter);
        imagesGridView.setNumColumns(2);

        imagesGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Image selectedImage = images.get(i);

                new RemoveImageFragment(selectedImage).show(getSupportFragmentManager(), "removeImage");
            }
        });

        FacilityPage.setOnClickListener(v -> {
            Intent intent = new Intent(BrowseImagesAdmin.this, FacilityActivity.class);
            startActivity(intent);
        });

    }

    /**
     * Retrieves user profile pictures from Firestore and adds them to the images list.
     * Notifies the adapter of any updates to display the new images in the grid.
     */
    public void getUserProfilePicture() {
        db.collection("users")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String profilePictureUrl = document.getString("profile_picture_url");
                        if (profilePictureUrl != null && !profilePictureUrl.isEmpty()) {
                            images.add(new Image(profilePictureUrl));

                        }
                    }
                    imageAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(BrowseImagesAdmin.this, "Error loading user pictures", Toast.LENGTH_SHORT).show());
    }


    /**
     * Retrieves event poster images from Firestore and adds them to the images list.
     * Notifies the adapter of any updates to display the new images in the grid.
     */
    public void getEventImages() {
        db.collection("events")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String posterPath = document.getString("posterPath");
                        if (posterPath != null && !posterPath.isEmpty()) {
                            images.add(new Image(posterPath));
                        }
                    }
                    imageAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(BrowseImagesAdmin.this, "Error loading poster pictures", Toast.LENGTH_SHORT).show());

    }


    @Override
    public void removeImage(Image image) {
        String imageId = image.getImageId();
        try {
            StorageReference imageRef = storage.getReferenceFromUrl(imageId);
            imageRef.delete()
                    .addOnSuccessListener(Void -> {
                        Log.d("BrowseImagesAdmin", "Image successfully deleted from storage: " + imageId);
                        db.collection("users")
                                .whereEqualTo("profile_picture_url", imageId)
                                .get()
                                .addOnSuccessListener(queryDocumentSnapshots -> {
                                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                        Log.d("BrowseImagesAdmin", "Updating Firestore user document: " + document.getId());
                                        document.getReference().update("profile_picture_url", null);
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("Firestore", "Failed to remove profile picture", e);
                                });
                        db.collection("events")
                                .whereEqualTo("posterPath", imageId)
                                .get()
                                .addOnSuccessListener(queryDocumentSnapshots -> {
                                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                        Log.d("BrowseImagesAdmin", "Updating Firestore event document: " + document.getId());
                                        document.getReference().update("posterPath", null);
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("Firestore", "Failed to remove poster path", e);
                                });
                        images.remove(image);
                        imageAdapter.notifyDataSetChanged();
                        Log.d("BrowseImagesAdmin", "Image removed from adapter list.");
                    })
                    .addOnFailureListener(e -> {
                        Log.e("FirebaseStorage", "Error deleting image", e);
                    });
        } catch (IllegalArgumentException e){
            Log.e("BrowseImagesAdmin", "Invalid URL provided for StorageReference.", e);
        }
    }
}


