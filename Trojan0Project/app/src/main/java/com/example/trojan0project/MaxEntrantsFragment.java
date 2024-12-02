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

    public interface OnMaxEntrantsSavedListener {
        void onMaxEntrantsSaved(int maxEntrants);
    }

    public void setOnMaxEntrantsSavedListener(OnMaxEntrantsSavedListener listener) {
        this.onMaxEntrantsSavedListener = listener;
    }

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
