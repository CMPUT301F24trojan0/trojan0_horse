package com.example.trojan0project.Controller.Entrant;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.trojan0project.View.Entrant.ImageGenerator;
import com.example.trojan0project.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * Purpose:
 * The `EntrantMain` class serves as the main activity for entrants, providing access to
 * profile management, event viewing, and QR code scanning. It integrates with Firebase Firestore
 * and Firebase Storage to retrieve and display user profile data, including profile pictures.
 *
 * Design Rationale:
 * - Utilizes Firebase Firestore for cloud-based data retrieval to manage user profile information.
 * - Leverages Firebase Storage to handle profile picture storage and retrieval.
 * - Implements notifications and permission handling to enhance user engagement and functionality.
 * - Provides intuitive navigation to key entrant functionalities via buttons.
 *
 * Outstanding Issues:
 * - No known issues at this time.
 */

public class EntrantMain extends AppCompatActivity {
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private static final int QR_SCANNER_REQUEST_CODE = 200;
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

        // Fetch and display notifications for the device
        Notification notificationHelper = new Notification();
        notificationHelper.getNotificationsForDevice(this, deviceId);

        requestNotificationPermission();

        // Call createNotificationChannel to ensure the channel is created on compatible devices
        createNotificationChannel(this);

        updateProfileButton.setOnClickListener(v -> {
            Intent profileIntent = new Intent(EntrantMain.this, ViewProfile.class);
            profileIntent.putExtra("DEVICE_ID", deviceId);
            startActivity(profileIntent);
        });

        // Set up the QR Code Scanner button
        scanQRcodeButton.setOnClickListener(v -> {
            Intent qrScannerIntent = new Intent(this, QrScannerActivity.class);
            startActivityForResult(qrScannerIntent, QR_SCANNER_REQUEST_CODE);
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
     * Requests permission for post notifications.
     */
    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // API 33+
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                // Request the permission if not already granted
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
            }
        }
    }

    /**
     * Creates a Notification Channel
     */
    public void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null && notificationManager.getNotificationChannel("default") == null) {
                CharSequence name = "Default Channel";
                String description = "Channel for default notifications";
                int importance = NotificationManager.IMPORTANCE_DEFAULT;
                NotificationChannel channel = new NotificationChannel("default", name, importance);
                channel.setDescription(description);
                notificationManager.createNotificationChannel(channel);
            }
        }
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

    /**
     * Reloads the user's profile data when the activity comes back into focus.
     */
    @Override
    protected void onResume() {
        super.onResume();
        loadProfileData(); // Reload the profile data when the activity comes back into focus
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == QR_SCANNER_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            String scannedData = data.getStringExtra("SCANNED_DATA");
            if (scannedData != null) {
                Toast.makeText(this, "QR Code Scanned: " + scannedData, Toast.LENGTH_LONG).show();
            }
        }
    }
}
