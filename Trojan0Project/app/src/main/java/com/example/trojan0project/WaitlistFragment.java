/**
 * Purpose:
 * This activity allows the user to leave the waitlist by communicating with the in_waitlist.xml fragment
 *
 * Design Rationale:
 * The activity uses a onCreateView to obtain the eventID and the userID and to then remove them for the waitlist dictionaries of their respective collections
 *
 * Outstanding Issues:
 *
 */

package com.example.trojan0project;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

public class WaitlistFragment extends DialogFragment {

    private String deviceId;
    private String eventId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.in_waitlist, container, false);

        // Retrieve arguments
        if (getArguments() != null) {
            deviceId = getArguments().getString("DEVICE_ID");
            eventId = getArguments().getString("EVENT_ID");
        }

        // Leave waitlist button
        Button leaveWaitlistButton = view.findViewById(R.id.buttonLeaveWaitlist);
        leaveWaitlistButton.setOnClickListener(v -> leaveWaitlist());

        // Cancel button
        Button cancelButton = view.findViewById(R.id.buttonCancel);
        cancelButton.setOnClickListener(v -> dismiss());

        return view;
    }

    private void leaveWaitlist() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Remove the event ID from the user's events dictionary
        db.collection("users").document(deviceId)
                .update("events." + eventId, FieldValue.delete())
                .addOnSuccessListener(aVoid -> {
                    // Remove the user ID from the event's users dictionary
                    db.collection("events").document(eventId)
                            .update("users." + deviceId, FieldValue.delete())
                            .addOnSuccessListener(unused -> {
                                Toast.makeText(getContext(), "You have left the waitlist.", Toast.LENGTH_SHORT).show();
                                dismiss();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(getContext(), "Error updating event: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error updating user: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
