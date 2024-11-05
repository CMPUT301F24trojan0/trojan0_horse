package com.example.trojan0project;



import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class JoinWaitlist extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_join_waitlist);

        TextView eventTitle = findViewById(R.id.event_title);
        TextView eventLocation = findViewById(R.id.location_label);
        TextView eventTime = findViewById(R.id.time_label);
        TextView eventRegistrationStatus = findViewById(R.id.registration_label);
        TextView eventMoreInfo = findViewById(R.id.more_info_label);
        Button joinWaitlistButton = findViewById(R.id.join_waitlist_button);

        eventTitle.setText("Sample Event Title");
        eventLocation.setText("123 Main St, Cityville");
        eventTime.setText("12:00 PM - 2:00 PM");
        eventRegistrationStatus.setText("Open");
        eventMoreInfo.setText("This is a sample event that provides details about the event.");


        ArrayList<Profile> waitlist = new ArrayList<>();
        WaitlistAdapter waitlistAdapter = new WaitlistAdapter(this, waitlist);
        ListView waitlistListView = findViewById(R.id.waitlist_view);
        waitlistListView.setAdapter(waitlistAdapter);

        Profile mockProfile = new Profile("John", "Doe", "johndoe@example.com");

        joinWaitlistButton.setOnClickListener(v -> {
            // Create a new instance of WaitlistSignUpFragment with the profile data
           JoinWaitlistFragment dialog = new JoinWaitlistFragment(mockProfile, updatedProfile -> {
                // Add the confirmed profile to the waitlist adapter
                waitlistAdapter.addToWaitlist(updatedProfile);
            });

            // Show the dialog using the FragmentManager
            dialog.show(getSupportFragmentManager(), "waitlistSignUp");
        });




    }
}