package com.example.trojan0project.entrantUITests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import android.provider.Settings;
import android.util.Log;

import androidx.test.core.app.ApplicationProvider;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import org.junit.Before;
import org.junit.Test;

public class Entrant4VerifyProfileUpdate {
    private FirebaseFirestore db;
    private FirebaseStorage sb;
    private CollectionReference devicesRef;
    private CollectionReference eventsRef;
    private static final String LOCAL_HOST = "10.0.2.2";  // Emulator's IP on Android
    private final String username = "trojan0";
    private final String email = "trojan0@gmail.com";
    private final String deviceID = Settings.Secure.getString(ApplicationProvider.getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);

    @Before
    public void setup() {
        // Set up Firebase to use the emulator
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance();

        // Firebase Firestore and Firebase Storage setup
        db = FirebaseFirestore.getInstance();
        devicesRef = db.collection("users");
        eventsRef = db.collection("events");
    }

    @Test
    public void verifyUserUpdatedProfile() throws InterruptedException {
        // Wait for async Firestore operations to complete
        Thread.sleep(3000);  // Consider using a better approach like CountDownLatch

        // Fetch the user document from Firestore using the deviceID
        devicesRef.document(deviceID).get()
                .addOnSuccessListener(documentSnapshot -> {
                    // Check if the document exists
                    if (documentSnapshot.exists()) {
                        // Retrieve the username and email from the document
                        String usernameFromFirestore = documentSnapshot.getString("username");
                        String user_typeFromFirestore = documentSnapshot.getString("user_type");
                        String emailFromFirestore = documentSnapshot.getString("email");
                        String firstNameFromFirestore = documentSnapshot.getString("first_name");
                        String lastNameFromFirestore = documentSnapshot.getString("last_name");
                        String phoneNumberFromFirestore = documentSnapshot.getString("phone_number");

                        // Verify if the username and email match the expected values
                        assertNotNull("Username should not be null", usernameFromFirestore);
                        assertNotNull("Usertype should not be null", user_typeFromFirestore);
                        assertNotNull("First Name should not be null", firstNameFromFirestore);
                        assertNotNull("Last Name should not be null", lastNameFromFirestore);
                        assertNotNull("Phone Number should not be null", phoneNumberFromFirestore);
                        assertNotNull("Email should not be null", emailFromFirestore);

                        // Check if the values match the expected values
                        assertEquals("Username does not match", username, usernameFromFirestore);
                        assertEquals("Usertype does not match", "entrant", user_typeFromFirestore);
                        assertEquals("Email does not match", email, emailFromFirestore);
                        assertEquals("First Name does not match", "trojan", firstNameFromFirestore);
                        assertEquals("Last Name does not match", "0", lastNameFromFirestore);
                        assertEquals("Phone Number does not match", "123456789", phoneNumberFromFirestore);

                    } else {
                        Log.e("verifyEntrantSignedUp", "Document does not exist for device: " + deviceID);
                        fail("Document does not exist for device: " + deviceID);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("verifyEntrantSignedUp", "Failed to verify user: " + e.getMessage());
                    fail("Failed to fetch document: " + e.getMessage());
                });
    }
}
