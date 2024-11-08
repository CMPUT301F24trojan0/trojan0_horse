/**
 * Purpose:
 * This activity handles the organizer sign-up process, allowing a user to create an organizer profile
 * by entering a facility name.
 *
 * Design Rationale:
 * This uses Firestore for storing the organizer details and transitions to the OrganizerPageActivity
 * after saving the organizer data.
 *
 * Outstanding Issues:
 *No Issues.
 */

package com.example.trojan0project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OrganizerSignUpActivity extends AppCompatActivity {

    private EditText facilityInput;
    private Button signUpButton;
    private FirebaseFirestore db;
    private String deviceId;
    private static final String TAG = "OrganizerSignUpActivity";

    /**
     * Initializes the activity and sets up UI elements and event listeners.
     *
     * @param savedInstanceState The saved state of the activity.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer_signup);

        db = FirebaseFirestore.getInstance();

        Intent intent = getIntent();
        deviceId = intent.getStringExtra("DEVICE_ID");

        facilityInput = findViewById(R.id.facility_input);
        signUpButton = findViewById(R.id.signup_button);

        signUpButton.setOnClickListener(v -> saveOrganizerData());
    }

    private void saveOrganizerData() {
        String facilityName = facilityInput.getText().toString().trim();

        if (facilityName.isEmpty()) {
            Toast.makeText(this, "Please enter facility name", Toast.LENGTH_SHORT).show();
            return;
        }

        // Prepare user data for Firestore with facilityName as a top-level field
        Map<String, Object> userData = new HashMap<>();
        userData.put("user_type", "organizer");
        userData.put("facilityName", facilityName); // Use "facilityName" as the key
        userData.put("events", new ArrayList<>()); // Empty list of events initially

        db.collection("users").document(deviceId).set(userData).addOnSuccessListener(aVoid -> {
            Log.d(TAG, "Organizer data saved successfully");
            Toast.makeText(this, "Organizer registered successfully!", Toast.LENGTH_SHORT).show();

            // Navigate to OrganizerPageActivity after successful registration
            Intent intent = new Intent(OrganizerSignUpActivity.this, OrganizerPageActivity.class);
            intent.putExtra("organizerId", deviceId);
            startActivity(intent);
            finish();
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Failed to save organizer: " + e.getMessage());
            Toast.makeText(this, "Failed to save organizer", Toast.LENGTH_SHORT).show();
        });
    }
}
