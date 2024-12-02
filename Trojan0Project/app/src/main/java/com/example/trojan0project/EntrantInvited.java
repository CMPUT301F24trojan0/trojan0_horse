

package com.example.trojan0project;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.annotation.Nullable;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;
/**
 * Purpose:
 * Displays a list of invited entrants for a specific event.
 * Gets the data from Firestore and populates the list with profiles of users
 * who have been invited to an event but have not yet accepted or declined
 *
 * Design Rationale:
 * Firebase Firestore is used to get and monitor entrant data
 * Profiles are displayes in a ListView and button triggers the data and displays list
 *
 * Outstanding Issues:
 * No issues
 */

public class EntrantInvited extends AppCompatActivity {

    private FirebaseFirestore db;
    private ArrayAdapter<Profile> ProfileAdapter;
    //private String targetEventId = "9AOwqyKOPMUO7rCZIF6V";
    private String targetEventId = "g7MK9lR8W8HwesTVgmdU";
    ListView entrantsInvited;
    private ArrayAdapter<Profile> profileArrayAdapter;
    public ArrayList<Profile> invited;

    /**
     * Initializes activity, setting up Firebase Firestore, adapter for the ListView
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_entrant_invited);
        entrantsInvited = findViewById(R.id.entrants_invited);
        db = FirebaseFirestore.getInstance();
        invited = new ArrayList<>();
        profileArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, invited);
        entrantsInvited.setAdapter(profileArrayAdapter);
        Button fetchInvitedButton = findViewById(R.id.fetchInvitedButton);

        fetchInvitedButton.setOnClickListener(v -> {

            getInvitedEntrants();

        });
        Log.d("InvitedActivity", "Calling getInvitedEntrants() method");
    }

    /**
     * Gets a list of invited entrants for an event in Firestore
     * Entrants are identified as users with a status of 1
     */
    private void getInvitedEntrants() {
        final CollectionReference collectionReference = db.collection("users");

        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {

                Log.d("Invited", "onEvent triggered");

                invited.clear();

                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    String userType = doc.getString("user_type");
                    if ("entrant".equals(userType)) {
                        Map<String, Long> events = (Map<String, Long>) doc.get("events");

                        if (events != null) {
                            for (Map.Entry<String, Long> entry : events.entrySet()) {

                                if (entry.getValue() == 1 && entry.getKey().equals(targetEventId)) {
                                    String eventId = entry.getKey();
                                    Log.d("Invited", "Event ID with 5: " + eventId);
                                    String firstName = doc.getString("first_name");
                                    String lastName = doc.getString("last_name");
                                    String email = doc.getString("email");

                                    //create profile
                                    Profile profile = new Profile(firstName, lastName, email);
                                    invited.add(profile);
                                    Log.d("Invited", "Added Profile: " + profile.getFirstName() + " " + profile.getLastName());
                                }
                            }
                        }
                    }
                }
                profileArrayAdapter.notifyDataSetChanged();

                for (Profile profile : invited) {
                    Log.d("Invited",
                            "First Name: " + profile.getFirstName() +
                                    ", Last Name: " + profile.getLastName() +
                                    ", Email: " + profile.getEmail());
                }
            }
        });
    }
}