package com.example.trojan0project;


import org.junit.runner.RunWith;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.anything;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;

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
        Espresso.onView(ViewMatchers.withId(R.id.profile_list)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        Espresso.onData((CoreMatchers.anything())).inAdapterView(ViewMatchers.withId(R.id.profile_list)).atPosition(0).check(ViewAssertions.matches(ViewMatchers.hasDescendant(ViewMatchers.withId(R.id.profile_name))));

    }

    @Test
    public void testDeleteProfile()throws InterruptedException{
        Thread.sleep(3000);
        //open fragment by clicking on first profile
        Espresso.onData(CoreMatchers.anything())
                .inAdapterView(ViewMatchers.withId(R.id.profile_list))
                .atPosition(0)
                .perform(ViewActions.click());

        //checks if dialog is displayed
        Espresso.onView(ViewMatchers.withText("Do you want to delete the profile?")).inRoot(RootMatchers.isDialog()).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

        //clicks yes to delete
        Espresso.onView(ViewMatchers.withId(R.id.yes_remove_profile)).perform(ViewActions.click());

        Thread.sleep(3000);

        ActivityScenario.launch(BrowseProfileAdmin.class);

        Espresso.onData(anything()).inAdapterView(withId(R.id.profile_list)).atPosition(0).check(ViewAssertions.doesNotExist());
    }

}
