package com.example.trojan0project;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

/**
 * A `DialogFragment` that provides a user interface for sending notifications.
 * The dialog contains an input field for entering a message and two buttons for sending or cancelling the action.
 *
 * Purpose:
 * - Enables the user to input and send a notification message.
 *
 * Features:
 * - Custom dialog layout with an input field and action buttons.
 * - Validates user input to ensure the message is not empty.
 *
 * Design Rationale:
 * - Uses a listener interface to communicate with the hosting activity.
 *
 * Usage:
 * - Hosting activity must implement `NotificationDialogListener` to receive the notification message.
 *
 * Methods:
 * - `onAttach(Context context)`: Ensures the hosting activity implements the required listener.
 * - `onCreateDialog(Bundle savedInstanceState)`: Configures the dialog's appearance and functionality.
 */

public class NotificationDialogFragment extends DialogFragment {

    public interface NotificationDialogListener {
        void onSendNotification(String message);
    }

    private NotificationDialogListener listener;
    private EditText notificationEditText;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (NotificationDialogListener) context; // Activity must implement the interface
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement NotificationDialogListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());

        // Inflate custom view
        View view = LayoutInflater.from(requireContext()).inflate(R.layout.fragment_notification_dialog, null);
        notificationEditText = view.findViewById(R.id.notification_edit_text);
        Button sendButton = view.findViewById(R.id.send_button);
        Button cancelButton = view.findViewById(R.id.cancel_button);

        // Handle Send Button
        sendButton.setOnClickListener(v -> {
            String message = notificationEditText.getText().toString().trim();
            if (!message.isEmpty()) {
                listener.onSendNotification(message); // Pass the message to the listener
                dismiss(); // Close the dialog
            } else {
                notificationEditText.setError("Message cannot be empty");
            }
        });

        // Handle Cancel Button
        cancelButton.setOnClickListener(v -> dismiss());

        // Set custom view in the dialog
        builder.setView(view);

        return builder.create();
    }


}