package com.example.trojan0project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);




        //for each role we can use a if statement and for each role the base activity main will be different
        setContentView(R.layout.activity_main_admin);
        ImageButton homeButton = findViewById(R.id.home_button);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Home Button Clicked", Toast.LENGTH_SHORT).show();

            }
        });

        ImageButton profileButton = findViewById(R.id.profile_button);
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Profile Button Clicked", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, BrowseProfileAdmin.class);
                startActivity(intent);

            }
        });
        ImageButton imageButton = findViewById(R.id.camera_button);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Camera Button Clicked", Toast.LENGTH_SHORT).show();

            }
        });

        ImageButton eventsButton = findViewById(R.id.events_button);
        eventsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Events Button Clicked", Toast.LENGTH_SHORT).show();

            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

    }



}