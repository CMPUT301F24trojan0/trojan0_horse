package com.example.trojan0project;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.trojan0project.R;

public class OrganizerPageActivity extends AppCompatActivity {

    private Button editFacilityButton, viewEventsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer_page);

        editFacilityButton = findViewById(R.id.edit_facility_button);
        viewEventsButton = findViewById(R.id.view_events_button);

        editFacilityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Code to edit facility
            }
        });

        viewEventsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Code to view all events
            }
        });
    }
}
