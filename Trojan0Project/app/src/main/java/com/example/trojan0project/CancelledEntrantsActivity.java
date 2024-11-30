package com.example.trojan0project;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class CancelledEntrantsActivity extends AppCompatActivity {
    private static final String TAG = "CancelledEntrantsActivity";

    private FirebaseFirestore db;
    private ListView cancelledEntrantsListView;
    private ArrayList<Profile> cancelledEntrantsList;
    private CancelledEntrantsAdapter adapter;
    private String eventId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancelled_entrants);

        db = FirebaseFirestore.getInstance();
        cancelledEntrantsListView = findViewById(R.id.cancelledEntrantsListView);
        cancelledEntrantsList = new ArrayList<>();

        adapter = new CancelledEntrantsAdapter(this, cancelledEntrantsList);
        cancelledEntrantsListView.setAdapter(adapter);

        eventId = getIntent().getStringExtra("EVENT_ID");
        if (eventId != null) {
            fetchCancelledEntrants(eventId);
        } else {
            Toast.makeText(this, "Error: Event ID not found", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchCancelledEntrants(String eventId) {
        db.collection("users")
                .whereEqualTo("events." + eventId, 3) // Query users with status '3' for the event
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String firstName = document.getString("first_name");
                        String lastName = document.getString("last_name");
                        String email = document.getString("email");

                        if (firstName != null && lastName != null && email != null) {
                            cancelledEntrantsList.add(new Profile(firstName, lastName, email));
                        }
                    }
                    adapter.notifyDataSetChanged(); // Refresh the ListView
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching cancelled entrants: ", e);
                    Toast.makeText(this, "Failed to fetch cancelled entrants", Toast.LENGTH_SHORT).show();
                });
    }
}
