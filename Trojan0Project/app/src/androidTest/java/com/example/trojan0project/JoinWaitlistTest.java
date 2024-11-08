package com.example.trojan0project;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.junit.Assert.assertTrue;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.Map;

@RunWith(AndroidJUnit4.class)
public class JoinWaitlistTest {

    @Rule
    public ActivityScenarioRule<JoinWaitlist> activityRule = new ActivityScenarioRule<>(JoinWaitlist.class);


    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String eventId = "CRMooy8t4g20CW2TEZAW";
    private String deviceId = "c49fcd9f6ec4bc07";

    @Test
    public void JoinWaitlistButton(){
        onView(withId(R.id.event_title)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        onView(withId(R.id.location_label)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        onView(withId(R.id.time_label)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        onView(withId(R.id.more_info_label)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }


}
