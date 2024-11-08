package com.example.trojan0project;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private CollectionReference devicesRef;
    private TextView logoText;
    private TextView pickRoleText;
    private Button userButton;
    private Button organizerButton;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseApp.initializeApp(this);
        db = FirebaseFirestore.getInstance();
        devicesRef = db.collection("users");

        logoText = findViewById(R.id.logoText);
        pickRoleText = findViewById(R.id.pickRoleText);
        userButton = findViewById(R.id.userButton);
        organizerButton = findViewById(R.id.organizerButton);

        getDeviceIdAndCheck();
    }

    private void getDeviceIdAndCheck() {
        String deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.d(TAG, "Generated device ID: " + deviceId);

        devicesRef.document(deviceId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    String userType = document.getString("user_type");
                    Log.d(TAG, "User type: " + userType);

                    if ("organizer".equals(userType)) {
                        // Directly navigate to OrganizerPageActivity if the user is an organizer
                        Intent intent = new Intent(MainActivity.this, OrganizerPageActivity.class);
                        intent.putExtra("organizerId", deviceId);  // Assuming deviceId is used as organizerId in Firestore
                        startActivity(intent);
                    } else {
                        setupRoleSelectionButtons(deviceId);  // Entrant user type or not registered
                    }
                } else {
                    setupRoleSelectionButtons(deviceId);  // User does not exist in Firestore
                }
            } else {
                Toast.makeText(this, "Failed to connect to Firestore. Please check your internet connection.", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Firestore connection failed: " + task.getException());
            }
        });
    }

    private void setupRoleSelectionButtons(String deviceId) {
        pickRoleText.setVisibility(View.VISIBLE);
        userButton.setEnabled(true);
        organizerButton.setEnabled(true);

        userButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, UserSignUpActivity.class);
            intent.putExtra("DEVICE_ID", deviceId);
            intent.putExtra("USER_TYPE", "entrant");
            startActivity(intent);
        });

        organizerButton.setOnClickListener(v -> {
            devicesRef.document(deviceId).get().addOnSuccessListener(documentSnapshot -> {
                String userType = documentSnapshot.getString("user_type");
                if ("organizer".equals(userType)) {
                    // User is already registered as an organizer, navigate directly to OrganizerPageActivity
                    Intent intent = new Intent(MainActivity.this, OrganizerPageActivity.class);
                    intent.putExtra("organizerId", deviceId);
                    startActivity(intent);
                } else {
                    // User is not an organizer, proceed to OrganizerSignUpActivity for registration
                    Intent intent = new Intent(MainActivity.this, OrganizerSignUpActivity.class);
                    intent.putExtra("DEVICE_ID", deviceId);
                    intent.putExtra("USER_TYPE", "organizer");
                    startActivity(intent);
                }
            }).addOnFailureListener(e -> Log.e(TAG, "Error checking user type: " + e.getMessage()));
        });
    }
}
