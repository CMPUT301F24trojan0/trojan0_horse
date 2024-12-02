package com.example.trojan0project.View.Admin;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.trojan0project.Controller.Admin.DisplayEventDetails;
import com.example.trojan0project.Model.Event;
import com.example.trojan0project.R;

/**
 * Purpose:
 * DeleteEventFragment allows users to delete an event or its QR code
 * Shows a dialog with options for event deletion, QR deletion
 *
 * Design Rationale:
 * This fragment uses an interface (DeleteEventDialogListener) to communicate with EventActivity
 * ensuring that the deleting is properly handled
 *
 * Outstanding Issues:
 * No issues
 */

public class DeleteEventFragment extends DialogFragment {
    /**
     * Creates a new instance of DeleteEventFragment with the specified event.
     *
     * @param event The event to be passed into the fragment for deletion.
     * @return A new instance of DeleteEventFragment with the event attached as an argument.
     */
    public static DeleteEventFragment newInstance(Event event, String eventId ){ //creates a new Instance of the class DeleteEventFragment
        Bundle args = new Bundle();
        args.putSerializable("event",  event);
        args.putString("eventId", eventId);

        DeleteEventFragment fragment = new DeleteEventFragment();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Listener interface for communicating deletion actions.
     */
    public interface DeleteEventDialogListener {
        void deleteEvent(Event event);
        void deleteQRCode(Event QR);

    }

    private DeleteEventDialogListener listener;
    private Event selectedEvent;

    /**
     * Attaches the fragment to the activity, ensuring it implements the DeleteEventDialogListener interface.
     *
     * @param context The context of the activity.
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof DeleteEventDialogListener) {
            listener = (DeleteEventDialogListener) context;
        } else {
            throw new RuntimeException(context + " must implement DeleteEventDialogListener");
        }
    }
    /**
     * Creates the dialog, inflating the layout and setting up click listeners for the delete and close buttons.
     *
     * @param savedInstanceState The saved state of the dialog, if available.
     * @return The created dialog with custom layout and actions.
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) { //customize the dialog here
        if (getArguments() != null) {
            selectedEvent = (Event) getArguments().getSerializable("event");
            String eventId = getArguments().getString("eventId");

            Log.d(TAG, "DeleteEventFragment: Received eventId: " + eventId + ", eventName: " );
        }
        View view =
                LayoutInflater.from(getContext()).inflate(R.layout.fragment_delete_event, null);
        Button deleteQRButton = view.findViewById(R.id.button_QR);
        Button deleteEventButton = view.findViewById(R.id.button_event);
        Button xButton = view.findViewById(R.id.close_button);
        //OpenAI, (2024, October 26), "How should I make it so my Event Button actually deletes the event when selected??", ChatGPT
        if (getArguments() != null) {
            selectedEvent = (Event) getArguments().getSerializable("event");
        }
        deleteEventButton.setOnClickListener(v -> {
            if (listener != null && selectedEvent != null) {
                listener.deleteEvent(selectedEvent);
                dismiss();
            }
        });
        deleteQRButton.setOnClickListener(v -> {
            if (listener != null && selectedEvent != null) {
                listener.deleteQRCode(selectedEvent);
                dismiss();
            }
        });
        // Parwiz Forogh, https://www.youtube.com/watch?v=2b7YrS8ZRM4, november 6 2024, youtube
        xButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Retrieve eventId and eventName from the fragment's arguments
                String eventId = getArguments() != null ? getArguments().getString("eventId") : null;


                if (eventId != null) {
                    // Log the data for debugging
                    Log.d("DeleteEventFragment", "Navigating with eventId: " + eventId + ", eventName: " );

                    // Create the Intent to navigate to DisplayEventDetails
                    Intent intent = new Intent(getContext(), DisplayEventDetails.class);
                    intent.putExtra("eventId", eventId);

                    // Start the activity
                    startActivity(intent);
                } else {
                    // Show a Toast if the data is missing
                    Toast.makeText(getContext(), "Event details not available for navigation.", Toast.LENGTH_SHORT).show();
                    Log.e("DeleteEventFragment", "Missing eventId or eventName in arguments.");
                }

                // Dismiss the fragment (optional, based on your flow)
                dismiss();
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        return builder
                .setView(view)

                .create();
    }
    /**
     * Sets the event selected for deletion.
     *
     * @param event The event to be set as selected.
     */
    public void setSelectedEvent(Event event) {
        this.selectedEvent = event;
    }

}