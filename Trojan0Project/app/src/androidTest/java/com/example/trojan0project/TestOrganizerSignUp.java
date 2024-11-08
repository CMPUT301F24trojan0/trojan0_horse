package com.example.trojan0project;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.intent.Intents.init;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.release;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class TestOrganizerSignUp {

    @Rule
    public ActivityScenarioRule<MainActivity> activityScenarioRule = new ActivityScenarioRule<>(MainActivity.class);
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Test
    public void testOrganizerFlow() throws InterruptedException {
        // Initialize Espresso Intents to capture intents
        init();

        // Click on the "organizerButton" to go to the sign-up page
        Thread.sleep(2000);
        onView(withId(R.id.organizerButton)).perform(click());

        // Verify that the intent to start OrganizerSignUpActivity was fired
        intended(hasComponent(OrganizerSignUpActivity.class.getName()));

        // Simulate typing text into the "facility name" field
        onView(withId(R.id.facility_input)).perform(typeText("TROJAN0"), closeSoftKeyboard());

        // Click on the "signup_button" to submit the form
        onView(withId(R.id.signup_button)).perform(click());

        // Verify that the intent to start ViewProfile activity was fired
        Thread.sleep(2000);
        intended(hasComponent(OrganizerPageActivity.class.getName()));

        // Click on the "signup_button" to submit the form
        onView(withId(R.id.create_event_button)).perform(click());
        intended(hasComponent(CreateEventActivity.class.getName()));
        Thread.sleep(1000);
        release();
    }

    @After
    public void deleteDeviceIDFromFirestore() {
        String facility_name = "TROJAN0"; // Username used in the test
        String email = "trojan0@gmail.com"; // Email used in the test

        // Query Firestore to find the document with the exact fields
        db.collection("users")
                .whereEqualTo("facilityName", facility_name)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        // Loop through all documents that match (there should be only one in this case)
                        queryDocumentSnapshots.getDocuments().forEach(documentSnapshot -> {
                            String documentId = documentSnapshot.getId();

                            // Delete the document by its ID
                            db.collection("users").document(documentId)
                                    .delete()
                                    .addOnSuccessListener(aVoid -> {
                                        System.out.println("Document with facility name " + facility_name + " deleted successfully.");
                                    })
                                    .addOnFailureListener(e -> {
                                        System.err.println("Error deleting document: " + e.getMessage());
                                    });
                        });
                    } else {
                        System.out.println("No document found with the specified username and email.");
                    }
                })
                .addOnFailureListener(e -> {
                    System.err.println("Error finding document: " + e.getMessage());
                });
    }
}