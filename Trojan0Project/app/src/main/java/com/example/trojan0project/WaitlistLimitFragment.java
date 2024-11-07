package com.example.trojan0project;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;

public class WaitlistLimitFragment extends Fragment {

    private EditText limitEditText;
    private Button saveButton;
    private String eventId;
    private FirebaseFirestore db;

    public WaitlistLimitFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.waitlist_limit_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize Firestore instance
        db = FirebaseFirestore.getInstance();

        // Retrieve the event ID from the intent
        Intent intent = getActivity().getIntent();
        eventId = intent.getStringExtra("EVENT_ID");

        // Bind UI elements to the class fields
        limitEditText = view.findViewById(R.id.limitEditText);
        saveButton = view.findViewById(R.id.saveButton);

        // Set up the save button click listener
        saveButton.setOnClickListener(v -> saveLimit());
    }

    // Method to handle saving the limit value
    private void saveLimit() {
        String limitText = limitEditText.getText().toString();

        if (limitText.isEmpty()) {
            Toast.makeText(getActivity(), "Please enter a valid limit.", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int limit = Integer.parseInt(limitText);

            // Get a reference to the specific event document
            DocumentReference eventRef = db.collection("events").document(eventId);

            // Update the waitlist limit in the Firestore document
            eventRef.update("waitlistlimit", limit)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getActivity(), "Limit set to: " + limit, Toast.LENGTH_SHORT).show();
                        checkWaitlistLimit(eventRef, limit); // Check if waitlist is full
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getActivity(), "Failed to set limit. Try again.", Toast.LENGTH_SHORT).show();
                    });
        } catch (NumberFormatException e) {
            Toast.makeText(getActivity(), "Please enter a numeric value.", Toast.LENGTH_SHORT).show();
        }
    }

    // Method to check if the waitlisted count has reached the limit
    private void checkWaitlistLimit(DocumentReference eventRef, int limit) {
        eventRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<String> waitlisted = (List<String>) documentSnapshot.get("waitlisted");

                        if (waitlisted != null && waitlisted.size() >= limit) {
                            Toast.makeText(getActivity(), "Waitlist is full.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), "Waitlist has space available.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getActivity(), "Event not found.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getActivity(), "Failed to retrieve event data.", Toast.LENGTH_SHORT).show();
                });
    }
}

