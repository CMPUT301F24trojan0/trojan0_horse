/**
 * Purpose:
 * This activity checks whether the device is registered in Firestore and navigates
 * the user to the appropriate page based on their device id.
 * If not registered, the user can choose to sign up as either a user or organizer.
 * Controls the flow of the app.
 *
 * Design Rationale:
 * Firestore is used to store device-specific user data and to check if the device
 * is already associated with a user or admin or organizer role. Based on the result, the person using the app
 * is directed to either a page in the app specifc to their role or a sign-up page.
 *
 * Outstanding Issues:
 * No Issues.
 */
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
    private static final String TAG = "MainActivity";

    /**
     * Initializes the MainActivity, setting up Firestore and UI elements. Checks if the device ID is registered.
     *
     * @param savedInstanceState Saved instance state for restoring activity state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);

        // Initialize Firestore
        FirebaseApp.initializeApp(this);
        db = FirebaseFirestore.getInstance();
        devicesRef = db.collection("users");

        // Reference UI elements
        logoText = findViewById(R.id.logoText);

        // Check if the device ID exists in Firestore
        getDeviceIdAndCheck();
    }
    /**
     * Retrieves the device ID and checks if it exists in Firestore. Based on the result:
     * - Redirects the user if registered.
     * - Shows options to select a role if unregistered.
     */
    private void getDeviceIdAndCheck() {
        // Get the device ID
        String deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.d(TAG, "Generated device ID: " + deviceId);

        //sending to joinWaitlist for user device ids
        Intent intent1 = new Intent(MainActivity.this, JoinWaitlist.class);
        intent1.putExtra("DEVICE_ID", deviceId);


        // Check if the device ID exists in Firestore
        devicesRef.document(deviceId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Toast.makeText(this, "Device already registered!", Toast.LENGTH_SHORT).show();
                    // Device ID exists in Firestore
                    String userType = document.getString("user_type");
                    Log.d(TAG, "User type: " + userType);

                    if ("entrant".equals(userType)) {
                        Intent intent = new Intent(MainActivity.this, EntrantMain.class);
                        intent.putExtra("DEVICE_ID", deviceId);
                        startActivity(intent);
                        finish();
                    }

                    else if ("organizer".equals(userType)) {
                        // Directly navigate to OrganizerPageActivity if the user is an organizer
                        Intent intent = new Intent(MainActivity.this, OrganizerPageActivity.class);
                        intent.putExtra("organizerId", deviceId);  // Assuming deviceId is used as organizerId in Firestore
                        startActivity(intent);
                        finish();
                    }

                    else if ("admin".equals(userType)) {
                        Intent intent = new Intent(MainActivity.this, AdminMain.class);
                        intent.putExtra("DEVICE_ID", deviceId);
                        startActivity(intent);
                        finish();
                    }

                } else {
                    setContentView(R.layout.activity_main);

                    pickRoleText = findViewById(R.id.pickRoleText);
                    userButton = findViewById(R.id.userButton);
                    organizerButton = findViewById(R.id.organizerButton);

                    // Device ID does not exist in Firestore
                    pickRoleText.setVisibility(View.VISIBLE); // Show the pickRoleText
                    userButton.setEnabled(true);               // Enable the user button
                    organizerButton.setEnabled(true);          // Enable the organizer button

                    // Set OnClickListener
                    userButton.setOnClickListener(v -> {
                        // Navigate to user sign-up page
                        Intent intent = new Intent(MainActivity.this, UserSignUpActivity.class);
                        intent.putExtra("DEVICE_ID", deviceId);
                        intent.putExtra("USER_TYPE", "entrant");
                        startActivity(intent);
                        finish();
                    });

                    organizerButton.setOnClickListener(v -> {
                        devicesRef.document(deviceId).get().addOnSuccessListener(documentSnapshot -> {
                            String userType = documentSnapshot.getString("user_type");
                            if ("organizer".equals(userType)) {
                                // User is already registered as an organizer, navigate directly to OrganizerPageActivity
                                Intent intent = new Intent(MainActivity.this, OrganizerPageActivity.class);
                                intent.putExtra("organizerId", deviceId);
                                startActivity(intent);
                                finish();
                            } else {
                                // User is not an organizer, proceed to OrganizerSignUpActivity for registration
                                Intent intent = new Intent(MainActivity.this, OrganizerSignUpActivity.class);
                                intent.putExtra("DEVICE_ID", deviceId);
                                intent.putExtra("USER_TYPE", "organizer");
                                startActivity(intent);
                                finish();
                            }
                        }).addOnFailureListener(e -> Log.e(TAG, "Error checking user type: " + e.getMessage()));
                    });
                }
            } else {
                Toast.makeText(this, "Failed to connect to Firestore. Please check your internet connection.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
