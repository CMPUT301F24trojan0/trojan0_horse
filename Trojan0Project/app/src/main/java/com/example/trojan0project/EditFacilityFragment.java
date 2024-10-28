package com.example.trojan0project;

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
                    // Code to save or update facility name (e.g., update in database or shared preferences)
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

