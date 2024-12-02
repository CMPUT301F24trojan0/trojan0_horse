package com.example.trojan0project.View.Organizer;

import android.app.DatePickerDialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;

import com.example.trojan0project.R;
import com.google.firebase.Timestamp;

import java.util.Calendar;

/**
 * Purpose:
 * The `DeadlineFragment` class provides a dialog interface for selecting a deadline date.
 * It includes a date picker dialog and communicates the selected deadline to the parent
 * activity or fragment through a listener interface.
 *
 * Design Rationale:
 * - Utilizes a `DialogFragment` for modularity and reusable UI components.
 * - Implements a listener interface (`OnDeadlineSavedListener`) to decouple deadline selection
 *   logic from the parent activity or fragment.
 * - Uses a `DatePickerDialog` for user-friendly date selection.
 *
 * Outstanding Issues:
 * - No known issues at this time.
 */

public class DeadlineFragment extends DialogFragment {

    private OnDeadlineSavedListener onDeadlineSavedListener;

    /**
     * Interface for listening to deadline selection events.
     * Implement this interface in the parent activity or fragment to handle
     * the deadline selected by the user.
     */
    public interface OnDeadlineSavedListener {
        void onDeadlineSaved(Timestamp deadline);
    }

    /**
     * Sets the listener for the deadline selection event.
     *
     * @param listener The {@link OnDeadlineSavedListener} implementation.
     */
    public void setOnDeadlineSavedListener(OnDeadlineSavedListener listener) {
        this.onDeadlineSavedListener = listener;
    }

    /**
     * Inflates the layout for the fragment and sets up the date picker button.
     *
     * @param inflater  The LayoutInflater object that can be used to inflate views in the fragment.
     * @param container The parent view that this fragment's UI should be attached to.
     * @param savedInstanceState A bundle containing saved state, if available.
     * @return The inflated view for this fragment.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_deadline, container, false);

        Button selectDeadlineButton = view.findViewById(R.id.selectDeadlineButton);
        selectDeadlineButton.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    getContext(),
                    (DatePicker view1, int year, int month, int dayOfMonth) -> {
                        Calendar selectedDate = Calendar.getInstance();
                        selectedDate.set(year, month, dayOfMonth, 23, 59, 59);
                        Timestamp deadline = new Timestamp(selectedDate.getTime());
                        if (onDeadlineSavedListener != null) {
                            onDeadlineSavedListener.onDeadlineSaved(deadline);
                        }
                        dismiss();
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.show();
        });

        return view;
    }
}
