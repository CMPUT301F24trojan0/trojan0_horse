package com.example.trojan0project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class DisplayEventDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.display_event_details_admin);



        //OpenAI, (2024, November 6 2024), "How do I transfer the city from one activity to another?", ChatGPT
        Intent intent = getIntent();
        String selectedEventTitle = intent.getStringExtra("event_title");

        TextView eventTextView = findViewById(R.id.event_title);
        eventTextView.setText(selectedEventTitle);




    }
}

