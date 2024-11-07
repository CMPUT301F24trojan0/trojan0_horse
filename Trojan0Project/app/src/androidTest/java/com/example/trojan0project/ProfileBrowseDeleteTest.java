package com.example.trojan0project;


import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.anything;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertTrue;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.RootMatchers;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.trojan0project.MainActivity;
import com.example.trojan0project.R;

import org.hamcrest.CoreMatchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ProfileBrowseDeleteTest {

    @Rule
    public ActivityScenarioRule<BrowseProfileAdmin> activityRule =
            new ActivityScenarioRule<>(BrowseProfileAdmin.class);

    //From ChatpGPT, OpenAI showed what error I was getting and it responded by saying the
    // info from firestore is not being loaded before the test, so had to put delay
    @Test
    public void testBrowseProfiles() throws InterruptedException{
        Thread.sleep(3000);

        onView(withId(R.id.profile_list)).check(matches(isDisplayed()));

        onData(anything()).inAdapterView(withId(R.id.profile_list));
    }

    @Test
    public void testDeleteProfile()throws InterruptedException{

        Thread.sleep(3000);

        int[] sizeBeforeDeleting = new int[1]; //holds one size value of list before deleting
        activityRule.getScenario().onActivity(activity ->
                sizeBeforeDeleting[0] = activity.dataList.size());

        onData(anything())
                .inAdapterView(withId(R.id.profile_list))
                .atPosition(0)
                .perform(ViewActions.click());

        //checks if dialog is displayed
        onView(withText("Do you want to delete the profile?")).check(matches(isDisplayed()));

        //clicks yes to delete
        onView(withId(R.id.yes_remove_profile)).perform(ViewActions.click());

        Thread.sleep(3000);


        int[] sizeAfterDeleting = new int[1];
        activityRule.getScenario().onActivity(activity ->
                sizeAfterDeleting[0] = activity.dataList.size());

        assertTrue(sizeAfterDeleting[0] < sizeBeforeDeleting[0]);
    }

}
