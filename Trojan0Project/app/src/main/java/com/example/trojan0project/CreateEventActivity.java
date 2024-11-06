package com.example.trojan0project;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.common.BitMatrix;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

public class CreateEventActivity extends AppCompatActivity {
    private EditText eventNameInput;
    private Switch geolocationSwitch;
    private Button addPosterButton, saveButton;
    private ImageView qrCodeImageView;
    private FusedLocationProviderClient fusedLocationClient;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private double latitude = 0.0;
    private double longitude = 0.0;
    private Uri posterUri;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        // Initialize UI elements
        eventNameInput = findViewById(R.id.eventNameInput);
        geolocationSwitch = findViewById(R.id.geolocationSwitch);
        addPosterButton = findViewById(R.id.addPosterButton);
        saveButton = findViewById(R.id.saveButton);
        qrCodeImageView = findViewById(R.id.qrCodeImageView);
        progressDialog = new ProgressDialog(this);

        // Initialize Firebase services
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        // Request location permission if not granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        // Geolocation Switch Logic
        geolocationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                getLocation();
            }
        });

        // Add Poster Button Logic
        addPosterButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, 1);
        });

        // Save Event Button Logic
        saveButton.setOnClickListener(v -> {
            String eventName = eventNameInput.getText().toString();
            if (validateInput(eventName, posterUri)) {
                Event event = new Event(eventName, latitude, longitude, ""); // posterPath will be set later
                saveEvent(event);
            }
        });
    }

    // Validate event input
    private boolean validateInput(String eventName, Uri posterUri) {
        if (eventName.isEmpty()) {
            Toast.makeText(this, "Please enter an event name", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (posterUri == null) {
            Toast.makeText(this, "Please select a poster image", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    // Retrieve location when requested
    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                    }
                });
    }

    // Handle selected image
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            posterUri = data.getData();  // Store the selected image URI
        }
    }

    // Save event to Firestore
    private void saveEvent(Event event) {
        progressDialog.setMessage("Saving Event...");
        progressDialog.show();

        db.collection("events")
                .add(event)
                .addOnSuccessListener(documentReference -> {
                    String eventId = documentReference.getId();
                    event.setId(eventId);  // Set the document ID as the event ID
                    String qrContent = createQRContent(event); // Create QR content
                    uploadPosterToStorage(eventId, event, qrContent);  // Upload poster and save event details
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Failed to save event", Toast.LENGTH_SHORT).show();
                    Log.e("Firestore Error", "Error saving initial event to Firestore", e);
                });
    }

    // Upload the poster to Firebase Storage
    private void uploadPosterToStorage(String eventId, Event event, String qrContent) {
        StorageReference posterRef = storage.getReference().child("posters/" + eventId + "_poster.jpg");
        posterRef.putFile(posterUri)
                .addOnSuccessListener(taskSnapshot -> posterRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    event.setPosterPath(uri.toString());  // Set the poster URL in the event object

                    // Save the event and QR content to Firestore
                    db.collection("events").document(eventId)
                            .set(event)
                            .addOnSuccessListener(aVoid -> {
                                // Update Firestore with the qrContent field
                                db.collection("events").document(eventId).update("qrContent", qrContent)
                                        .addOnSuccessListener(aVoid2 -> {
                                            Bitmap qrCodeBitmap = generateQRCode(qrContent);
                                            uploadQRCodeToStorage(qrCodeBitmap, eventId);  // Upload QR code
                                        })
                                        .addOnFailureListener(e -> {
                                            progressDialog.dismiss();
                                            Toast.makeText(this, "Failed to save QR content to Firestore", Toast.LENGTH_SHORT).show();
                                            Log.e("Firestore Error", "Failed to save QR content", e);
                                        });
                            })
                            .addOnFailureListener(e -> {
                                progressDialog.dismiss();
                                Toast.makeText(this, "Failed to update event with poster URL", Toast.LENGTH_SHORT).show();
                                Log.e("Firestore Error", "Error updating event with poster URL", e);
                            });
                }))
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Failed to upload poster to Storage", Toast.LENGTH_SHORT).show();
                    Log.e("Storage Error", "Error uploading poster", e);
                });
    }

    private String createQRContent(Event event) {
        JSONObject json = new JSONObject();
        try {
            json.put("id", event.getId());
            json.put("name", event.getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return json.toString();
    }

    private Bitmap generateQRCode(String content) {
        QRCodeWriter writer = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, 500, 500);
            Bitmap bmp = Bitmap.createBitmap(500, 500, Bitmap.Config.RGB_565);
            for (int x = 0; x < 500; x++) {
                for (int y = 0; y < 500; y++) {
                    bmp.setPixel(x, y, bitMatrix.get(x, y) ? android.graphics.Color.BLACK : android.graphics.Color.WHITE);
                }
            }
            return bmp;
        } catch (WriterException e) {
            Log.e("QRCode Error", "Error generating QR Code", e);
        }
        return null;
    }

    private void uploadQRCodeToStorage(Bitmap qrCodeBitmap, String eventId) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        qrCodeBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] qrCodeData = baos.toByteArray();

        StorageReference qrCodeRef = storage.getReference().child("qrcodes/" + eventId + ".png");
        qrCodeRef.putBytes(qrCodeData)
                .addOnSuccessListener(taskSnapshot -> qrCodeRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    db.collection("events").document(eventId).update("qrCodeUrl", uri.toString())
                            .addOnSuccessListener(aVoid -> {
                                progressDialog.dismiss();
                                Toast.makeText(this, "Event and QR code saved successfully", Toast.LENGTH_SHORT).show();
                                qrCodeImageView.setImageBitmap(qrCodeBitmap);
                                refreshActivity();
                            })
                            .addOnFailureListener(e -> {
                                progressDialog.dismiss();
                                Toast.makeText(this, "Failed to save QR code URL to Firestore", Toast.LENGTH_SHORT).show();
                                Log.e("Firestore Error", "Failed to save QR code URL", e);
                            });
                }))
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Failed to upload QR code to Storage", Toast.LENGTH_SHORT).show();
                    Log.e("Storage Error", "Failed to upload QR code", e);
                    refreshActivity();
                });
    }

    // Refresh the activity
    private void refreshActivity() {
        finish();
        overridePendingTransition(0, 0);
        startActivity(getIntent());
        overridePendingTransition(0, 0);
    }
}
