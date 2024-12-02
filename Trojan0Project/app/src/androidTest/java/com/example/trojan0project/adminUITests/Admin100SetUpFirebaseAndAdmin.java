package com.example.trojan0project.adminUITests;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.init;
import static androidx.test.espresso.intent.Intents.release;
import static androidx.test.espresso.matcher.ViewMatchers.isClickable;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertNotNull;

import android.provider.Settings;
import android.util.Log;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.idling.CountingIdlingResource;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.example.trojan0project.MainActivity;
import com.example.trojan0project.R;
import com.google.firebase.FirebaseApp;
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
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@RunWith(JUnit4.class)
public class Admin100SetUpFirebaseAndAdmin {

    @Rule
    public ActivityScenarioRule<MainActivity> activityScenarioRule = new ActivityScenarioRule<>(MainActivity.class);

    private FirebaseFirestore db;
    private FirebaseStorage sb;
    private CollectionReference devicesRef;
    private CollectionReference eventsRef;
    private CountingIdlingResource mIdlingResource;  // Ensure this is a CountingIdlingResource
    private static final String LOCAL_HOST = "10.0.2.2";

    @BeforeClass
    public static void setupFirebase() {
        // Initialize Firebase in the class before any tests are run
        FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext());
        FirebaseFirestore.getInstance().useEmulator(LOCAL_HOST, 8080);
        FirebaseStorage.getInstance().useEmulator(LOCAL_HOST, 9199);
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

        // Add a deviceID with user_type="admin" in the Firestore emulator before running tests
        String deviceID = Settings.Secure.getString(ApplicationProvider.getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);
        Map<String, Object> deviceData = new HashMap<>();
        deviceData.put("user_type", "admin");

        // Add the device data to Firestore
        devicesRef.document(deviceID).set(deviceData).addOnSuccessListener(aVoid -> {
            Log.d("adminUITests", "Great success!");
        }).addOnFailureListener(e -> {
            Log.e("adminUITests", "This suit is black not :( " + e.getMessage());
        });

        // Add the entrant data to Firestore
        String entrantDeviceID = "100";
        Map<String, Object> entrantData = new HashMap<>();
        entrantData.put("user_type", "entrant");
        entrantData.put("username", "trojan0entrant");
        entrantData.put("email", "trojan0@gmail.com");
        entrantData.put("first_name", "trojan");
        entrantData.put("last_name", "trojan");
        devicesRef.document(entrantDeviceID).set(entrantData).addOnSuccessListener(aVoid -> {
            Log.d("adminUITests", "Great success!");
        }).addOnFailureListener(e -> {
            Log.e("adminUITests", "This suit is black not :( " + e.getMessage());
        });

        // Add the organizer data to Firestore
        String organizerDeviceID = "300";
        Map<String, Object> organizerData = new HashMap<>();
        Map<String, Object> organizerDetail = new HashMap<>();
        ArrayList<String> eventIDs = new ArrayList<>();
        eventIDs.add("150");  // Add your event IDs
        organizerDetail.put("events", eventIDs);
        organizerData.put("user_type", "organizer");
        organizerData.put("facilityName", "trojan0organizer");
        organizerData.put("organizer_details", organizerDetail);
        devicesRef.document(organizerDeviceID).set(organizerData).addOnSuccessListener(aVoid -> {
            Log.d("adminUITests", "Great success!");
        }).addOnFailureListener(e -> {
            Log.e("adminUITests", "This suit is black not :( " + e.getMessage());
        });

        // Add the event data to Firestore
        String EventID = "150";
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("eventName", "trojan0event");
        eventData.put("qrContent", "{\"id\":\"101\",\"name\":\"trojan0event\"}");
        eventsRef.document(EventID).set(eventData).addOnSuccessListener(aVoid -> {
            Log.d("adminUITests", "Great success!");
        }).addOnFailureListener(e -> {
            Log.e("adminUITests", "This suit is black not :( " + e.getMessage());
        });
    }

    @Test
    public void adminPageWorks() throws InterruptedException {
        // Initialize Espresso Intents to capture intents
        init();

        Thread.sleep(3000);
        
        onView(withId(R.id.browse_events_button)).check(matches(isDisplayed()))
                .check(matches(isClickable()));
        onView(withId(R.id.browse_events_button)).perform(click());
        Thread.sleep(500);
        onView(withContentDescription("Navigate up")).perform(click());
        Thread.sleep(500);

        onView(withId(R.id.browse_facilities_button)).check(matches(isDisplayed()))
                .check(matches(isClickable()));
        onView(withId(R.id.browse_facilities_button)).perform(click());
        Thread.sleep(500);
        onView(withContentDescription("Navigate up")).perform(click());
        Thread.sleep(500);

        onView(withId(R.id.browse_images_button)).check(matches(isDisplayed()))
                .check(matches(isClickable()));
        onView(withId(R.id.browse_images_button)).perform(click());
        Thread.sleep(500);
        onView(withContentDescription("Navigate up")).perform(click());
        Thread.sleep(500);

        onView(withId(R.id.browse_profiles_button)).check(matches(isDisplayed()))
                .check(matches(isClickable()));
        onView(withId(R.id.browse_profiles_button)).perform(click());
        Thread.sleep(500);
        onView(withContentDescription("Navigate up")).perform(click());
        Thread.sleep(2000);
    }

    @After
    public void tearDown() {
        IdlingRegistry.getInstance().unregister(mIdlingResource);
        release();  // Release intents after the test
    }
}
