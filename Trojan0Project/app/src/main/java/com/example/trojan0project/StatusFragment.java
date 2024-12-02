/**
 * Purpose:
 * This fragment displays a dialog where the user can accept or decline an event.
 * The event's status is updated in Firebase Firestore based on the user's action.
 * When the "Accept" button is clicked, the event status is updated to 2. When the "Decline" button is clicked, the event status is updated to 3.
 *
 * Design Rationale:
 * The fragment uses a custom layout to display two buttons: "Accept" and "Decline".
 * When buttons are clicked the event's status is updated in the Firestore database, and  feedback is shown to the user.
 *
 * Outstanding Issues:
 * No Issues.
 */

package com.example.trojan0project;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class StatusFragment extends DialogFragment {

    private static final String TAG = "StatusFragment";
    private String deviceId;
    private String eventId;

    private TextView messageTextView;
    private Button buttonAccept;
    private Button buttonDecline;
    private FirebaseFirestore db;
    /**
     * Creates the dialog for accepting or declining an event invitation.
     *
     * @param savedInstanceState If the fragment is being re-constructed from a previous saved state.
     * @return The created dialog with the event invitation options.
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // Retrieve the device ID from the arguments
        deviceId = getArguments() != null ? getArguments().getString("DEVICE_ID") : null;

        // Retrieve the event ID from the arguments
        eventId = getArguments() != null ? getArguments().getString("EVENT_ID") : null;

        if (deviceId == null || eventId == null) {
            Log.e(TAG, "Correct Details not received in StatusFragment");
            Toast.makeText(getActivity(), "Error: Missing IDs", Toast.LENGTH_SHORT).show();
            dismiss();
            return super.onCreateDialog(savedInstanceState);
        }

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Create the Dialog using the custom layout
        Context context = getActivity();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.fragment_status, null);

        // Initialize the views
        messageTextView = view.findViewById(R.id.messageTextView);
        buttonAccept = view.findViewById(R.id.buttonAccept);
        buttonDecline = view.findViewById(R.id.buttonDecline);

        // Set button click listeners
        buttonAccept.setOnClickListener(v -> {
            // Handle the Accept button click
            acceptEvent(deviceId, eventId);
        });

        buttonDecline.setOnClickListener(v -> {
            // Handle the Decline button click
            declineEvent(deviceId, eventId);
        });

        // Create and return the dialog
        Dialog dialog = new Dialog(context);
        dialog.setContentView(view);
        dialog.setCancelable(true);

        return dialog;
    }
    /**
     * Updates the event status to accepted for the user in Firestore.
     *
     * @param deviceId The ID of the device associated with the user.
     * @param eventId  The ID of the event to be accepted.
     */
    private void acceptEvent(String deviceId, String eventId) {
        // Reference to the user document
        db.collection("users").document(deviceId)
                .update("events." + eventId, 2) // Update the event status to 2
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Event status updated successfully.");
                    Toast.makeText(getActivity(), "Event status updated successfully.", Toast.LENGTH_SHORT).show();

                    // Update the num_sampled field in the events collection
                    db.collection("events").document(eventId)
                            .update("num_sampled", FieldValue.increment(1)) // Increment num_sampled by 1
                            .addOnSuccessListener(aVoid2 -> {
                                Log.d(TAG, "num_sampled incremented successfully.");
                                Toast.makeText(getActivity(), "Event num_sampled incremented.", Toast.LENGTH_SHORT).show();

                                // Update the users map in the events collection
                                db.collection("events").document(eventId)
                                        .update("users." + deviceId, 2) // Set the user's status in the map to 2
                                        .addOnSuccessListener(aVoid3 -> {
                                            Log.d(TAG, "Users map updated successfully.");
                                            Toast.makeText(getActivity(), "Event users map updated.", Toast.LENGTH_SHORT).show();
                                        })
                                        .addOnFailureListener(e -> {
                                            Log.e(TAG, "Error updating users map: ", e);
                                            Toast.makeText(getActivity(), "Failed to update users map: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        });

                            })
                            .addOnFailureListener(e -> {
                                Log.e(TAG, "Error incrementing num_sampled: ", e);
                                Toast.makeText(getActivity(), "Failed to increment num_sampled: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });

                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error updating event status: ", e);
                    Toast.makeText(getActivity(), "Failed to update event status: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * Updates the event status to declined for the user in Firestore.
     *
     * @param deviceId The ID of the device associated with the user.
     * @param eventId  The ID of the event to be declined.
     */
    private void declineEvent(String deviceId, String eventId) {
        // Update the user's event status in the users collection
        db.collection("users").document(deviceId)
                .update("events." + eventId, 3) // Update the event status
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Event status updated successfully.");
                    Toast.makeText(getActivity(), "Event status updated successfully.", Toast.LENGTH_SHORT).show();

                    // Update the num_sampled field in the events collection
                    db.collection("events").document(eventId)
                            .update("num_sampled", FieldValue.increment(-1)) // Decrement num_sampled by 1
                            .addOnSuccessListener(aVoid2 -> {
                                Log.d(TAG, "num_sampled decremented successfully.");
                                Toast.makeText(getActivity(), "Event num_sampled decremented.", Toast.LENGTH_SHORT).show();

                                // Update the users map in the events collection
                                db.collection("events").document(eventId)
                                        .update("users." + deviceId, 3) // Set the user's status in the map to 3
                                        .addOnSuccessListener(aVoid3 -> {
                                            Log.d(TAG, "Users map updated successfully.");
                                            Toast.makeText(getActivity(), "Event users map updated.", Toast.LENGTH_SHORT).show();
                                        })
                                        .addOnFailureListener(e -> {
                                            Log.e(TAG, "Error updating users map: ", e);
                                            Toast.makeText(getActivity(), "Failed to update users map: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        });

                            })
                            .addOnFailureListener(e -> {
                                Log.e(TAG, "Error decrementing num_sampled: ", e);
                                Toast.makeText(getActivity(), "Failed to decrement num_sampled: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });

                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error updating event status: ", e);
                    Toast.makeText(getActivity(), "Failed to update event status: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}