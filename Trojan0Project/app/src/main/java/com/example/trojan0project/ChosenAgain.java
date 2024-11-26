package com.example.trojan0project;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class ChosenAgain extends AppCompatActivity {

    private FirebaseFirestore db;
    private ArrayAdapter<Profile> ProfileAdapter;
    private String targetEventId = "9AOwqyKOPMUO7rCZIF6V";
    private int numAttendees = 2;
    ListView entrantsWaitlist;

    private ListView entrantWaitlist;
    private ArrayAdapter<Profile> profileArrayAdapter;
    public ArrayList<Profile> waitList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entrants_join_waitlist);
        db = FirebaseFirestore.getInstance();

        waitList = new ArrayList<>();
        entrantsWaitlist = findViewById(R.id.entrants_wait_list);
        profileArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, waitList);
        entrantsWaitlist.setAdapter(profileArrayAdapter);

        Button sampleWaitlistButton = findViewById(R.id.sampleWaitlistButton);


        //sampleWaitlistButton.setOnClickListener(v -> {

            //sampleWaitlist(numAttendees);
        //});
        //Log.d("sampleWaitlistActivity", "Calling sampleWaitlist() method");

    }


}

