package com.example.trojan0project.entrantUITests;

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
import static org.junit.Assert.assertNotNull;

import android.provider.Settings;
import android.util.Log;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.idling.CountingIdlingResource;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.trojan0project.MainActivity;
import com.example.trojan0project.R;
import com.example.trojan0project.Controller.Entrant.ViewEvents;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;

@RunWith(AndroidJUnit4.class)
public class Entrant114LeaveWaitlist {
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
        eventsRef = db.collection("events");

        // Add the event data to Firestore
        String EventID = "150";
        String deviceID = Settings.Secure.getString(ApplicationProvider.getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);

        // Event data for the events collection
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("eventName", "trojan0event");
        eventData.put("description", "Team trojan0's event");
        eventData.put("num_sampled", 1);
        eventData.put("qrContent", "{\"id\":\"101\",\"name\":\"trojan0event\"}");

        // Add the event data to the "events" collection
        eventsRef.document(EventID).set(eventData)
                .addOnSuccessListener(aVoid -> {
                    Log.d("ViewingAllSignedUpEvents", "Event added to events collection successfully!");

                    // Add the event reference to the user's "events" map
                    devicesRef.document(deviceID).get()
                            .addOnSuccessListener(documentSnapshot -> {
                                if (documentSnapshot.exists()) {
                                    // Update the existing "events" map or create a new one
                                    Map<String, Object> userData = documentSnapshot.getData();
                                    Map<String, Object> userEvents = (Map<String, Object>) userData.get("events");
                                    if (userEvents == null) {
                                        userEvents = new HashMap<>();
                                    }
                                    userEvents.put(EventID, 0); // Example value for user signup

                                    // Update the Firestore document with user's events
                                    devicesRef.document(deviceID)
                                            .update("events", userEvents)
                                            .addOnSuccessListener(updateVoid -> {
                                                Log.d("ViewingAllSignedUpEvents", "Event added to user's events map!");

                                                // Add the user to the event's "users" map
                                                eventsRef.document(EventID).get()
                                                        .addOnSuccessListener(eventSnapshot -> {
                                                            if (eventSnapshot.exists()) {
                                                                Map<String, Object> eventUsers = (Map<String, Object>) eventSnapshot.get("users");
                                                                if (eventUsers == null) {
                                                                    eventUsers = new HashMap<>();
                                                                }
                                                                eventUsers.put(deviceID, 0); // Add the deviceID with a value of 1 (indicating user signup)

                                                                // Update the event document to include the user in the "users" map
                                                                eventsRef.document(EventID)
                                                                        .update("users", eventUsers)
                                                                        .addOnSuccessListener(userUpdateVoid -> {
                                                                            Log.d("ViewingAllSignedUpEvents", "User added to event's users map!");
                                                                        })
                                                                        .addOnFailureListener(e -> Log.e("ViewingAllSignedUpEvents", "Failed to add user to event's users map: " + e.getMessage()));
                                                            } else {
                                                                Log.e("ViewingAllSignedUpEvents", "Event document does not exist: " + EventID);
                                                            }
                                                        })
                                                        .addOnFailureListener(e -> Log.e("ViewingAllSignedUpEvents", "Failed to fetch event document: " + e.getMessage()));
                                            })
                                            .addOnFailureListener(e -> Log.e("ViewingAllSignedUpEvents", "Failed to update user's events map: " + e.getMessage()));
                                } else {
                                    // If the document does not exist, create a new one with the "events" map
                                    Map<String, Object> newUserData = new HashMap<>();
                                    Map<String, Object> newUserEvents = new HashMap<>();
                                    newUserEvents.put(EventID, 0); // Example value for user signup
                                    newUserData.put("events", newUserEvents);

                                    devicesRef.document(deviceID).set(newUserData)
                                            .addOnSuccessListener(createVoid -> Log.d("ViewingAllSignedUpEvents", "New user document created with events map!"))
                                            .addOnFailureListener(e -> Log.e("ViewingAllSignedUpEvents", "Failed to create new user document: " + e.getMessage()));
                                }
                            })
                            .addOnFailureListener(e -> Log.e("ViewingAllSignedUpEvents", "Failed to fetch user document: " + e.getMessage()));
                })
                .addOnFailureListener(e -> Log.e("ViewingAllSignedUpEvents", "Failed to add event to events collection: " + e.getMessage()));
    }

    @After
    public void tearDown() {
        // Unregister the idling resource and release intents after the test
        IdlingRegistry.getInstance().unregister(mIdlingResource);
        release();  // Release intents after the test
    }

    @Test
    public void entrantCanLeaveWaitlistt() throws InterruptedException {
        // Initialize Espresso Intents to capture intents
        init();

        Thread.sleep(2000);

        // Ensure "View All Events" button is displayed and clickable
        onView(withId(R.id.view_all_events_button))
                .check(matches(isDisplayed()))
                .check(matches(isClickable()));

        // Click to view all events
        onView(withId(R.id.view_all_events_button)).perform(click());

        // Ensure the ViewEvents layout is displayed
        onView(withId(R.id.ViewEventsLayout)).check(matches(isDisplayed()));

        // Verify that the ViewEvents activity is opened
        intended(hasComponent(ViewEvents.class.getName()));

        // Ensure RecyclerView is displayed and populated
        onView(withId(R.id.recyclerView)).check(matches(isDisplayed()));

        // Simulate clicking the first item in the RecyclerView
        onView(withId(R.id.recyclerView)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        // Check if the status dialog is displayed
        onView(withText("You are currently on the waitlist for this event.")).check(matches(isDisplayed()));

        // Click the accept button on the dialog
        onView(withId(R.id.buttonLeaveWaitlist)).perform(click());

        Thread.sleep(1000);

        Thread.sleep(1000);
    }
}
