package com.example.trojan0project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class OrganizerSignUpActivity extends AppCompatActivity {

    private EditText facilityInput;
    private Button signUpButton;
    /**
     * Initializes the activity and sets up UI elements and event listeners.
     *
     * @param savedInstanceState The saved state of the activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer_signup);

        facilityInput = findViewById(R.id.facility_input);
        signUpButton = findViewById(R.id.signup_button);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String facility = facilityInput.getText().toString().trim();

                if (!facility.isEmpty()) {
                    // Create an Organizer object with an empty list of events
                    Organizer organizer = new Organizer(facility, new ArrayList<Event>());

                    // Get Firestore instance and generate a unique document ID
                    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                    String organizerId = firestore.collection("organizers").document().getId();

                    // Save the Organizer to Firestore
                    firestore.collection("organizers").document(organizerId).set(organizer)
                            .addOnSuccessListener(aVoid -> {
                                // Pass the organizer ID to OrganizerPageActivity
                                Intent intent = new Intent(OrganizerSignUpActivity.this, OrganizerPageActivity.class);
                                intent.putExtra("organizerId", organizerId);
                                startActivity(intent);
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(OrganizerSignUpActivity.this, "Failed to save organizer", Toast.LENGTH_SHORT).show();
                                e.printStackTrace();  // Print error details in Logcat
                            });
                } else {
                    Toast.makeText(OrganizerSignUpActivity.this, "Please enter facility name", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
