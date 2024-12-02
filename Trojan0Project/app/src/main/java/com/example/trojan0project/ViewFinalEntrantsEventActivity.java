/**
 * This activity displays the final entrants for a specific event.
 * It retrieves the list of entrants from Firebase Firestore and displays them
 * in a RecyclerView. Entrants are filtered by their status for the given event.
 */

package com.example.trojan0project;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ViewFinalEntrantsEventActivity extends AppCompatActivity {

    private static final String TAG = "ViewFinalEntrants";
    private RecyclerView entrantsRecyclerView;
    private EntrantsAdapter entrantsAdapter;
    private FirebaseFirestore firestore;

    /**
     * Called when the activity is created. It sets up the layout, initializes
     * the Firestore instance, and retrieves the event ID from the intent.
     * If a valid event ID is found, it fetches the entrants for the event.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down, this Bundle contains
     *                           the data it most recently supplied in
     *                           {@link #onSaveInstanceState(Bundle)}.
     *                           Otherwise, it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_final_entrants_event);

        entrantsRecyclerView = findViewById(R.id.entrants_recycler_view);
        entrantsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        firestore = FirebaseFirestore.getInstance();

        String eventId = getIntent().getStringExtra("eventId");
        if (eventId != null) {
            fetchEntrants(eventId);
        } else {
            Toast.makeText(this, "Invalid Event ID", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Fetches the list of entrants for the given event ID from Firebase Firestore.
     * It filters users by their type ("entrant") and checks if they have a status
     * of 2 (final entrant) for the specified event. The filtered list of entrants
     * is then displayed in a RecyclerView.
     *
     * @param eventId The ID of the event for which the entrants are being fetched.
     */
    private void fetchEntrants(String eventId) {
        firestore.collection("users").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<String> entrantsList = new ArrayList<>();
                    queryDocumentSnapshots.forEach(documentSnapshot -> {
                        String userType = documentSnapshot.getString("user_type");
                        if ("entrant".equals(userType)) {
                            Map<String, Long> eventsMap = (Map<String, Long>) documentSnapshot.get("events");
                            if (eventsMap != null && eventsMap.containsKey(eventId) && eventsMap.get(eventId) == 2) {
                                String entrantName = documentSnapshot.getString("first_name") + " " +
                                        documentSnapshot.getString("last_name");
                                entrantsList.add(entrantName);
                            }
                        }
                    });
                    if (entrantsList.isEmpty()) {
                        Toast.makeText(this, "No entrants found for this event.", Toast.LENGTH_SHORT).show();
                    }
                    entrantsAdapter = new EntrantsAdapter(entrantsList);
                    entrantsRecyclerView.setAdapter(entrantsAdapter);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to fetch entrants", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error fetching entrants: " + e.getMessage());
                });
    }
}
