package com.example.trojan0project;

import com.example.trojan0project.BrowseImagesAdmin;
import com.example.trojan0project.Image;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

//import org.hamcrest.Matchers.matches;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

@RunWith(AndroidJUnit4.class)
public class BrowseImagesAdminTest {

    @Rule
    public ActivityScenarioRule<BrowseImagesAdmin> activityRule = new ActivityScenarioRule<>(BrowseImagesAdmin.class);

    private BrowseImagesAdmin browseImagesAdmin;

    @Before
    public void testSetUp(){
        activityRule.getScenario().onActivity(activity -> {
            browseImagesAdmin = activity;
        });
    }

    @Test
    public void testUserProfilePicture() throws InterruptedException{
        browseImagesAdmin.getUserProfilePicture();

        Thread.sleep(5000);

        onView(withId(R.id.images_list)).check(matches(isDisplayed()));
    }

    @Test
    public void testGetEventImages() throws InterruptedException{
        browseImagesAdmin.getEventImages();

        Thread.sleep(3000);

        onView(withId(R.id.images_list)).check(matches(isDisplayed()));
    }


}
