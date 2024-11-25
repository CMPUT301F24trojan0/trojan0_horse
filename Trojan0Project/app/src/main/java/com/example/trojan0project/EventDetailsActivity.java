package com.example.trojan0project;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class EventDetailsActivity extends AppCompatActivity {

    private TextView eventNameTextView;
    private TextView descriptionTextView;
    private TextView timeTextView;
    private ImageView posterImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        // Reference UI components
        eventNameTextView = findViewById(R.id.eventNameTextView);
        descriptionTextView = findViewById(R.id.descriptionTextView);
        timeTextView = findViewById(R.id.timeTextView);
        posterImageView = findViewById(R.id.posterImageView);

        // Get data from intent
        String eventName = getIntent().getStringExtra("eventName");
        String description = getIntent().getStringExtra("description");
        String time = getIntent().getStringExtra("time");
        String posterPath = getIntent().getStringExtra("posterPath");

        // Populate data into views
        eventNameTextView.setText(eventName);
        descriptionTextView.setText(description);
        timeTextView.setText(time);

        if (posterPath != null) {
            Glide.with(this).load(posterPath).into(posterImageView);
        }
    }
}
