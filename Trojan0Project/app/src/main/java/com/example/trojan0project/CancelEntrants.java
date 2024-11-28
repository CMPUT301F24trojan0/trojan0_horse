package com.example.trojan0project;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.Date;
import java.util.Map;

public class CancelEntrants extends AppCompatActivity {
    private FirebaseFirestore db;
    private String targetEventId = "g7MK9lR8W8HwesTVgmdU";
    private Date signupDeadline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cancel_entrants);
        db = FirebaseFirestore.getInstance();

        signupDeadline = new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000);

        cancelEntrantsConfirm();
    }

    private void cancelEntrantsConfirm() {
        new AlertDialog.Builder(this)
                .setTitle("Cancel Entrants")
                .setMessage("Are you sure you want to cancel all entrants who haven't signed up?")
                .setPositiveButton("Yes", (dialog, which) -> cancelEntrants())
                .setNegativeButton("No", null)
                .show();
    }

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
                                            events.put(eventId, Long.valueOf(3)); // Update to status 3 (cancelled)
                                            batch.update(document.getReference(), "events", events);


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
