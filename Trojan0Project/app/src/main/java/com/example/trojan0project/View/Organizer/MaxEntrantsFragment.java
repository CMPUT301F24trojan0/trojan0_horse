package com.example.trojan0project.View.Organizer;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;

import com.example.trojan0project.R;

/**
 * Purpose:
 * The `MaxEntrantsFragment` class provides a dialog interface for selecting the maximum number of entrants
 * for an event. It features a `NumberPicker` to allow users to choose a value between 1 and 1000
 * and communicates the selected value back to the parent component using a listener.
 *
 * Design Rationale:
 * - Utilizes a `DialogFragment` for a modular and reusable UI component.
 * - Implements a listener interface (`OnMaxEntrantsSavedListener`) to decouple the max entrants selection logic
 *   from the parent activity or fragment.
 * - Uses a `NumberPicker` to ensure input consistency and provide an intuitive user experience.
 *
 * Outstanding Issues:
 * - No known issues at this time.
 */

public class MaxEntrantsFragment extends DialogFragment {

    private OnMaxEntrantsSavedListener onMaxEntrantsSavedListener;

    /**
     * Interface for communicating the saved maximum entrants value to the parent component.
     *
     * <p>The implementing component will receive the selected maximum entrants value when the user clicks
     * the "Save" button in the fragment.</p>
     *
     * The maximum number of entrants selected by the user.
     */
    public interface OnMaxEntrantsSavedListener {
        void onMaxEntrantsSaved(int maxEntrants);
    }

    /**
     * Sets the listener that will be notified when the maximum entrants value is saved.
     *
     * @param listener The listener to notify when the max entrants are saved.
     */
    public void setOnMaxEntrantsSavedListener(OnMaxEntrantsSavedListener listener) {
        this.onMaxEntrantsSavedListener = listener;
    }

    /**
     * Inflates the view for the dialog, sets up the {@link NumberPicker} to allow the user to select a
     * maximum number of entrants, and handles the "Save" button click event. When the user saves the
     * value, it is communicated back to the parent via the listener.
     *
     * @param inflater The LayoutInflater used to inflate the view.
     * @param container The parent view that the fragment's UI will be attached to.
     * @param savedInstanceState The saved state of the fragment, or null if no state exists.
     * @return The view for the dialog fragment.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_max_entrants, container, false);

        NumberPicker numberPicker = view.findViewById(R.id.maxEntrantsPicker);
        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(1000);
        numberPicker.setWrapSelectorWheel(false);

        Button saveMaxEntrantsButton = view.findViewById(R.id.saveMaxEntrantsButton);
        saveMaxEntrantsButton.setOnClickListener(v -> {
            int maxEntrants = numberPicker.getValue();
            if (onMaxEntrantsSavedListener != null) {
                onMaxEntrantsSavedListener.onMaxEntrantsSaved(maxEntrants);
            }
            dismiss();
        });

        return view;
    }
}
