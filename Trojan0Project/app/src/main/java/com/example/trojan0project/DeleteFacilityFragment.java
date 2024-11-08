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


    static DeleteFacilityFragment newInstance(Facility facility ){ //creates a new Instance of the class DeleteFacilityFragment
        Bundle args = new Bundle();
        args.putSerializable("facility",  facility);

        DeleteFacilityFragment fragment = new DeleteFacilityFragment();
        fragment.setArguments(args);
        return fragment;
    }

    interface DeleteFacilityDialogListener {
        void deleteFacility(Facility facility);


    }

    private DeleteFacilityDialogListener listener;
    private Facility selectedFacility;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof DeleteFacilityDialogListener) {
            listener = (DeleteFacilityDialogListener) context;
        } else {
            throw new RuntimeException(context + " must implement DeleteFacilityDialogListener");
        }
    }
    // CREATING THE FRAGMENT
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
    public void setSelectedFacility(Facility facility) {
        this.selectedFacility= facility;
    }
}
