/**
 * A dialog fragment for setting the maximum number of entrants for an event. This fragment contains a
 * {@link NumberPicker} that allows the user to select a value between 1 and 1000, representing the maximum
 * number of entrants for the event. Once the value is selected, the user can save it by clicking the
 * "Save" button. The selected value is passed back to the activity or parent fragment through the
 * {@link OnMaxEntrantsSavedListener} interface.
 *
 * <p>The dialog is used to update the maximum entrants value for an event in a user-friendly way, and the
 * result is communicated back to the parent component that implements the listener.</p>
 *
 * @see NumberPicker
 * @see OnMaxEntrantsSavedListener
 */

package com.example.trojan0project;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Toast;

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
