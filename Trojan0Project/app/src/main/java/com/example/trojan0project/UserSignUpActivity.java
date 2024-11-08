package com.example.trojan0project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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
    private String userType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_signup);

        db = FirebaseFirestore.getInstance();

        // Retrieve device ID and user type from intent
        Intent intent = getIntent();
        deviceId = intent.getStringExtra("DEVICE_ID");
        userType = intent.getStringExtra("USER_TYPE");

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

        Map<String, Object> userData = new HashMap<>();
        userData.put("username", username);
        userData.put("email", email);
        userData.put("user_type", userType);

        db.collection("users").document(deviceId).set(userData).addOnSuccessListener(aVoid -> {
            Log.d(TAG, "User data saved successfully");
            Toast.makeText(UserSignUpActivity.this, "User registered successfully!", Toast.LENGTH_SHORT).show();

            Intent profileIntent = new Intent(UserSignUpActivity.this, ViewProfile.class);
            profileIntent.putExtra("DEVICE_ID", deviceId);
            startActivity(profileIntent);
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Registration failed: " + e.getMessage());
            Toast.makeText(UserSignUpActivity.this, "Registration failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }
}
