package com.example.trojan0project; // Replace with your package name

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;

import com.example.trojan0project.R;

public class MainActivity extends AppCompatActivity {

    private ImageButton addEventButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Ensure this matches your XML file name

        // Initialize the "Add Event" button
        addEventButton = findViewById(R.id.add_event_button);

        // Set click listener to navigate to CreateEventActivity
        addEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, com.example.trojan0project.CreateEventActivity.class);
                startActivity(intent);
            }
        });
    }
}
