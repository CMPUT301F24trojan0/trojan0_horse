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
import static org.junit.Assert.assertNotNull;

import android.util.Log;

import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.idling.CountingIdlingResource;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObjectNotFoundException;

import com.example.trojan0project.Controller.CommonControllers.EventsListActivityOrganizer;
import com.example.trojan0project.MainActivity;
import com.example.trojan0project.R;
import com.example.trojan0project.Controller.Entrant.ViewProfile;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

public class Organizer107DeleteAllData {
    @Rule
    public ActivityScenarioRule<MainActivity> activityScenarioRule = new ActivityScenarioRule<>(MainActivity.class);

    private FirebaseFirestore db;
    private FirebaseStorage sb;
    private CollectionReference devicesRef;
    private CollectionReference eventsRef;
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
        eventsRef = db.collection("events");
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
    }
    @After
    public void tearDown() throws InterruptedException {
        deleteAllData();
        IdlingRegistry.getInstance().unregister(mIdlingResource);
        release();  // Release intents after the test
    }

    @Test
    public void organizerFinalTest() throws InterruptedException, UiObjectNotFoundException {
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

    // Method to delete all documents in a collection
    private void deleteAllData() throws InterruptedException {
        Thread.sleep(3000);
        devicesRef.get()
                .addOnSuccessListener(querySnapshot -> {
                    WriteBatch batch = db.batch();

                    // Iterate over the documents in the "users" collection and delete them
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        batch.delete(document.getReference());
                    }

                    // Commit the batch delete operation
                    batch.commit()
                            .addOnSuccessListener(aVoid -> {
                                // Optional: Log success or assert successful deletion
                                Log.d("TEST", "All documents deleted successfully.");
                            })
                            .addOnFailureListener(e -> {
                                // Optional: Handle failure case
                                Log.e("TEST", "Failed to delete documents: " + e.getMessage());
                            });
                })
                .addOnFailureListener(e -> {
                    // Handle failure to retrieve documents
                    Log.e("TEST", "Error retrieving documents: " + e.getMessage());
                });

        eventsRef.get()
                .addOnSuccessListener(querySnapshot -> {
                    WriteBatch batch = db.batch();

                    // Iterate over the documents in the "users" collection and delete them
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        batch.delete(document.getReference());
                    }

                    // Commit the batch delete operation
                    batch.commit()
                            .addOnSuccessListener(aVoid -> {
                                // Optional: Log success or assert successful deletion
                                Log.d("TEST", "All documents deleted successfully.");
                            })
                            .addOnFailureListener(e -> {
                                // Optional: Handle failure case
                                Log.e("TEST", "Failed to delete documents: " + e.getMessage());
                            });
                })
                .addOnFailureListener(e -> {
                    // Handle failure to retrieve documents
                    Log.e("TEST", "Error retrieving documents: " + e.getMessage());
                });
    }
}
