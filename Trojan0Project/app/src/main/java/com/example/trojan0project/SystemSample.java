package com.example.trojan0project;

import static android.content.ContentValues.TAG;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class SystemSample extends AppCompatActivity {

    private FirebaseFirestore db;
    private ArrayAdapter<Profile> ProfileAdapter;
    private String targetEventId = "9AOwqyKOPMUO7rCZIF6V";
    ListView entrantsWaitlist;

    private ListView entrantWaitlist;
    private ArrayAdapter<Profile> profileArrayAdapter;
    public ArrayList<Profile> waitList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entrants_join_waitlist); // Set your layout file here
        db = FirebaseFirestore.getInstance();

        waitList = new ArrayList<>();
        entrantsWaitlist = findViewById(R.id.entrants_wait_list);
        profileArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, waitList);
        entrantsWaitlist.setAdapter(profileArrayAdapter);

        Button fetchWaitlistButton = findViewById(R.id.fetchWaitlistButton);

        // Set the OnClickListener
        fetchWaitlistButton.setOnClickListener(v -> {
            // Call the method when the button is clicked
            getWaitlist();
        });
        Log.d("WaitlistActivity", "Calling getWaitlist() method");

    }

    private void getWaitlist() {
        final CollectionReference collectionReference = db.collection("users");
        waitList.clear();


        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {


                Log.d("Waitlist", "onEvent triggered");


                for (QueryDocumentSnapshot doc: queryDocumentSnapshots){
                    String userType = doc.getString("user_type");
                    if ("entrant".equals(userType)){
                        Map<String, Long> events = (Map<String, Long>) doc.get("events");

                        if (events != null) {
                            for (Map.Entry<String, Long> entry : events.entrySet()) {

                                if (entry.getValue() == 0 && entry.getKey().equals(targetEventId)) {
                                    String eventId = entry.getKey(); // This is the event ID
                                    Log.d("Waitlist", "Event ID with 0: " + eventId);
                                    String firstName = doc.getString("first_name");
                                    String lastName = doc.getString("last_name");
                                    String email = doc.getString("email");

                                   //create profile
                                    Profile profile = new Profile(firstName, lastName, email);
                                    waitList.add(profile);
                                    Log.d("Waitlist", "Added Profile: " + profile.getFirstName() + " " + profile.getLastName());

                                }
                            }
                        }

                    }
                }
                profileArrayAdapter.notifyDataSetChanged();

                for (Profile profile : waitList) {
                    Log.d("Waitlist",
                            "First Name: " + profile.getFirstName() +
                                    ", Last Name: " + profile.getLastName() +
                                    ", Email: " + profile.getEmail());
                }
            }


        });




    }
}

