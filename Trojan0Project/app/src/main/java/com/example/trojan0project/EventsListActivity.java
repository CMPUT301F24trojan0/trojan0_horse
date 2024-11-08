package com.example.trojan0project;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class EventsListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events_list);

        // Display "No events created yet." message
        TextView noEventsText = findViewById(R.id.no_events_text);
        noEventsText.setText("No events created yet.");
    }
}
