package com.example.trojan0project;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class ViewProfile extends AppCompatActivity {
    private static final String TAG = "ViewProfile";
    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText usernameEditText;
    private EditText emailEditText;
    private EditText phoneNumberEditText;
    private Switch notificationsToggle;
    private Button viewEventsButton;

    private FirebaseFirestore db;
    private String deviceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_view); // Links the XML layout to this activity

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Retrieve the device ID from the intent
        Intent intent = getIntent();
        deviceId = intent.getStringExtra("DEVICE_ID");

        // Reference UI elements
        firstNameEditText = findViewById(R.id.firstNameInput);
        lastNameEditText = findViewById(R.id.lastNameInput);
        usernameEditText = findViewById(R.id.usernameInput);
        emailEditText = findViewById(R.id.emailInput);
        phoneNumberEditText = findViewById(R.id.phoneNumberInput);
        notificationsToggle = findViewById(R.id.notificationsToggle);
        viewEventsButton = findViewById(R.id.viewEventsButton);

        // Load profile data
        loadProfileData();

        // Save details and go to View Events Page
        viewEventsButton.setOnClickListener(v -> saveProfileData());

    }

    private void loadProfileData() {
        db.collection("users").document(deviceId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    // Populate fields with existing data
                    firstNameEditText.setText(document.getString("first_name"));
                    lastNameEditText.setText(document.getString("last_name"));
                    usernameEditText.setText(document.getString("username"));
                    emailEditText.setText(document.getString("email"));
                    phoneNumberEditText.setText(document.getString("phone_number"));

                    Boolean notifications = document.getBoolean("notifications");
                    if (notifications != null) {
                        notificationsToggle.setChecked(notifications);
                    }

                    Log.d(TAG, "Profile data loaded: " + document.getData());
                } else {
                    Toast.makeText(this, "Profile not found", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.e(TAG, "Error loading profile", task.getException());
                Toast.makeText(this, "Failed to load profile", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveProfileData() {
        String firstName = firstNameEditText.getText().toString().trim();
        String lastName = lastNameEditText.getText().toString().trim();
        String username = usernameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String phoneNumber = phoneNumberEditText.getText().toString().trim();
        boolean notifications = notificationsToggle.isChecked();

        if (firstName.isEmpty() || lastName.isEmpty() || phoneNumber.isEmpty()) {
            Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show();
            // Exit the method to stay on the same page
            return;
        }

        // Create a Map to store updated profile data
        Map<String, Object> profileData = new HashMap<>();
        profileData.put("first_name", firstName);
        profileData.put("last_name", lastName);
        profileData.put("username", username);
        profileData.put("email", email);
        profileData.put("phone_number", phoneNumber);
        profileData.put("notifications", notifications);

        // Update Firestore database
        db.collection("users").document(deviceId).set(profileData, SetOptions.merge())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Profile updated successfully: " + profileData);
                        Toast.makeText(ViewProfile.this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();


                        // Prevent multiple clicks
                        viewEventsButton.setEnabled(false);
                        // Navigate to View Events Page
                        Intent intent1 = new Intent(ViewProfile.this, ViewEvents.class);
                        intent1.putExtra("DEVICE_ID", deviceId); // Add device ID to intent
                        startActivity(intent1);
                    } else {
                        Log.e(TAG, "Profile update failed", task.getException());
                        Toast.makeText(ViewProfile.this, "Profile update failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}