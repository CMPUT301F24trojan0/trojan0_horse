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
public class EventActivityTest {

    @Rule
    public ActivityScenarioRule<EventActivity> activityRule =
            new ActivityScenarioRule<>(EventActivity.class);

    //From ChatpGPT, OpenAI showed what error I was getting and it responded by saying the
    // info from firestore is not being loaded before the test, so had to put delay
    @Test
    public void testBrowseEvents() throws InterruptedException{
        Thread.sleep(3000);

        onView(withId(R.id.admin_events_list)).check(matches(isDisplayed()));

        onData(anything()).inAdapterView(withId(R.id.admin_events_list));
    }


    @Test
    public void testDeleteEvent()throws InterruptedException{

        Thread.sleep(3000);

        int[] sizeBeforeDeleting = new int[1]; //holds one size value of list before deleting
        activityRule.getScenario().onActivity(activity ->
                sizeBeforeDeleting[0] = activity.dataList.size());

        onData(anything())
                .inAdapterView(withId(R.id.admin_events_list))
                .atPosition(0)
                .perform(ViewActions.click());

        //checks if dialog is displayed
        onView(withText("What do you want to delete?")).check(matches(isDisplayed()));

        //clicks yes to delete
        onView(withId(R.id.button_event)).perform(ViewActions.click());

        Thread.sleep(3000);


        int[] sizeAfterDeleting = new int[1];
        activityRule.getScenario().onActivity(activity ->
                sizeAfterDeleting[0] = activity.dataList.size());

        assertTrue(sizeAfterDeleting[0] < sizeBeforeDeleting[0]);
    }



    @Test
    public void testDeleteQR()throws InterruptedException{

        Thread.sleep(3000);

        int[] sizeBeforeDeleting = new int[1]; //holds one size value of list before deleting
        activityRule.getScenario().onActivity(activity ->
                sizeBeforeDeleting[0] = activity.dataList.size());

        onData(anything())
                .inAdapterView(withId(R.id.admin_events_list))
                .atPosition(0)
                .perform(ViewActions.click());

        //checks if dialog is displayed
        onView(withText("What do you want to delete?")).check(matches(isDisplayed()));

        //clicks yes to delete
        onView(withId(R.id.button_QR)).perform(ViewActions.click());

        Thread.sleep(3000);


        int[] sizeAfterDeleting = new int[1];
        activityRule.getScenario().onActivity(activity ->
                sizeAfterDeleting[0] = activity.dataList.size());

        assertTrue(sizeAfterDeleting[0] < sizeBeforeDeleting[0]);
    }
}
