package com.example.trojan0project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Purpose:
 * This class serves as the main admin panel, providing navigation to sections such as
 * event management, facility management, image browsing, and profile browsing.
 *
 * Design Rationale:
 * - Uses buttons for intuitive navigation to key admin functionalities.
 * - Centralizes all administrative options in a single screen for efficiency.
 *
 * Outstanding Issues:
 * - No known issues at this time.
 */

public class AdminMain extends AppCompatActivity {
    private Button browseAllEvents;
    private Button browseAllFacilities;
    private Button browseAllImages;
    private Button browseAllProfiles;

    /**
     * Initializes the activity, retrieves the device ID, sets up Firestore, and initializes the UI elements.
     *
     * @param savedInstanceState The saved state of the activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_main); // Links the XML layout to this activity

        // Reference UI elements
        browseAllEvents = findViewById(R.id.browse_events_button);
        browseAllFacilities = findViewById(R.id.browse_facilities_button);
        browseAllImages = findViewById(R.id.browse_images_button);
        browseAllProfiles = findViewById(R.id.browse_profiles_button);

        browseAllEvents.setOnClickListener(v -> {
            Intent profileIntent = new Intent(AdminMain.this, EventActivity.class);
            startActivity(profileIntent);
        });

        browseAllFacilities.setOnClickListener(v -> {
            Intent profileIntent = new Intent(AdminMain.this, FacilityActivity.class);
            startActivity(profileIntent);
        });

        browseAllImages.setOnClickListener(v -> {
            Intent profileIntent = new Intent(AdminMain.this, BrowseImagesAdmin.class);
            startActivity(profileIntent);
        });

        browseAllProfiles.setOnClickListener(v -> {
            Intent profileIntent = new Intent(AdminMain.this, BrowseProfileAdmin.class);
            startActivity(profileIntent);
        });
    }
}

