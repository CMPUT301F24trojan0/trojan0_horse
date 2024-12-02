package com.example.trojan0project.View.Organizer;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.trojan0project.R;

/**
 * Purpose:
 * The DescriptionFragment displays a dialog with an input field for users to enter a description.
 * Once the description is entered, the user can save it.
 *
 * Design Rationale:
 * The fragment uses an OnDescriptionSavedListener interface to send the entered description back to the parent.
 *
 * Outstanding Issues:
 * No issues
 */

public class DescriptionFragment extends DialogFragment {

    private EditText descriptionInput;
    private Button saveButton;
    private OnDescriptionSavedListener listener;

    /**
     * Interface for communicating the saved description back to the activity or fragment.
     */
    public interface OnDescriptionSavedListener {
        void onDescriptionSaved(String description);
    }

    /**
     * Sets the listener to handle the saved description.
     *
     * @param listener An instance implementing OnDescriptionSavedListener to handle the saved description.
     */
    public void setOnDescriptionSavedListener(OnDescriptionSavedListener listener) {
        this.listener = listener;
    }

    /**
     * Creates the view for this fragment, inflating the layout and setting up the save button logic.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container          If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     * @return The created view for the fragment with the description input and save button.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_description, container, false);

        descriptionInput = view.findViewById(R.id.descriptionInput);
        saveButton = view.findViewById(R.id.saveDescriptionButton);

        saveButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDescriptionSaved(descriptionInput.getText().toString());
            }
            dismiss();
        });

        return view;
    }

    /**
     * Adjusts the dialog dimensions to 85% of the screen width upon resume.
     */
    @Override
    public void onResume() {
        super.onResume();
        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.85); // 85% of screen width
        int height = ViewGroup.LayoutParams.WRAP_CONTENT;
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(width, height);
        }
    }
}
