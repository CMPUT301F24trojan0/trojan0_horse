package com.example.trojan0project.organizerUnitTests;

import com.example.trojan0project.Controller.Organizer.EventDetailsActivityOrganizer;
import com.example.trojan0project.R;

import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.*;

import android.widget.Button;

public class organizer3EventDetailsActivityOrganizer {

    private EventDetailsActivityOrganizer activity;
    private Button changePosterButton;
    private Button viewPeopleButton;

    @Before
    public void setup() {

        activity = mock(EventDetailsActivityOrganizer.class);


        changePosterButton = mock(Button.class);
        viewPeopleButton = mock(Button.class);


        when(activity.findViewById(R.id.change_poster_button)).thenReturn(changePosterButton);
        when(activity.findViewById(R.id.view_people_button)).thenReturn(viewPeopleButton);
    }

    @Test
    public void testChangePosterButtonClick() {
        // Simulate clicking on the 'changePosterButton'
        changePosterButton.performClick();


    }

    @Test
    public void testViewPeopleButtonClick() {
        // Simulate clicking on the 'viewPeopleButton'
        viewPeopleButton.performClick();

    }
}











