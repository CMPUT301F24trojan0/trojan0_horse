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

public class DeleteEventFragment extends DialogFragment {

    interface DeleteEventDialogListener {
        void deleteEvent(Event event);

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
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view =
                LayoutInflater.from(getContext()).inflate(R.layout.fragment_delete_event, null);
        Button deleteQRButton = view.findViewById(R.id.button_QR);
        Button deleteEventButton = view.findViewById(R.id.button_event);



        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        return builder
                .setView(view)

                .create();


    }
}
