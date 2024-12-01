package com.example.trojan0project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONObject;

public class ManualEntryActivity extends AppCompatActivity {
    private static final String TAG = "ManualEntryActivity";
    private FirebaseFirestore db;
    private EditText manualInputField;
    private Button submitButton, cancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_entry);

        // Initialize Firestore and views
        db = FirebaseFirestore.getInstance();
        manualInputField = findViewById(R.id.manualInputField);
        submitButton = findViewById(R.id.submitButton);
        cancelButton = findViewById(R.id.cancelButton);

        // Submit button functionality
        submitButton.setOnClickListener(v -> {
            String inputData = manualInputField.getText().toString().trim();
            if (!inputData.isEmpty()) {
                handleManualInput(inputData);
            } else {
                Toast.makeText(this, "Please enter event data.", Toast.LENGTH_SHORT).show();
            }
        });

        // Cancel button functionality
        cancelButton.setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });
    }

    private void handleManualInput(String inputData) {
        try {
            // Parse JSON data from manual input
            JSONObject jsonObject = new JSONObject(inputData);
            String eventId = jsonObject.getString("id");
            Log.d(TAG, "handleManualInput: Parsed eventId = " + eventId);

            // Query Firebase to fetch the event data
            db.collection("events").document(eventId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            Log.d(TAG, "Event found in database: " + documentSnapshot.getData());

                            // Pass data to EventDetailsActivity
                            Intent intent = new Intent(ManualEntryActivity.this, EventDetailsActivity.class);
                            intent.putExtra("eventName", documentSnapshot.getString("eventName"));
                            intent.putExtra("description", documentSnapshot.getString("description"));
                            intent.putExtra("latitude", documentSnapshot.getDouble("latitude"));
                            intent.putExtra("longitude", documentSnapshot.getDouble("longitude"));
                            intent.putExtra("posterPath", documentSnapshot.getString("posterPath"));
                            intent.putExtra("time", documentSnapshot.getString("time"));
                            intent.putExtra("deadline", documentSnapshot.getTimestamp("deadline").toDate().getTime()); // Pass as long
                            intent.putExtra("maxNumberOfEntrants", documentSnapshot.getLong("maxNumberOfEntrants").intValue());

                            startActivity(intent);

                            // Finish ManualEntryActivity
                            finish();
                        } else {
                            Log.d(TAG, "handleManualInput: Event not found.");
                            Toast.makeText(ManualEntryActivity.this, "Event not found in database.", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "handleManualInput: Error fetching event: ", e);
                        Toast.makeText(ManualEntryActivity.this, "Error fetching event.", Toast.LENGTH_SHORT).show();
                    });
        } catch (Exception e) {
            Log.e(TAG, "handleManualInput: Invalid format: ", e);
            Toast.makeText(this, "Invalid input format.", Toast.LENGTH_SHORT).show();
        }
    }
}
