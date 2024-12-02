package com.example.trojan0project;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.example.trojan0project.HandleEXIF.handleEXIF;

public class ViewProfile extends AppCompatActivity {
    private static final String TAG = "ViewProfile";
    private static final int QR_SCANNER_REQUEST_CODE = 200;

    private ImageView profilePicture;
    private ImageButton editImageButton;
    private ImageButton deleteImageButton;
    private ProgressBar progressBar;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private Bitmap bitmap;
    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText usernameEditText;
    private EditText emailEditText;
    private EditText phoneNumberEditText;
    private Switch notificationsToggle;
    private Button viewEventsButton, scanQrCodeButton;
    private FirebaseFirestore db;
    private String deviceId;
    private String username;

    ActivityResultLauncher<PickVisualMediaRequest> pickVisualMedia =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                if (uri != null) {
                    ContentResolver CR = this.getContentResolver();
                    String type = CR.getType(uri);
                    if (type.contains("image")) {
                        profilePicture.setImageURI(uri);
                        try {
                            bitmap = handleEXIF(this, uri);
                            uploadImage();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                } else {
                    Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_view); // Links the XML layout to this activity

        // Initialize Firestore and Firebase Storage
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        // Retrieve the device ID from the intent
        Intent intent = getIntent();
        deviceId = intent.getStringExtra("DEVICE_ID");

        // Reference UI elements
        profilePicture = findViewById(R.id.profileIcon);
        editImageButton = findViewById(R.id.editImageIcon);
        deleteImageButton = findViewById(R.id.deleteImageIcon);
        progressBar = findViewById(R.id.progressBar);
        firstNameEditText = findViewById(R.id.firstNameInput);
        lastNameEditText = findViewById(R.id.lastNameInput);
        usernameEditText = findViewById(R.id.usernameInput);
        emailEditText = findViewById(R.id.emailInput);
        phoneNumberEditText = findViewById(R.id.phoneNumberInput);
        notificationsToggle = findViewById(R.id.notificationsToggle);
        viewEventsButton = findViewById(R.id.viewEventsButton);
        scanQrCodeButton = findViewById(R.id.scanQrCodeButton);

        // Load profile data
        loadProfileData();

        // Set up the button to update profile image
        editImageButton.setOnClickListener(v -> updateImage());

        // Save details and go to View Events Page
        viewEventsButton.setOnClickListener(v -> saveProfileData());

        // Set up the QR Code Scanner button
        scanQrCodeButton.setOnClickListener(v -> {
            Intent qrScannerIntent = new Intent(this, QrScannerActivity.class);
            startActivityForResult(qrScannerIntent, QR_SCANNER_REQUEST_CODE);
        });
    }

    private void loadProfileData() {
        db.collection("users").document(deviceId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    username = document.getString("username");

                    // Populate fields with existing data
                    firstNameEditText.setText(document.getString("first_name"));
                    lastNameEditText.setText(document.getString("last_name"));
                    usernameEditText.setText(username);
                    emailEditText.setText(document.getString("email"));
                    phoneNumberEditText.setText(document.getString("phone_number"));

                    Boolean notifications = document.getBoolean("notifications");
                    if (notifications != null) {
                        notificationsToggle.setChecked(notifications);
                    }

                    // Load profile picture from URL
                    String profilePicUrl = document.getString("profile_picture_url");
                    if (profilePicUrl != null) {
                        Glide.with(this).load(profilePicUrl).into(profilePicture);
                        deleteImageButton.setVisibility(View.VISIBLE);
                        deleteImageButton.setOnClickListener(v -> deleteImage());
                    } else {
                        newImage(username);
                    }

                    Log.d(TAG, "Profile data loaded: " + document.getData());
                } else {
                    Toast.makeText(this, "Profile not found", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.e(TAG, "Error loading profile", task.getException());
                Toast.makeText(this, "Failed to load profile", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveProfileData() {
        String firstName = firstNameEditText.getText().toString().trim();
        String lastName = lastNameEditText.getText().toString().trim();
        String username = usernameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String phoneNumber = phoneNumberEditText.getText().toString().trim();
        boolean notifications = notificationsToggle.isChecked();

        if (firstName.isEmpty() || lastName.isEmpty()) {
            Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> profileData = new HashMap<>();
        profileData.put("first_name", firstName);
        profileData.put("last_name", lastName);
        profileData.put("username", username);
        profileData.put("email", email);
        profileData.put("phone_number", phoneNumber);
        profileData.put("notifications", notifications);

        db.collection("users").document(deviceId).set(profileData, SetOptions.merge())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Profile updated successfully: " + profileData);
                        Toast.makeText(ViewProfile.this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e(TAG, "Profile update failed", task.getException());
                        Toast.makeText(ViewProfile.this, "Profile update failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateImage() {
        pickVisualMedia.launch((new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                .build()));
    }

    private void uploadImage() {
        progressBar.setVisibility(View.VISIBLE);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte[] image = baos.toByteArray();

        UploadTask uploadTask = storageReference.child("profilePictures/" + deviceId).putBytes(image);
        uploadTask.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                storageReference.child("profilePictures/" + deviceId)
                        .getDownloadUrl()
                        .addOnSuccessListener(uri -> {
                            String downloadUrl = uri.toString();

                            db.collection("users").document(deviceId)
                                    .update("profile_picture_url", downloadUrl)
                                    .addOnCompleteListener(updateTask -> {
                                        if (updateTask.isSuccessful()) {
                                            Toast.makeText(ViewProfile.this, "Image uploaded and URL saved", Toast.LENGTH_SHORT).show();
                                            deleteImageButton.setVisibility(View.VISIBLE);
                                            deleteImageButton.setOnClickListener(v -> deleteImage());
                                        } else {
                                            Toast.makeText(ViewProfile.this, "Failed to save URL", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        });
            }
            progressBar.setVisibility(View.GONE);
        });
    }

    private void deleteImage() {
        progressBar.setVisibility(View.VISIBLE);
        StorageReference imageRef = storageReference.child("profilePictures/" + deviceId);
        imageRef.delete().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                db.collection("users").document(deviceId)
                        .update("profile_picture_url", null)
                        .addOnCompleteListener(updateTask -> {
                            progressBar.setVisibility(View.GONE);
                            if (updateTask.isSuccessful()) {
                                Toast.makeText(ViewProfile.this, "Image deleted successfully", Toast.LENGTH_SHORT).show();
                                newImage(username);
                                deleteImageButton.setVisibility(View.GONE);
                            }
                        });
            }
        });
    }

    private void newImage(String username) {
        ImageGenerator mydrawing = new ImageGenerator(this);
        mydrawing.setUserText(String.valueOf(username.charAt(0)));
        profilePicture.setImageDrawable(mydrawing);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == QR_SCANNER_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            String scannedData = data.getStringExtra("SCANNED_DATA");
            if (scannedData != null) {
                Toast.makeText(this, "QR Code Scanned: " + scannedData, Toast.LENGTH_LONG).show();
            }
        }
    }
}