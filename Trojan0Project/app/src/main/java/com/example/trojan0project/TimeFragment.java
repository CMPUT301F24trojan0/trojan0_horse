/**
 * Purpose:
 * This fragment allows the user to pick a time. It includes a button to save the selected time.
 * When the time is saved, it sends the selected time back to the activity or parent fragment via the OnTimeSavedListener interface.
 *
 * Design Rationale:
 * The fragment uses a TimePicker to allow the user to select a time, which is then formatted into a string representing time format.
 *
 * Outstanding Issues:
 * No Issues.
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
import android.widget.TimePicker;

public class TimeFragment extends DialogFragment {

    private TimePicker timePicker;
    private Button saveButton;
    private OnTimeSavedListener listener;
    /**
     * Interface definition for a callback to be invoked when the time is saved.
     */
    public interface OnTimeSavedListener {
        void onTimeSaved(String time);
    }
    /**
     * Sets the listener for time saving events.
     *
     * @param listener The listener that will be notified when the time is saved.
     */
    public void setOnTimeSavedListener(OnTimeSavedListener listener) {
        this.listener = listener;
    }
    /**
     * Creates the view for the time selection dialog.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container          If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
     * @return The view for the time selection dialog.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_time, container, false);

        timePicker = view.findViewById(R.id.timePicker);
        timePicker.setIs24HourView(true);
        saveButton = view.findViewById(R.id.saveTimeButton);

        saveButton.setOnClickListener(v -> {
            int hour = timePicker.getCurrentHour();
            int minute = timePicker.getCurrentMinute();
            String time = String.format("%02d:%02d", hour, minute);
            if (listener != null) {
                listener.onTimeSaved(time);
            }
            dismiss();
        });

        return view;
    }
    /**
     * Resizes the dialog when it is resumed to set its width and height.
     */
    @Override
    public void onResume() {
        super.onResume();
        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.85); // Set width to 85% of screen width
        int height = ViewGroup.LayoutParams.WRAP_CONTENT; // Or any specific height
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(width, height);
        }
    }
}
