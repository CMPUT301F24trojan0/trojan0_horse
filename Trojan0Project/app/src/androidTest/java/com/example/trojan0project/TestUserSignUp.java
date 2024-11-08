package com.example.trojan0project;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.intent.Intents.init;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.release;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class TestUserSignUp {

    @Rule
    public ActivityScenarioRule<MainActivity> activityScenarioRule = new ActivityScenarioRule<>(MainActivity.class);

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String deviceID;

    @Test
    public void testUserFlow() throws InterruptedException {
        // Initialize Espresso Intents to capture intents
        init();

        // Start the user flow and capture intents
        Thread.sleep(2000);
        onView(withId(R.id.userButton)).perform(click());

        // Verify that the intent to start UserSignUpActivity was fired
        intended(hasComponent(UserSignUpActivity.class.getName()));

        onView(withId(R.id.username)).perform(typeText("TROJAN0"), closeSoftKeyboard());
        onView(withId(R.id.email)).perform(typeText("trojan0@gmail.com"), closeSoftKeyboard());
        onView(withId(R.id.signUpButton)).perform(click());

        Thread.sleep(2000);

        // Capture the intent to ViewProfile and retrieve the deviceID from it
        intended(hasComponent(ViewProfile.class.getName()));

        // Complete other UI interactions as needed
        onView(withId(R.id.firstNameInput)).perform(typeText("Tro"), closeSoftKeyboard());
        onView(withId(R.id.lastNameInput)).perform(typeText("jan0"), closeSoftKeyboard());
        onView(withId(R.id.phoneNumberInput)).perform(typeText("123456789"), closeSoftKeyboard());
        onView(withId(R.id.notificationsToggle)).perform(click());
        onView(withId(R.id.viewEventsButton)).perform(click());

        Thread.sleep(1000);
        intended(hasComponent(ViewEvents.class.getName()));

        // Release the intents capture after the test
        release();
    }

    @After
    public void deleteDeviceIDFromFirestore() {
        String username = "TROJAN0"; // Username used in the test
        String email = "trojan0@gmail.com"; // Email used in the test

        // Query Firestore to find the document with the exact fields
        db.collection("users")
                .whereEqualTo("username", username)
                .whereEqualTo("email", email)
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
                                        System.out.println("Document with username " + username + " and email " + email + " deleted successfully.");
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