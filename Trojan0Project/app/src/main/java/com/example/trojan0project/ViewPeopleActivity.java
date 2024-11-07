package com.example.trojan0project;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class ViewPeopleActivity extends AppCompatActivity {

        private Switch limitToggle;
        private FrameLayout entrantLimitContainer;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.view_people); // Ensure this matches `view_people.xml`

            // Initialize toggle and container views
            limitToggle = findViewById(R.id.limit_toggle);
            entrantLimitContainer = findViewById(R.id.entrant_limit_container);

            // Set up toggle listener to show/hide fragment
            limitToggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    // Show the entrant limit fragment
                    entrantLimitContainer.setVisibility(View.VISIBLE);
                } else {
                    // Hide the entrant limit fragment
                    entrantLimitContainer.setVisibility(View.GONE);
                }
            });
        }
    }

