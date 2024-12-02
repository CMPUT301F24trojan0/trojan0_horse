/**
 * Purpose:
 * DeleteFacilityFragment shows a dialog to confirm the deletion of a facility.
 * It lets users either delete the facility or cancel the action.
 *
 * Design Rationale:
 * This fragment uses a DeleteFacilityDialogListener to communicate with its FacilityActivity.
 * This makes sure the delete action is handled outside the fragment
 * The design allows for simple and clear interaction with options for confirming or canceling.
 *
 * Outstanding Issues:
 * No issues
 */
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

public class DeleteFacilityFragment extends DialogFragment {
    /**
     * Creates a new instance of DeleteFacilityFragment with the specified facility.
     *
     * @param facility The facility to be passed into the fragment for deletion.
     * @return A new instance of DeleteFacilityFragment with the facility attached as an argument.
     */
    static DeleteFacilityFragment newInstance(Facility facility ){ //creates a new Instance of the class DeleteFacilityFragment
        Bundle args = new Bundle();
        args.putSerializable("facility",  facility);

        DeleteFacilityFragment fragment = new DeleteFacilityFragment();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Listener interface for communicating deletion actions.
     */
    interface DeleteFacilityDialogListener {
        void deleteFacility(Facility facility);

    }

    private DeleteFacilityDialogListener listener;
    private Facility selectedFacility;

    /**
     * Attaches the fragment to the activity, ensuring it implements the DeleteFacilityDialogListener interface.
     *
     * @param context The context of the activity.
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof DeleteFacilityDialogListener) {
            listener = (DeleteFacilityDialogListener) context;
        } else {
            throw new RuntimeException(context + " must implement DeleteFacilityDialogListener");
        }
    }
    /**
     * Creates the dialog, inflating the layout and setting up click listeners for the confirm and cancel buttons.
     *
     * @param savedInstanceState The saved state of the dialog, if available.
     * @return The created dialog with custom layout and actions.
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) { //customize the dialog here
        View view =
                LayoutInflater.from(getContext()).inflate(R.layout.fragment_delete_facility, null);
        Button deleteFacilityButton = view.findViewById(R.id.button_yes);
        Button deleteNoButton = view.findViewById(R.id.button_no);
        //OpenAI, (2024, October 26), "How should I make it so my Event Button actually deletes the event when selected??", ChatGPT
        if (getArguments() != null) {
            selectedFacility = (Facility) getArguments().getSerializable("facility");
        }
        deleteFacilityButton.setOnClickListener(v -> {
            if (listener != null && selectedFacility != null) {
                listener.deleteFacility(selectedFacility);
                dismiss();
            }
        });

        deleteNoButton.setOnClickListener(v -> {
            dismiss();
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        return builder
                .setView(view)

                .create();

    }
    /**
     * Sets the facility selected for deletion.
     *
     * @param facility The facility to be set as selected.
     */
    public void setSelectedFacility(Facility facility) {
        this.selectedFacility= facility;
    }
}
