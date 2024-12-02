package com.example.trojan0project.entrantUITests;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.init;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.release;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isClickable;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertNotNull;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.idling.CountingIdlingResource;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;

import com.example.trojan0project.MainActivity;
import com.example.trojan0project.R;
import com.example.trojan0project.ViewProfile;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class Entrant102ProfileUpdateTest {
    @Rule
    public ActivityScenarioRule<MainActivity> activityScenarioRule = new ActivityScenarioRule<>(MainActivity.class);

    private FirebaseFirestore db;
    private FirebaseStorage sb;
    private CollectionReference devicesRef;
    private CountingIdlingResource mIdlingResource;  // Ensure this is a CountingIdlingResource
    private UiDevice device;

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
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
    }

    @Test
    public void entrantNavigatesToUpdateProfileTest() throws InterruptedException, UiObjectNotFoundException {
        // Initialize Espresso Intents to capture intents
        init();

        Thread.sleep(2000);
        onView(withId(R.id.update_profile_button)).check(matches(isDisplayed()))
                .check(matches(isClickable()));
        onView(withId(R.id.update_profile_button)).perform(click());

        onView(withId(R.id.ViewProfileLayout)).check(matches(isDisplayed()));
        intended(hasComponent(ViewProfile.class.getName()));

        // Clear the existing text and type the new value
        onView(withId(R.id.usernameInput)).perform(clearText(), typeText("trojan0"), closeSoftKeyboard());
        onView(withContentDescription("Navigate up")).perform(click());
        Thread.sleep(1000);

        onView(withId(R.id.firstNameInput)).perform(typeText("trojan"), closeSoftKeyboard());
        onView(withId(R.id.lastNameInput)).perform(typeText("0"), closeSoftKeyboard());
        onView(withId(R.id.phoneNumberInput)).perform(typeText("123456789"), closeSoftKeyboard());
        onView(withId(R.id.notificationsToggle)).perform(click());
        onView(withContentDescription("Navigate up")).perform(click());
        Thread.sleep(1000);
    }

    @After
    public void tearDown() {
        IdlingRegistry.getInstance().unregister(mIdlingResource);
        release();  // Release intents after the test
    }
}
