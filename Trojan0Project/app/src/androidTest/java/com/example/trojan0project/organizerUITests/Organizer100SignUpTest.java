package com.example.trojan0project.organizerUITests;

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
import static org.junit.Assert.assertNotNull;

import android.provider.Settings;
import android.util.Log;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.idling.CountingIdlingResource;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.UiDevice;

import com.example.trojan0project.MainActivity;
import com.example.trojan0project.OrganizerSignUpActivity;
import com.example.trojan0project.R;
import com.example.trojan0project.UserSignUpActivity;
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

@RunWith(AndroidJUnit4.class)
public class Organizer100SignUpTest {
    @Rule
    public ActivityScenarioRule<MainActivity> activityScenarioRule = new ActivityScenarioRule<>(MainActivity.class);

    private FirebaseFirestore db;
    private FirebaseStorage sb;
    private CollectionReference devicesRef;
    private CollectionReference eventsRef;
    private CountingIdlingResource mIdlingResource;  // Ensure this is a CountingIdlingResource
    private static final String LOCAL_HOST = "10.0.2.2";
    private String username = "TROJAN0facility";
    private String deviceID = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
    private UiDevice device;

    @BeforeClass
    public static void setupFirebase() {
        // Initialize Firebase in the class before any tests are run
        FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext());
        FirebaseFirestore.getInstance().useEmulator(LOCAL_HOST, 8080);
        FirebaseStorage.getInstance().useEmulator(LOCAL_HOST, 9199);
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
        eventsRef = db.collection("events");
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
    }

    @After
    public void tearDown() {
        IdlingRegistry.getInstance().unregister(mIdlingResource);
        release();  // Release intents after the test
    }

    @Test
    public void organizerNavigatesToSignUpTest() throws InterruptedException {
        // Initialize Espresso Intents to capture intents
        init();

        // Increment the idling resource to signal that the activity is being worked on
        Thread.sleep(2000);
        onView(ViewMatchers.withId(R.id.organizerButton)).check(matches(isDisplayed()))
                .check(matches(isClickable()));

        onView(withId(R.id.organizerButton)).perform(click());

        // Fill registration details
        onView(withId(R.id.activity_organizer_signup_layout)).check(matches(isDisplayed()));
        intended(hasComponent(OrganizerSignUpActivity.class.getName()));
        onView(withId(R.id.facility_input)).perform(typeText(username), closeSoftKeyboard());
        onView(withId(R.id.signup_button)).perform(click());
        Thread.sleep(1000);
    }

    private void deleteAllData() {
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
