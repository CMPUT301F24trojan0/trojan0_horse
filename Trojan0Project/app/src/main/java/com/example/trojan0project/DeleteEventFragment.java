package com.example.trojan0project;



import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.io.Serializable;

public class DeleteEventFragment extends DialogFragment {


    static DeleteEventFragment newInstance(Event event ){ //creates a new Instance of the class DeleteEventFragment
        Bundle args = new Bundle();
        args.putSerializable("event",  event);

        DeleteEventFragment fragment = new DeleteEventFragment();
        fragment.setArguments(args);
        return fragment;
    }

    interface DeleteEventDialogListener {
        void deleteEvent(Event event);
        void deleteQRCode(Event QR);

    }

    private DeleteEventDialogListener listener;
    private Event selectedEvent;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof DeleteEventDialogListener) {
            listener = (DeleteEventDialogListener) context;
        } else {
            throw new RuntimeException(context + " must implement DeleteEventDialogListener");
        }
    }
    // CREATING THE FRAGMENT
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) { //customize the dialog here
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
        xButton.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), DisplayEventDetails.class);

            intent.putExtra("event_title", selectedEvent.getEventName());
            intent.putExtra("clicked_event", (Serializable) selectedEvent);
            startActivity(intent);
                dismiss();
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        return builder
                .setView(view)

                .create();


    }
    public void setSelectedEvent(Event event) {
        this.selectedEvent = event;
    }

}
