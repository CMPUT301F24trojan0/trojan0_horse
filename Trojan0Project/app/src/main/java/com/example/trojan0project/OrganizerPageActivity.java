package com.example.trojan0project;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class OrganizerPageActivity extends AppCompatActivity implements EditFacilityFragment.OnFacilityNameUpdatedListener {

    private Button editFacilityButton, viewEventsButton;
    private TextView facilityNameText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer_page);

        editFacilityButton = findViewById(R.id.edit_facility_button);
        viewEventsButton = findViewById(R.id.view_events_button);
        facilityNameText = findViewById(R.id.facility_name_text);

        // Retrieve the facility name passed from OrganizerSignUpActivity
        String facilityName = getIntent().getStringExtra("facility_name");
        facilityNameText.setText(facilityName != null ? facilityName : "No facility name provided");

        editFacilityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open EditFacilityFragment
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new EditFacilityFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

        viewEventsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Code to view all events
            }
        });
    }

    @Override
    public void onFacilityNameUpdated(String newFacilityName) {
        // Update the facility name displayed on the OrganizerPageActivity
        facilityNameText.setText(newFacilityName);
    }
}
