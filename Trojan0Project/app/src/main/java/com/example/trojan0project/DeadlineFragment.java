package com.example.trojan0project;

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
import android.widget.Toast;

import com.google.firebase.Timestamp;

import java.util.Calendar;

public class DeadlineFragment extends DialogFragment {

    private OnDeadlineSavedListener onDeadlineSavedListener;

    public interface OnDeadlineSavedListener {
        void onDeadlineSaved(Timestamp deadline);
    }

    public void setOnDeadlineSavedListener(OnDeadlineSavedListener listener) {
        this.onDeadlineSavedListener = listener;
    }

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
