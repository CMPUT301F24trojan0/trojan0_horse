package com.example.trojan0project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class EventDetailsActivity extends AppCompatActivity {

    private static final String TAG = "EventDetailsActivity";

    private ImageView posterImageView;
    private TextView eventNameTextView, descriptionTextView, timeTextView;
    private Button cancelButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        Log.d(TAG, "onCreate: Activity started");

        // Reference views
        posterImageView = findViewById(R.id.posterImageView);
        eventNameTextView = findViewById(R.id.eventNameTextView);
        descriptionTextView = findViewById(R.id.descriptionTextView);
        timeTextView = findViewById(R.id.timeTextView);
        cancelButton = findViewById(R.id.cancelButton);

        // Retrieve event details passed via intent
        Intent intent = getIntent();
        if (intent != null) {
            String eventName = intent.getStringExtra("eventName");
            String description = intent.getStringExtra("description");
            String posterUrl = intent.getStringExtra("posterPath");
            String time = intent.getStringExtra("time");

            Log.d(TAG, "onCreate: Received event details");
            Log.d(TAG, "onCreate: eventName = " + eventName);
            Log.d(TAG, "onCreate: description = " + description);
            Log.d(TAG, "onCreate: posterUrl = " + posterUrl);
            Log.d(TAG, "onCreate: time = " + time);

            // Set values in views
            eventNameTextView.setText(eventName != null ? eventName : "N/A");
            descriptionTextView.setText(description != null ? description : "N/A");
            timeTextView.setText(time != null ? String.format("Time: %s", time) : "N/A");

            if (posterUrl != null) {
                Glide.with(this).load(posterUrl).into(posterImageView);
                Log.d(TAG, "onCreate: Loaded poster image");
            }
        } else {
            Log.e(TAG, "onCreate: Intent is null");
        }

        // Cancel button listener
        cancelButton.setOnClickListener(v -> {
            Log.d(TAG, "Cancel button clicked");
            finish(); // Ensure activity is properly finished
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: EventDetailsActivity destroyed");
    }
}
