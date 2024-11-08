package com.example.trojan0project;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.firestore.FirebaseFirestore;

public class LeaveWaitlistFragment extends DialogFragment {

    private static final String TAG = "LeaveWaitlistFragment";
    private String deviceId;
    private String eventId;
    private FirebaseFirestore db;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        deviceId = getArguments() != null ? getArguments().getString("DEVICE_ID") : null;
        eventId = getArguments() != null ? getArguments().getString("EVENT_ID") : null;

        if (deviceId == null || eventId == null) {
            Log.e(TAG, "Missing IDs in LeaveWaitlistFragment");
            Toast.makeText(getActivity(), "Error: Missing IDs", Toast.LENGTH_SHORT).show();
            dismiss();
            return super.onCreateDialog(savedInstanceState);
        }

        db = FirebaseFirestore.getInstance();

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.leave_waitlist_fragment, null);
        Button buttonLeave = view.findViewById(R.id.buttonLeave);
        Button buttonCancel = view.findViewById(R.id.buttonCancel);

        buttonLeave.setOnClickListener(v -> leaveWaitlist());
        buttonCancel.setOnClickListener(v -> dismiss());

        Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(view);
        dialog.setCancelable(true);

        return dialog;
    }

    private void leaveWaitlist() {
        db.collection("users").document(deviceId)
                .update("events." + eventId, null)  // Remove the event from the user's list
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Left the waitlist successfully.");
                    Toast.makeText(getActivity(), "You have left the waitlist.", Toast.LENGTH_SHORT).show();
                    dismiss();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error leaving waitlist: ", e);
                    Toast.makeText(getActivity(), "Failed to leave waitlist: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
