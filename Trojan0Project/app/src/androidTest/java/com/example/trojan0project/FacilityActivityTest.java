package com.example.trojan0project;

import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;


import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.anything;
import static org.junit.Assert.assertTrue;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.filters.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class FacilityActivityTest {

    @Rule
    public ActivityScenarioRule<FacilityActivity> activityRule =
            new ActivityScenarioRule<>(FacilityActivity.class);

    //From ChatpGPT, OpenAI showed what error I was getting and it responded by saying the
    // info from firestore is not being loaded before the test, so had to put delay
    @Test
    public void testBrowseFacility() throws InterruptedException{
        Thread.sleep(3000);

        onView(withId(R.id.admin_facilities_list)).check(matches(isDisplayed()));

        onData(anything()).inAdapterView(withId(R.id.admin_facilities_list));
    }


}

