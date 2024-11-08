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

    // Define the callback interface
    public interface OnFacilityNameUpdatedListener {
        void onFacilityNameUpdated(String newFacilityName);
    }

    private OnFacilityNameUpdatedListener callback;

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

