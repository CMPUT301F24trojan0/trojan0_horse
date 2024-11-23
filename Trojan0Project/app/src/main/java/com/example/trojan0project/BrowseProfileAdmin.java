package com.example.trojan0project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class BrowseProfileAdmin extends MainActivity implements RemoveProfileFragment.RemoveProfileDialogListener {

    public ArrayList<Profile> dataList;
    private ListView profileList;
    private ProfileAdapter profileAdapter;
    private FirebaseFirestore db;
    //private String deviceId;

    private static final String TAG = "BrowseProfileAdmin"; // Added log tag

    /**
     * Sets up the activity, including initializing Firestore, loading profiles, and setting up list and navigation buttons.
     *
     * @param savedInstanceState The saved state of the activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_browse_profile_admin);

        //FirebaseFirestore.setLoggingEnabled(true);

        db = FirebaseFirestore.getInstance();
        profileList = findViewById(R.id.profile_list);
        dataList = new ArrayList<>();
        profileAdapter = new ProfileAdapter(this, dataList);
        profileList.setAdapter(profileAdapter);

        //deviceId = getIntent().getStringExtra("DEVICE_ID");
        //Log.d(TAG, "Device ID received: " + deviceId);  // Log device ID

        ImageButton ImagePage = findViewById(R.id.camera_button);

        // Fetching profiles from Firestore
        getProfile();

        profileList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Profile selectedProfile = dataList.get(i);

                new RemoveProfileFragment(selectedProfile).show(getSupportFragmentManager(), "removeProfile");
            }
        });

        ImagePage.setOnClickListener(v -> {
            Log.d(TAG, "Navigating to BrowseImagesAdmin activity");
            Intent intent = new Intent(BrowseProfileAdmin.this, BrowseImagesAdmin.class);
            startActivity(intent);
        });

    }

    /**
     * Gets the list of profiles from Firestore where user type is "entrant" and adds them to the dataList.
     * Notifies the adapter to refresh the view.
     */
    private void getProfile() {
        Log.d(TAG, "Fetching profiles from Firestore...");
        db.collection("users")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    dataList.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String userType = document.getString("user_type");
                        if ("entrant".equals(userType)) {
                            String username = document.getString("username");
                            //String profileImage = document.getString("profile_url");
                            String deviceId = document.getId();
                            Log.d(TAG, "User Type: " + userType);
                            Log.d(TAG, "Document ID (Device ID): " + deviceId);
                            Log.d(TAG, "Adding profile: " + username);
                            //Log.d(TAG, "Profile Image URL: " + profileImage);// Log each added profile
                            dataList.add(new Profile(username, deviceId));
                            Log.d(TAG, "Profile added to list: " + username);
                        }
                    }
                    profileAdapter.notifyDataSetChanged();
                    Log.d(TAG, "Profile list updated with " + dataList.size() + " profiles.");
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to get profiles", e);  // Log the error if fetching fails
                    Toast.makeText(this, "Failed to get data", Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * Removes a selected profile from Firestore and updates the local list view.
     *
     * @param profile The profile to be removed.
     */
    @Override
    public void removeProfile(Profile profile) {
        String deviceId = profile.getDeviceId();
        Log.d(TAG, "Attempting to remove profile with Device ID: " + deviceId);
        db.collection("users")
                .document(deviceId)
                .delete()
                .addOnSuccessListener(Void -> {
                    dataList.remove(profile);
                    profileAdapter.notifyDataSetChanged();
                    Toast.makeText(this, "Profile is deleted", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Profile deleted successfully.");
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to delete profile", e);  // Log the error if deletion fails
                    Toast.makeText(this, "Profile not deleted", Toast.LENGTH_SHORT).show();
                });
    }
}
