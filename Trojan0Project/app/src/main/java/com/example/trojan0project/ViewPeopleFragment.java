package com.example.trojan0project;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class ViewPeopleFragment extends Fragment {
    private Switch limitToggle;
    private FrameLayout entrantLimitContainer;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.view_people, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize toggle and container views
        limitToggle = view.findViewById(R.id.limit_toggle);
        entrantLimitContainer = view.findViewById(R.id.entrant_limit_container);

        // Set up toggle listener to show/hide fragment
        limitToggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Show the entrant limit fragment
                entrantLimitContainer.setVisibility(View.VISIBLE);
                loadEntrantLimitFragment();
            } else {
                // Hide the entrant limit fragment
                entrantLimitContainer.setVisibility(View.GONE);
            }
        });
    }

    private void loadEntrantLimitFragment() {
        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment fragment = new EntrantLimitFragment();
        fragmentTransaction.replace(R.id.entrant_limit_container, fragment);
        fragmentTransaction.commit();
    }
}
