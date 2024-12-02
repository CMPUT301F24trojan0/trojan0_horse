package com.example.trojan0project.adminUITests;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.init;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.release;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isClickable;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.anything;
import static org.junit.Assert.assertNotNull;

import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.idling.CountingIdlingResource;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.example.trojan0project.Controller.Admin.EventActivity;
import com.example.trojan0project.MainActivity;
import com.example.trojan0project.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class Admin105BrowseDeleteEvents {
    @Rule
    public ActivityScenarioRule<MainActivity> activityScenarioRule = new ActivityScenarioRule<>(MainActivity.class);

    private FirebaseFirestore db;
    private FirebaseStorage sb;
    private CollectionReference devicesRef;
    private CollectionReference eventsRef;
    private CountingIdlingResource mIdlingResource;  // Ensure this is a CountingIdlingResource

    @BeforeClass
    public static void setupFirebase() {
        // Ensure Firestore uses emulator
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseStorage sb = FirebaseStorage.getInstance();
    }

    @Before
    public void setup() {
        // Register the CountingIdlingResource with Espresso
        mIdlingResource = new CountingIdlingResource("ActivityIdle");
        IdlingRegistry.getInstance().register(mIdlingResource);

        // Initialize Firebase and the database only after the activity is created
        activityScenarioRule.getScenario().onActivity(activity -> {
            // Increment the idling resource to signal that the activity is being worked on
            mIdlingResource.increment();

            // Ensure MainActivity is in focus
            assertNotNull(activity);

            // Decrement the idling resource once the activity is fully loaded
            mIdlingResource.decrement();
        });

        // Firebase and Firestore setup after activity is in focus
        db = FirebaseFirestore.getInstance();
        devicesRef = db.collection("users");
    }

    @Test
    public void adminBrowseDeleteEvents() throws InterruptedException {
        // Initialize Espresso Intents to capture intents
        init();

        Thread.sleep(2000);

        onView(withId(R.id.browse_events_button)).check(matches(isDisplayed()))
                .check(matches(isClickable()));
        onView(withId(R.id.browse_events_button)).perform(click());

        onView(withId(R.id.main)).check(matches(isDisplayed()));
        intended(hasComponent(EventActivity.class.getName()));

        onView(withId(R.id.admin_events_list)).check(matches(isDisplayed()));
        onData(anything()).inAdapterView(withId(R.id.admin_events_list));

        Thread.sleep(1000);

        onData(anything())
                .inAdapterView(withId(R.id.admin_events_list))
                .atPosition(0)
                .perform(ViewActions.click());

        //checks if dialog is displayed
        onView(withText("What do you want to delete?")).check(matches(isDisplayed()));

        //clicks yes to delete
        onView(withId(R.id.button_event)).perform(ViewActions.click());

        Thread.sleep(2000);
    }

    @After
    public void tearDown() {
        IdlingRegistry.getInstance().unregister(mIdlingResource);
        release();  // Release intents after the test
    }
}
