package com.example.trojan0project;

import static android.content.Intent.getIntent;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class EntrantMain extends AppCompatActivity {
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private String deviceId;
    private String username;
    private ImageView profilePicture;
    private TextView profileWelcomeText;
    private Button updateProfileButton;
    private Button scanQRcodeButton;
    private Button viewAllEventsButton;

    /**
     * Initializes the activity, retrieves the device ID, sets up Firestore, and initializes the UI elements.
     *
     * @param savedInstanceState The saved state of the activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entrant_main); // Links the XML layout to this activity

        // Initialize Firestore and Firebase Storage
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        // Retrieve the device ID from the intent
        Intent intent = getIntent();
        deviceId = intent.getStringExtra("DEVICE_ID");

        // Reference UI elements
        profilePicture = findViewById(R.id.mainEntrantIcon);
        profileWelcomeText = findViewById(R.id.mainEntrantTitle);
        updateProfileButton = findViewById(R.id.update_profile_button);
        scanQRcodeButton = findViewById(R.id.scan_qr_code_button);
        viewAllEventsButton = findViewById(R.id.view_all_events_button);

        loadProfileData();

        updateProfileButton.setOnClickListener(v -> {
            Intent profileIntent = new Intent(EntrantMain.this, ViewProfile.class);
            profileIntent.putExtra("DEVICE_ID", deviceId);
            startActivity(profileIntent);
        });

        viewAllEventsButton.setOnClickListener(v -> {
            Intent profileIntent = new Intent(EntrantMain.this, ViewEvents.class);
            profileIntent.putExtra("DEVICE_ID", deviceId);
            startActivity(profileIntent);
        });
    }

    /**
     * Loads the user's profile data from Firestore and populates the UI fields with this data.
     */
    private void loadProfileData() {
        db.collection("users").document(deviceId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    username = document.getString("username");
                    profileWelcomeText.setText("Welcome " + username + "!");
                }

                // Load profile picture from URL
                String profilePicUrl = document.getString("profile_picture_url");
                if (profilePicUrl != null) {
                    Glide.with(this).load(profilePicUrl).into(profilePicture);
                } else {
                    newImage(username);
                }
            }
        });
    }

    /**
     * Displays a new image with the user's initials when no profile image is set.
     *
     * @param username The username used to generate the initial.
     */
    private void newImage(String username) {
        ImageGenerator mydrawing = new ImageGenerator(this);
        mydrawing.setUserText(String.valueOf(username.charAt(0)));
        profilePicture.setImageDrawable(mydrawing);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadProfileData(); // Reload the profile data when the activity comes back into focus
    }
}
