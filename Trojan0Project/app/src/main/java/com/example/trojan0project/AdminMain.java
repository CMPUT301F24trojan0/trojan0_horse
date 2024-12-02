/**
 * AdminMain is an activity that serves as the main screen for the admin panel of the application.
 * It provides buttons that navigate to different sections such as events, facilities, images, and profiles.
 * Each button, when clicked, opens a new activity to manage or view relevant content.
 */

package com.example.trojan0project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class AdminMain extends AppCompatActivity {

    private Button browseAllEvents;
    private Button browseAllFacilities;
    private Button browseAllImages;
    private Button browseAllProfiles;

    /**
     * Initializes the activity, sets the content view, references UI elements, and sets up click listeners
     * for each button to navigate to respective activities.
     *
     * @param savedInstanceState The saved instance state, if any, from the previous activity lifecycle.
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

        // Set click listeners for each button to navigate to corresponding activities
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