package com.example.trojan0project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.provider.Settings;
import android.content.Context;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firestore
        FirebaseApp.initializeApp(this);
        db = FirebaseFirestore.getInstance();
        devicesRef = db.collection("users");

        // Reference UI elements
        logoText = findViewById(R.id.logoText);
        pickRoleText = findViewById(R.id.pickRoleText);
        userButton = findViewById(R.id.userButton);
        organizerButton = findViewById(R.id.organizerButton);

        // Check if the device ID exists in Firestore
        getDeviceIdAndCheck();
    }

    private void getDeviceIdAndCheck() {
        // Get the device ID
        String deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        // Check if the device ID exists in Firestore
        devicesRef.document(deviceId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Toast.makeText(this, "Device already registered!", Toast.LENGTH_SHORT).show();
                    // Device ID exists in Firestore
                    String userType = document.getString("user_type");
                    Log.d("TAG", "User type: " + userType);

                    if ("entrant".equals(userType)) {
                        Intent intent = new Intent(MainActivity.this, ViewProfile.class);
                        intent.putExtra("DEVICE_ID", deviceId);
                        startActivity(intent);
                    }

                    else if ("admin".equals(userType)) {
                        Intent intent = new Intent(MainActivity.this, EventActivity.class);
                        intent.putExtra("DEVICE_ID", deviceId);
                        startActivity(intent);
                        }

                } else {
                    // Device ID does not exist in Firestore
                    pickRoleText.setVisibility(View.VISIBLE); // Show the pickRoleText
                    userButton.setEnabled(true);               // Enable the user button
                    organizerButton.setEnabled(true);          // Enable the organizer button

                    // Set OnClickListener
                    userButton.setOnClickListener(v -> {
                        // Navigate to user sign-up page
                        Intent intent = new Intent(MainActivity.this, UserSignUpActivity.class);
                        intent.putExtra("DEVICE_ID", deviceId);
                        startActivity(intent);
                    });

                    organizerButton.setOnClickListener(v -> {
                        // Navigate to organizer sign-up page
                        startActivity(new Intent(MainActivity.this, OrganizerSignUpActivity.class));
                    });
                }
            } else {
                Toast.makeText(this, "Failed to connect to Firestore. Please check your internet connection.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
