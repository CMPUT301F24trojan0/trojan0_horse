package com.example.trojan0project.entrantUITests;

import androidx.test.espresso.idling.CountingIdlingResource;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;

import com.example.trojan0project.MainActivity;
import com.example.trojan0project.R;
import com.example.trojan0project.Controller.Entrant.UserSignUpActivity;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.InstrumentationRegistry.getContext;
import static androidx.test.espresso.Espresso.onView;
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
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import android.provider.Settings;

@RunWith(AndroidJUnit4.class)
public class Entrant100SignUpTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityScenarioRule = new ActivityScenarioRule<>(MainActivity.class);

    private FirebaseFirestore db;
    private FirebaseStorage sb;
    private CollectionReference devicesRef;
    private CountingIdlingResource mIdlingResource;  // Ensure this is a CountingIdlingResource
    private static final String LOCAL_HOST = "10.0.2.2";
    private String username = "TROJAN0";
    private String email = "trojan0@gmail.com";
    private String deviceID = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
    private UiDevice device;

    @BeforeClass
    public static void setupFirebase() {
        // Ensure Firestore uses emulator
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance();
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

    @After
    public void tearDown() {
        IdlingRegistry.getInstance().unregister(mIdlingResource);
        release();  // Release intents after the test
    }

    @Test
    public void entrantNavigatesToSignUpTest() throws InterruptedException, UiObjectNotFoundException {
        // Initialize Espresso Intents to capture intents
        init();

        // Increment the idling resource to signal that the activity is being worked on
        Thread.sleep(2000);
        onView(ViewMatchers.withId(R.id.userButton)).check(matches(isDisplayed()))
                .check(matches(isClickable()));

        onView(withId(R.id.userButton)).perform(click());

        // Fill registration details
        onView(withId(R.id.userSignUpLayout)).check(matches(isDisplayed()));
        intended(hasComponent(UserSignUpActivity.class.getName()));
        onView(withId(R.id.username)).perform(typeText(username), closeSoftKeyboard());
        onView(withId(R.id.email)).perform(typeText(email), closeSoftKeyboard());
        onView(withId(R.id.signUpButton)).perform(click());
        Thread.sleep(1000);

        // Find the "Allow" button by its text and click it
        UiObject allowButton = device.findObject(new UiSelector().text("Allow"));
        if (allowButton.exists()) {
            allowButton.click();
        } else {
            throw new RuntimeException("Allow button not found!");
        }
    }
}
