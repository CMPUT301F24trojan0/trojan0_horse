package com.example.trojan0project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.trojan0project.R;

public class OrganizerSignUpActivity extends AppCompatActivity {

    private EditText facilityInput;
    private Button signUpButton;

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
                    // Save the facility data here (you can use SharedPreferences, SQLite, or Firestore)

                    // Navigate to OrganizerPageActivity
                    Intent intent = new Intent(OrganizerSignUpActivity.this, OrganizerPageActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(OrganizerSignUpActivity.this, "Please enter facility name", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
