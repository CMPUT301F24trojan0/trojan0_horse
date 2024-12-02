package com.example.trojan0project.Controller.CommonControllers;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.trojan0project.R;

/**
 * Purpose:
 * displays a message indicating that no events have been created yet.
 *
 * Design Rationale:
 * TextView is used to provide feedback to the user when there are no events to display.
 *
 * Outstanding Issues:
 * No issues
 */

public class EventsListActivity extends AppCompatActivity {

    /**
     * Initializes the activity and displays a message if there are no events.
     *
     * @param savedInstanceState The saved state of the activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events_list);
    }
}
