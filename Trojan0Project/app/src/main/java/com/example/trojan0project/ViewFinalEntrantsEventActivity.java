package com.example.trojan0project;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ViewFinalEntrantsEventActivity extends AppCompatActivity {

    private static final String TAG = "ViewFinalEntrantsEventActivity";
    private RecyclerView entrantsRecyclerView;
    private EntrantsAdapter profileAdapter;
    private ArrayList<String> profileList;
    private FirebaseFirestore firestore;
    private String eventID;
    private Spinner statusSpinner;
    private int selectedStatus1 = -1;
    private int selectedStatus2 = -2;
    private Long participationStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_final_entrants_event);

        Toolbar toolbar = findViewById(R.id.leave_view_people_toolbar);
        setSupportActionBar(toolbar);

        // Set the title of the action bar to be empty
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);  // Enable the "up" button
        }

        eventID = getIntent().getStringExtra("eventId");
        if (eventID == null) {
            Log.e(TAG, "Event ID not received in ViewFinalEntrantsEventActivity");
            Toast.makeText(this, "Error: Missing Event ID.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        firestore = FirebaseFirestore.getInstance();
        entrantsRecyclerView = findViewById(R.id.entrants_recycler_view);
        entrantsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        profileList = new ArrayList<>();

        profileAdapter = new EntrantsAdapter(profileList);
        // profileAdapter.setOnEventClickListener(this);  // 'this' refers to the ViewEvents activity
        entrantsRecyclerView.setAdapter(profileAdapter);

        // Set up Spinner
        statusSpinner = findViewById(R.id.statusSpinnerOrganizer);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.participation_statuses_organizer, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statusSpinner.setAdapter(adapter);

        statusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            /**
             * Called when an item is selected in the status spinner.
             * It sets the `selectedStatus` variable according to the selected position
             * and triggers the `retrieveEvents()` method to reload events based on the updated filter.
             *
             * @param parent The AdapterView where the selection was made.
             * @param view The view within the AdapterView that was clicked.
             * @param position The position of the item clicked in the spinner.
             * @param id The row ID of the item clicked.
             */
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        selectedStatus1 = -1; // All
                        selectedStatus2 = -2;
                        break;
                    case 1:
                        selectedStatus1 = 0;
                        selectedStatus2 = -2;
                        break;
                    case 2:
                        selectedStatus1 = 1;
                        selectedStatus2 = 2;
                        break;
                    case 3:
                        selectedStatus1 = 3;
                        selectedStatus2 = -2;
                        break;
                }
                retrieveEntrants(); // Reload events based on filter
            }

            /**
             * Called when no item is selected in the status spinner.
             * This method sets the `selectedStatus` to -1 (All) and triggers
             * the `retrieveEntrants()` method to reload all events without a filter.
             *
             * @param parent The AdapterView where no item was selected.
             */
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Default to All
                selectedStatus1 = -1;
                selectedStatus2 = -2;
                retrieveEntrants();
            }
        });
    }

    private void retrieveEntrants() {
        Log.d(TAG, "Retrieving devices for event: " + eventID);
        firestore.collection("events").document(eventID)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists() && document.contains("users")) {
                            Log.d(TAG, "Users found in Firestore document.");
                            Map<String, Long> usersMap = (Map<String, Long>) document.get("users");
                            profileList.clear(); // Clear current events
                            profileAdapter.notifyDataSetChanged(); // Update RecyclerView

                            for (Map.Entry<String, Long> entry : usersMap.entrySet()) {
                                String deviceId = entry.getKey();
                                participationStatus = entry.getValue();

                                Log.d(TAG, "Processing eventId: " + deviceId + ", Status: " + participationStatus);
                                Log.d(TAG, "participationStatus " + participationStatus);

                                if (selectedStatus1 == -1 || participationStatus == selectedStatus1 || participationStatus == selectedStatus2) {
                                    Log.d(TAG, "Device matches filter criteria. Fetching device details for: " + deviceId);
                                    firestore.collection("users").document(deviceId)
                                            .get()
                                            .addOnCompleteListener(entrantTask -> {
                                                if (entrantTask.isSuccessful()) {
                                                    DocumentSnapshot entrantDocument = entrantTask.getResult();
                                                    if (entrantDocument.exists()) {
                                                        String username = entrantDocument.getString("username");
                                                        if (username != null) {
                                                            Log.d(TAG, "User details retrieved: " + username);
                                                            profileList.add(username);
                                                            Log.d(TAG, "User added to the list: " + deviceId);
                                                        } else {
                                                            Log.w(TAG, "User document does not exist for ID: " + deviceId);
                                                        }
                                                    } else {
                                                        Log.w(TAG, "User document does not exist for ID: " + deviceId);
                                                    }
                                                } else {
                                                    Log.e(TAG, "Error fetching user details for ID: " + deviceId, entrantTask.getException());
                                                }
                                                profileAdapter.notifyDataSetChanged();
                                            });
                                } else {
                                    Log.d(TAG, "User does not match filter criteria. Skipping deviceId: " + deviceId);
                                }
                            }
                        } else {
                            Log.w(TAG, "No users found for Event ID: " + eventID);
                        }
                    } else {
                        Log.e(TAG, "Error retrieving users: ", task.getException());
                        Toast.makeText(ViewFinalEntrantsEventActivity.this, "Failed to retrieve users.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Firestore retrieval failed: ", e);
                    Toast.makeText(ViewFinalEntrantsEventActivity.this, "Firestore retrieval failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    /*
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
     */

    /**
     * Handles the selection of menu items, specifically the "home" button (up navigation).
     * This method is called when an item in the options menu is selected.
     *
     * @param item The menu item that was selected.
     * @return True if the menu item is handled, false otherwise.
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // Finish the current activity and return to the previous one
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
