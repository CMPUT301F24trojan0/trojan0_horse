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

    //From ChatpGPT, OpenAI, nov 7 2025, how can i check my facility is being dleted properly
    //should fail right now bc of outstanding issused
    @Test
    public void testDeleteFacility() throws InterruptedException {
        Thread.sleep(3000); // Wait for Firestore data to load


        int[] sizeBeforeDeleting = new int[1];
        activityRule.getScenario().onActivity(activity ->
                sizeBeforeDeleting[0] = activity.dataList.size());


        onData(anything())
                .inAdapterView(withId(R.id.admin_facilities_list))
                .atPosition(0)
                .perform(ViewActions.click());


        onView(withText("Do you want to delete the Facility?")).check(matches(isDisplayed()));


        onView(withId(R.id.button_yes)).perform(ViewActions.click());

        Thread.sleep(3000); // Wait for deletion to complete


        int[] sizeAfterDeleting = new int[1];
        activityRule.getScenario().onActivity(activity ->
                sizeAfterDeleting[0] = activity.dataList.size());


        assertTrue(sizeAfterDeleting[0] == sizeBeforeDeleting[0]);
    }

}

