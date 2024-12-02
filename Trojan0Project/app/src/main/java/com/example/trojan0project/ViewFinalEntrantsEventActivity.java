package com.example.trojan0project;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

/**
 * Purpose:
 * The `ViewFinalEntrantsEventActivity` class provides functionality to display the final list of entrants for a specific event.
 * It fetches data from Firebase Firestore, filters users based on their status for the event, and presents the results in a RecyclerView.
 *
 * Design Rationale:
 * - Ensures efficient filtering of users by combining their user type and event-specific status.
 * - Uses a RecyclerView for scalable and efficient display of the list of entrants.
 * - Provides real-time data fetching from Firestore to ensure up-to-date entrant information.
 *
 * Outstanding Issues:
 * - No known issues at this time.
 */

public class ViewFinalEntrantsEventActivity extends AppCompatActivity implements NotificationDialogFragment.NotificationDialogListener {

    private static final String TAG = "ViewFinalEntrantsEventActivity";
    private RecyclerView entrantsRecyclerView;
    private EntrantsAdapter profileAdapter;
    private ArrayList<String> profileList;
    private ArrayList<String> deviceIDList;
    private FirebaseFirestore firestore;
    private String eventID;
    private Spinner statusSpinner;
    private int selectedStatus1 = -1;
    private Long participationStatus;
    private Date signupDeadline;
    private Button sendNotification;

    @Override
    public void onSendNotification(String message) {
        Log.d(TAG, "Notification message received: " + message);

        // Instantiate the Notification class
        Notification notificationManager = new Notification();

        // Fetch the event name using the eventID
        firestore.collection("events").document(eventID)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists() && document.contains("eventName")) {
                            String eventName = document.getString("eventName");
                            Log.d(TAG, "Event Name retrieved: " + eventName);

                            // Iterate through the deviceIDlist and send notifications
                            for (int i = 0; i < deviceIDList.size(); i++) {
                                String deviceId = deviceIDList.get(i); // Assuming deviceIDlist matches profileList indices
                                Log.d(TAG, "Sending notification to Device ID: " + deviceId
                                        + ", Event ID: " + eventID
                                        + ", Event Name: " + eventName);

                                // Use addNotificationToDevice to send notifications
                                notificationManager.addNotificationToDevice(
                                        deviceId, // Device ID
                                        eventID,  // Event ID
                                        "Notification for Event: " + eventName, // Notification title
                                        message   // Notification message
                                );
                            }

                            // Handle the notification message here (e.g., send to Firestore or display a Toast)
                            Toast.makeText(this, "Notification Sent: " + message + " for Event: " + eventName, Toast.LENGTH_SHORT).show();
                        } else {
                            Log.w(TAG, "Event Name not found for Event ID: " + eventID);
                            Toast.makeText(this, "Event Name not found.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e(TAG, "Error retrieving Event Name for Event ID: " + eventID, task.getException());
                        Toast.makeText(this, "Error retrieving Event Name.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Firestore retrieval failed: ", e);
                    Toast.makeText(this, "Firestore retrieval failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });

        // Handle the notification message here (e.g., send to Firestore or display a Toast)
        Toast.makeText(this, "Notification Sent: " + message, Toast.LENGTH_SHORT).show();
    }

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
        deviceIDList = new ArrayList<>();

        profileAdapter = new EntrantsAdapter(profileList);
        entrantsRecyclerView.setAdapter(profileAdapter);

        Button cancelEntrantsButton = findViewById(R.id.cancelEntrantsButton);
        cancelEntrantsButton.setOnClickListener(v -> {
            Log.d("CancelEntrants", "Cancel Entrants button clicked");
            getDeadline(); // Fetch the deadline and then proceed with cancellation logic
        });

        Button sampleWaitlistButton = findViewById(R.id.sampleWaitlistButton);

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
             * @param parent   The AdapterView where the selection was made.
             * @param view     The view within the AdapterView that was clicked.
             * @param position The position of the item clicked in the spinner.
             * @param id       The row ID of the item clicked.
             */
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        selectedStatus1 = -1; // All
                        break;
                    case 1:
                        selectedStatus1 = 0;
                        break;
                    case 2:
                        selectedStatus1 = 1;
                        break;
                    case 3:
                        selectedStatus1 = 2;
                        break;
                    case 4:
                        selectedStatus1 = 3;
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
                retrieveEntrants();
            }
        });

        sendNotification = findViewById(R.id.sendNotificationButton); // Ensure ID matches your layout

        sendNotification.setOnClickListener(v -> {
            NotificationDialogFragment dialogFragment = new NotificationDialogFragment();
            dialogFragment.show(getSupportFragmentManager(), "NotificationDialog");
        });

        sampleWaitlistButton.setOnClickListener(v -> {
            Intent systemIntent = new Intent(ViewFinalEntrantsEventActivity.this, SystemSample.class);
            systemIntent.putExtra("eventId", eventID);
            startActivity(systemIntent);
        });
    }

    /**
     * Gets deadline for the target event from Firestore
     * Triggers the confirmation dialog to cancel entrants
     */
    private void getDeadline(){
        firestore.collection("events").document(eventID).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()){
                        Timestamp deadlineTimestamp = documentSnapshot.getTimestamp("deadline");
                        Log.d("CancelEntrants", "Fetched deadline: " + deadlineTimestamp);
                        if (deadlineTimestamp != null){
                            signupDeadline = deadlineTimestamp.toDate();
                            Log.d("CancelEntrants", "Fetched signup deadline: " + signupDeadline);

                            cancelEntrantsConfirm();
                        } else {
                            Log.e("CancelEntrants", "Deadline field is missing in event: " + eventID);
                        }
                    }else {
                        Log.e("CancelEntrants", "Event not found for ID: " + eventID);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("CancelEntrants", "Error fetching event: " + e.getMessage());
                });
    }

    /**
     * Displays confirmation dialog asking the user if they want to cancel all entrants
     */
    private void cancelEntrantsConfirm() {
        new AlertDialog.Builder(this)
                .setTitle("Cancel Entrants")
                .setMessage("Are you sure you want to cancel all entrants who haven't signed up?")
                .setPositiveButton("Yes", (dialog, which) -> cancelEntrants())
                .setNegativeButton("No", null)
                .show();
    }

    /**
     * Cancels all entrants for target event who meet the conditions
     *          - if their status is 1 (meaning invited to apply)
     *          - the current date is after the signup deadline
     * Performs batch update to ensure that everything gets cancelled together
     */
    //https://firebase.google.com/docs/firestore/manage-data/transactions, 2024-11-27
    private void cancelEntrants() {
        firestore.collection("users")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    WriteBatch batch = firestore.batch();

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String userType = document.getString("user_type");
                        if ("entrant".equals(userType)) {
                            Map<String, Long> events = (Map<String, Long>) document.get("events");
                            if (events != null) {
                                for (Map.Entry<String, Long> entry : events.entrySet()) {
                                    String eventId = entry.getKey();
                                    Long status = entry.getValue();

                                    if (status == 1 && eventId.equals(eventID)) {
                                        if (new Date().after(signupDeadline)) {
                                            events.put(eventId, Long.valueOf(3));
                                            batch.update(document.getReference(), "events", events);

                                            // Update the event document to reflect the change in the user's status
                                            DocumentReference eventRef = firestore.collection("events").document(eventID);
                                            Map<String, Object> userFieldUpdate = new HashMap<>();
                                            userFieldUpdate.put(document.getId(), 3); // Set the user's status to 3 in the event map
                                            batch.update(eventRef, "users", userFieldUpdate);

                                            Log.d("CancelEntrants", "Cancelled entrant for event: " + eventId
                                                    + ", User: " + document.getId());
                                        }
                                    }
                                }
                            }
                        }
                    }


                    batch.commit()
                            .addOnSuccessListener(aVoid ->
                                    Log.d("CancelEntrants", "Successfully cancelled entrants for event: " + eventID))
                            .addOnFailureListener(e ->
                                    Log.e("CancelEntrants", "Failed to cancel entrants: " + e.getMessage()));
                })
                .addOnFailureListener(e ->
                        Log.e("CancelEntrants", "Error fetching users: " + e.getMessage()));
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
                            deviceIDList.clear();
                            profileAdapter.notifyDataSetChanged(); // Update RecyclerView

                            for (Map.Entry<String, Long> entry : usersMap.entrySet()) {
                                String deviceId = entry.getKey();
                                participationStatus = entry.getValue();

                                Log.d(TAG, "Processing eventId: " + deviceId + ", Status: " + participationStatus);
                                Log.d(TAG, "participationStatus " + participationStatus);

                                if (selectedStatus1 == -1 || participationStatus == selectedStatus1) {
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
                                                            deviceIDList.add(deviceId);
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
