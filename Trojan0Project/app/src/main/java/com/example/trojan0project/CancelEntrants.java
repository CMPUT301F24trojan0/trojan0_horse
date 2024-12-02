/**
 * CancelEntrants class handles cancelling entrants for a specific event who did not sign up by
 * the deadline. Entrants are marked as 3 which is decline or cancelled in the Firestore database
 *
 * Purpose:
 * Class allows the event organizer to automatically cancel all entrants by clicking a button who:
 * were invited to sign up (status 1)
 * did not complete the sign up process before the deadline
 *
 * Design Rationale:
 * Gets signup deadline for the target event from Firestore
 * Prompts the organizer to confirm the cancellation with a dialog
 * Updates status of eligible entrants in Firestore to 3 (decline and cancelled)
 *
 * Outstanding Issues:
 * No issues
 *
 */
package com.example.trojan0project;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CancelEntrants extends AppCompatActivity {
    private FirebaseFirestore db;
    private String targetEventId = "Tm6SgOQNJgwcy79chggL";
    private Date signupDeadline;

    /**
     * Initializes activity and triggeres the process to get the deadline
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cancel_entrants);
        db = FirebaseFirestore.getInstance();

        Button cancelEntrantsButton = findViewById(R.id.cancelEntrantsButton);
        cancelEntrantsButton.setOnClickListener(v -> {
            Log.d("CancelEntrants", "Cancel Entrants button clicked");
            getDeadline(); // Fetch the deadline and then proceed with cancellation logic
        });
        //getDeadline();

    }

    /**
     * Gets deadline for the target event from Firestore
     * Triggers the confirmation dialog to cancel entrants
     */
    private void getDeadline(){
        db.collection("events").document(targetEventId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()){
                        Timestamp deadlineTimestamp = documentSnapshot.getTimestamp("deadline");
                        Log.d("CancelEntrants", "Fetched deadline: " + deadlineTimestamp);
                        if (deadlineTimestamp != null){
                            signupDeadline = deadlineTimestamp.toDate();
                            Log.d("CancelEntrants", "Fetched signup deadline: " + signupDeadline);

                            cancelEntrantsConfirm();
                        } else {
                            Log.e("CancelEntrants", "Deadline field is missing in event: " + targetEventId);
                        }
                    }else {
                        Log.e("CancelEntrants", "Event not found for ID: " + targetEventId);
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
        db.collection("users")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    WriteBatch batch = db.batch();

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String userType = document.getString("user_type");
                        if ("entrant".equals(userType)) {
                            Map<String, Long> events = (Map<String, Long>) document.get("events");
                            if (events != null) {
                                for (Map.Entry<String, Long> entry : events.entrySet()) {
                                    String eventId = entry.getKey();
                                    Long status = entry.getValue();

                                    if (status == 1 && eventId.equals(targetEventId)) {
                                        if (new Date().after(signupDeadline)) {
                                            events.put(eventId, Long.valueOf(3));
                                            batch.update(document.getReference(), "events", events);

                                            DocumentReference eventRef = db.collection("events").document(targetEventId);
                                            Map<String, Object> userFieldUpdate = new HashMap<>();
                                            userFieldUpdate.put(document.getId(), 3);
                                            batch.update(eventRef, userFieldUpdate);

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
                                    Log.d("CancelEntrants", "Successfully cancelled entrants for event: " + targetEventId))
                            .addOnFailureListener(e ->
                                    Log.e("CancelEntrants", "Failed to cancel entrants: " + e.getMessage()));
                })
                .addOnFailureListener(e ->
                        Log.e("CancelEntrants", "Error fetching users: " + e.getMessage()));
    }
}
