package com.example.trojan0project;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ViewProfile extends AppCompatActivity {
    private static final String TAG = "ViewProfile";
    private EditText usernameEditText;
    private EditText emailEditText;
    private Button signUpButton;

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

        // Load profile data
        loadProfileData();

    }

    private void loadProfileData() {
        db.collection("users").document(deviceId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    // Populate fields with data
                    // For example, you could set values in EditText fields here
                    Log.d(TAG, "Profile data: " + document.getData());
                } else {
                    Toast.makeText(this, "Profile not found", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.e(TAG, "Error loading profile", task.getException());
                Toast.makeText(this, "Failed to load profile", Toast.LENGTH_SHORT).show();
            }
        });
    }
}