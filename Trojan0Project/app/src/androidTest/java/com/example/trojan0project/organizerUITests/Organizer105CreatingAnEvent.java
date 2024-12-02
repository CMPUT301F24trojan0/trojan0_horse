package com.example.trojan0project.organizerUITests;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.PickerActions.setTime;
import static androidx.test.espresso.intent.Intents.init;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.release;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasType;
import static androidx.test.espresso.matcher.ViewMatchers.isClickable;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertNotNull;

import android.widget.TimePicker;

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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class Organizer105CreatingAnEvent {
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
    public void organizerCreatesEvent() throws InterruptedException, UiObjectNotFoundException {
        // Initialize Espresso Intents to capture intents
        init();

        Thread.sleep(2000);
        onView(withId(R.id.create_event_button)).check(matches(isDisplayed()))
                .check(matches(isClickable()));
        onView(withId(R.id.create_event_button)).perform(click());

        // Find the "Allow" button by its text and click it
        Thread.sleep(1000);
        UiObject allowButton = device.findObject(new UiSelector().text("Only this time"));
        if (allowButton.exists()) {
            allowButton.click();
        } else {
            throw new RuntimeException("Allow button not found!");
        }

        // event name
        onView(withId(R.id.eventNameInput)).check(matches(isDisplayed()));
        onView(withId(R.id.eventNameInput))
                .perform(clearText(), typeText("trojan0horseEvent"), closeSoftKeyboard());

        // event description
        onView(withId(R.id.addDescriptionButton)).check(matches(isDisplayed()));
        onView(withId(R.id.addDescriptionButton)).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.descriptionInput))
                .perform(clearText(), typeText("This is trojan0horseFacility's first event! Welcome!"), closeSoftKeyboard());
        onView(withId(R.id.saveDescriptionButton)).perform(click());

        // geolocation
        onView(withId(R.id.geolocationSwitch)).check(matches(isDisplayed()))
                .check(matches(isClickable()));
        onView(withId(R.id.geolocationSwitch)).perform(click());

        // time
        onView(withId(R.id.addTimeButton)).check(matches(isDisplayed()))
                .check(matches(isClickable()));
        onView(withId(R.id.addTimeButton)).perform(click());
        onView(withClassName(Matchers.equalTo(TimePicker.class.getName()))).perform(
                setTime(14, 30) // Example: Set the time to 2:30 PM
        );
        onView(withId(R.id.saveTimeButton)).perform(click());

        // deadline
        onView(withId(R.id.addDeadlineButton)).check(matches(isDisplayed()))
                .check(matches(isClickable()));
        onView(withId(R.id.addDeadlineButton)).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.selectDeadlineButton)).check(matches(isDisplayed()))
                .check(matches(isClickable()));
        onView(withId(R.id.selectDeadlineButton)).perform(click());

        // Find the date picker widget and set a date, for example, set the date to 2024-12-31
        UiObject datePicker = device.findObject(new UiSelector().className("android.widget.DatePicker"));
        if (datePicker.exists()) {
            datePicker.setText("2024-12-31"); // You can set a specific date here
        } else {
            throw new RuntimeException("DatePicker not found!");
        }

        UiObject okButton = device.findObject(new UiSelector().text("OK"));
        if (okButton.exists()) {
            okButton.click();
        } else {
            throw new RuntimeException("OK button not found!");
        }

        // poster (cannot be clicked, so emulating it here
        onView(withId(R.id.addPosterButton)).check(matches(isDisplayed()))
                .check(matches(isClickable()));
        onView(withId(R.id.addPosterButton)).perform(click());
        Thread.sleep(1000);
        UiObject picturesFolder = device.findObject(new UiSelector().text("Pictures")); // Adjust based on actual text in the folder
        picturesFolder.click();

        // Clear the existing text and type the new value
        Thread.sleep(1000);
    }

    @After
    public void tearDown() {
        IdlingRegistry.getInstance().unregister(mIdlingResource);
        release();  // Release intents after the test
    }
}
