/**
 * Purpose:
 * This activity allows users to sign up by entering their username and email. It then stores the user data
 * in Firestore under a document identified by the device ID.
 *
 * Design Rationale:
 * This activity uses Firestore to store user data, including the username, email, and user type .
 * The activity also handles navigation to a ViewProfile screen once the user is successfully registered.
 *
 * Outstanding Issues:
 * No Issues.
 */

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
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class UserSignUpActivity extends AppCompatActivity {
    private static final String TAG = "UserSignUpActivity";
    private EditText usernameEditText;
    private EditText emailEditText;
    private Button signUpButton;

    private FirebaseFirestore db;
    private String deviceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_signup); // Links the XML layout to this activity

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Retrieve the device ID from the intent
        Intent intent = getIntent();
        deviceId = intent.getStringExtra("DEVICE_ID");

        usernameEditText = findViewById(R.id.username);
        emailEditText = findViewById(R.id.email);
        signUpButton = findViewById(R.id.signUpButton);

        signUpButton.setOnClickListener(v -> saveUserData());
    }

    private void saveUserData() {
        String username = usernameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();

        if (username.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d(TAG, "Attempting to save user data");
        Log.d(TAG, "Username: " + username + ", Email: " + email + ", Device ID: " + deviceId);

        // Create a Map to store user data
        Map<String, String> userData = new HashMap<>();
        userData.put("username", username);
        userData.put("email", email);
        userData.put("user_type", "entrant"); // Add a field indicating user type

        db.collection("users").document(deviceId).set(userData).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("firestore", "User data saved successfully: " + userData);
                        Toast.makeText(UserSignUpActivity.this, "User registered successfully!", Toast.LENGTH_SHORT).show();

                        // Navigate to ViewProfile page
                        Intent intent = new Intent(UserSignUpActivity.this, ViewProfile.class);
                        intent.putExtra("DEVICE_ID", deviceId); // Pass the deviceId if needed in ViewProfile
                        startActivity(intent);
                    } else {
                        Log.e("firestore", "Registration failed: " + task.getException().getMessage());
                        Toast.makeText(UserSignUpActivity.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
        });

    }
}
