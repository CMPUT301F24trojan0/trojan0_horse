/**
 * Purpose:
 * This fragment is used to confirm if the admin wants to delete a selected profile
 * This fragment shows 2 buttons Yes to delete and No to cancel
 *
 * Design Rationale:
 * Uses interface RemoveProfileDialogListener which send the yes and no choices back to BrowseProfileAdmin.
 *
 * Outstanding Issues:
 * No issues
 */
package com.example.trojan0project;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class RemoveProfileFragment extends DialogFragment{
    /**
     * Listener interface for handling profile removal events.
     */
    interface RemoveProfileDialogListener{
        void removeProfile(Profile profile);
    }

    private RemoveProfileDialogListener listener;
    private Profile profile;
    /**
     * Constructor to create a new instance of RemoveProfileFragment with the specified profile.
     *
     * @param profile The profile to be removed.
     */
    public RemoveProfileFragment(Profile profile){
        this.profile = profile;
    }
    /**
     * Default constructor for creating an instance of RemoveProfileFragment without a profile.
     */
    public RemoveProfileFragment(){

    }
    /**
     * Attaches the fragment to the host context and checks if the context implements
     * the RemoveProfileDialogListener interface.
     *
     * @param context The context to which the fragment is being attached.
     * @throws RuntimeException If the context does not implement RemoveProfileDialogListener.
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof RemoveProfileDialogListener) {
            listener = (RemoveProfileDialogListener) context;
        } else {
            throw new RuntimeException(context
                    + " must implement RemoveProfileDialogListener");
        }
    }
    /**
     * Creates the dialog for confirming the removal of the profile.
     *
     * @param savedInstanceState If the fragment is being re-constructed from a previous saved state.
     * @return The created dialog with the confirmation UI.
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_remove_profile, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(view);

        Button yesButton = view.findViewById(R.id.yes_remove_profile);
        yesButton.setBackgroundTintList(null);
        Button noButton = view.findViewById(R.id.no_remove_profile);
        noButton.setBackgroundTintList(null);

        yesButton.setOnClickListener(v -> {
            if (profile != null) {
                listener.removeProfile(profile);
                dismiss();
            }
        });
        noButton.setOnClickListener(v -> dismiss());

        return builder.create();


    }
}
