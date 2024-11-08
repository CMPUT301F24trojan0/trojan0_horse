/**
 * Purpose:
 * The EditFacilityFragment allows users to update the name of a facility.
 * The new name is entered in a text field and saved by clicking a button.
 *
 * Design Rationale:
 * This fragment uses a callback interface, OnFacilityNameUpdatedListener,
 * to send the updated facility name back
 *
 * Outstanding Issues:
 * No issues
 */
package com.example.trojan0project;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class EditFacilityFragment extends Fragment {

    private EditText editFacilityName;
    private Button saveFacilityButton;

    /**
     * Interface to communicate the updated facility name.
     */
    public interface OnFacilityNameUpdatedListener {
        void onFacilityNameUpdated(String newFacilityName);
    }

    private OnFacilityNameUpdatedListener callback;

    /**
     * Attaches the fragment to its parent activity, verifying that the activity implements
     * the callback interface.
     *
     * @param context The context in which the fragment is operating.
     * @throws ClassCastException if the activity does not implement the required interface.
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            // Ensure the activity implements the callback interface
            callback = (OnFacilityNameUpdatedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnFacilityNameUpdatedListener");
        }
    }
    /**
     * Creates and returns the fragment's view hierarchy, setting up UI elements and event listeners.
     *
     * @param inflater           LayoutInflater to inflate the view.
     * @param container          Parent view to attach the fragment UI.
     * @param savedInstanceState Saved state of the fragment.
     * @return The constructed view for this fragment.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_facility, container, false);

        editFacilityName = view.findViewById(R.id.edit_facility_name);
        saveFacilityButton = view.findViewById(R.id.save_facility_button);

        saveFacilityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newFacilityName = editFacilityName.getText().toString().trim();

                if (!newFacilityName.isEmpty()) {
                    // Notify the activity about the updated facility name
                    callback.onFacilityNameUpdated(newFacilityName);
                    Toast.makeText(getActivity(), "Facility name updated", Toast.LENGTH_SHORT).show();
                    // Optionally, close the fragment after saving
                    requireActivity().getSupportFragmentManager().popBackStack();
                } else {
                    Toast.makeText(getActivity(), "Please enter a facility name", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }
}

