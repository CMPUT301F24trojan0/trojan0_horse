package com.example.trojan0project;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class DescriptionFragment extends DialogFragment {

    private EditText descriptionInput;
    private Button saveButton;
    private OnDescriptionSavedListener listener;

    public interface OnDescriptionSavedListener {
        void onDescriptionSaved(String description);
    }

    public void setOnDescriptionSavedListener(OnDescriptionSavedListener listener) {
        this.listener = listener;
    }

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
