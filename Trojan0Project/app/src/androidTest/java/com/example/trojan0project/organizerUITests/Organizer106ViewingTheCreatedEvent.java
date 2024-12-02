package com.example.trojan0project.organizerUITests;

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
import static org.junit.Assert.assertNotNull;

import android.provider.Settings;
import android.util.Log;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.idling.CountingIdlingResource;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObjectNotFoundException;

import com.example.trojan0project.Controller.CommonControllers.EventsListActivityOrganizer;
import com.example.trojan0project.MainActivity;
import com.example.trojan0project.R;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RunWith(AndroidJUnit4.class)
public class Organizer106ViewingTheCreatedEvent {
    @Rule
    public ActivityScenarioRule<MainActivity> activityScenarioRule = new ActivityScenarioRule<>(MainActivity.class);

    private FirebaseFirestore db;
    private FirebaseStorage sb;
    private CollectionReference devicesRef;
    private CollectionReference eventsRef;
    private CountingIdlingResource mIdlingResource;  // Ensure this is a CountingIdlingResource
    private UiDevice device;
    private String eventID;

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
        eventsRef = db.collection("events");
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        String deviceID = Settings.Secure.getString(ApplicationProvider.getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);

        eventID = "150";
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("eventName", "trojan0horseEvent");
        eventData.put("description", "This is trojan0horseFacility's first event! Welcome!");
        eventData.put("latitude", 37.4220936);
        eventData.put("longitude", -122.083922);
        eventData.put("time", "06:00");
        eventData.put("posterPath", null);
        eventData.put("qrContent", "{\"id\":\"150\",\"name\":\"trojan0event\"}");

        Date deadlineDate = new Date(2024 - 1900, 11, 15, 17, 0); // Month is 0-based in the Date constructor
        Timestamp deadline = new Timestamp(deadlineDate);
        eventData.put("deadline", deadline);

        eventsRef.document(eventID).set(eventData).addOnSuccessListener(aVoid -> {
            Log.d("organizerUITests", "Great success!");
        }).addOnFailureListener(e -> {
            Log.e("organizerUITests", "This suit is black not :( " + e.getMessage());
        });

        devicesRef.document(deviceID).update("organizer_details.events", FieldValue.arrayUnion(eventID))
                .addOnSuccessListener(aVoid -> {
                    Log.d("organizerUITests", "Event added to user successfully!");
                })
                .addOnFailureListener(e -> {
                    Log.e("organizerUITests", "Failed to add event to user: " + e.getMessage());
                });
    }

    @Test
    public void organizerViewsEvent() throws InterruptedException, UiObjectNotFoundException {
        // Initialize Espresso Intents to capture intents
        init();

        Thread.sleep(2000);
        onView(withId(R.id.view_events_button)).check(matches(isDisplayed()))
                .check(matches(isClickable()));
        onView(withId(R.id.view_events_button)).perform(click());

        // Verify that the ViewEvents activity is opened
        intended(hasComponent(EventsListActivityOrganizer.class.getName()));

        // Ensure RecyclerView is displayed and populated
        onView(withId(R.id.events_recycler_view)).check(matches(isDisplayed()));

        // Simulate clicking the first item in the RecyclerView
        onView(withId(R.id.events_recycler_view)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        // Clear the existing text and type the new value
        Thread.sleep(1000);

    }

    @After
    public void tearDown() {
        IdlingRegistry.getInstance().unregister(mIdlingResource);
        release();  // Release intents after the test
    }
}
