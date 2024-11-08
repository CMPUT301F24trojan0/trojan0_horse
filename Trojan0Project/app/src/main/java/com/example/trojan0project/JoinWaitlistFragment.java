/**
 * Purpose:
 *Displays users profile information like first name, last name and email.
 * Also allows them to edit it before confirming their waitlist entry
 *
 * Design Rationale:
 * Uses listener interface (JoinWaitlistListener) to send information about user activity back to JoinWaitlist
 *
 * Outstanding Issues:
 *
 */
package com.example.trojan0project;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class JoinWaitlistFragment extends DialogFragment {

    public interface JoinWaitlistListener{
        void onConfirm(Profile profile);
    }

    private Profile profile;
    private JoinWaitlistListener listener;

    public JoinWaitlistFragment(Profile profile){
        this.profile = profile;
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof JoinWaitlistListener) {
            listener = (JoinWaitlistListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement JoinWaitlistListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.waitlist_fragment, container, false);

        EditText firstNameField = view.findViewById(R.id.first_name_fragment);
        EditText lastNameField = view.findViewById(R.id.last_name);
        EditText emailField = view.findViewById(R.id.email);
        Button confirmButton = view.findViewById(R.id.confirm_button);

        if (profile != null){
            firstNameField.setText(profile.getFirstName());
            lastNameField.setText(profile.getLastName());
            emailField.setText(profile.getEmail());
        }


        confirmButton.setOnClickListener(v -> {
            profile.setFirstName(firstNameField.getText().toString());
            profile.setLastName(lastNameField.getText().toString());
            profile.setEmail(emailField.getText().toString());

            if (listener != null) {
                listener.onConfirm(profile);
            }
            dismiss();
        });

        return view;
    }



}
