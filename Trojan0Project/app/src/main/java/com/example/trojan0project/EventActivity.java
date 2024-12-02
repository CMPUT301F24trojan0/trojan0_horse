/**
 * Represents an activity for managing events in the application, providing functionalities
 * such as viewing, generating QR codes, and deleting events. It integrates with Firestore
 * for event data retrieval and storage and provides an admin interface for event management.
 *
 * <p>This class implements the `DeleteEventFragment.DeleteEventDialogListener` interface to
 * handle dialog-based event deletion operations.</p>
 *
 * Features:
 * - Displays a list of events retrieved from Firestore.
 * - Allows navigation to a profile management page.
 * - Supports generating and deleting QR codes for events.
 * - Provides the ability to delete entire events from Firestore.
 *
 * Design Rationale:
 * - Extends `AppCompatActivity` to utilize modern Android UI components.
 * - Uses Firestore for cloud-based event data management.
 */

package com.example.trojan0project;

import static android.content.ContentValues.TAG;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class EventActivity extends AppCompatActivity implements DeleteEventFragment.DeleteEventDialogListener {

    private ListView eventAdminList;
    private ArrayAdapter<Event> eventAdminAdapter;
    public ArrayList<Event> dataList;
    private Event selectedEvent = null;
    private FirebaseFirestore db;
    private ImageView qrCodeImageView;
    private ProgressDialog progressDialog;

    /**
     * Handles the creation of the `EventActivity` activity, initializing UI components,
     * setting up Firestore integration, and defining event listeners for user interactions.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down, this Bundle contains the most recent data.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.events_main);

        Toolbar toolbar = findViewById(R.id.browse_events_toolbar);
        setSupportActionBar(toolbar);

        // Set the title of the action bar to be empty
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);  // Enable the "up" button
        }

        db = FirebaseFirestore.getInstance();
        Log.d(TAG, "onCreate: Firestore instance initialized");

        final CollectionReference collectionReference = db.collection("events");

        String deviceId = getIntent().getStringExtra("DEVICE_ID");
        Log.d(TAG, "onCreate: Device ID: " + deviceId);

        ImageButton profilePage = findViewById(R.id.profile_button);

        profilePage.setOnClickListener(v -> {
            Log.d(TAG, "onCreate: Profile button clicked, navigating to profile page");
            Intent intent = new Intent(EventActivity.this, BrowseProfileAdmin.class);
            intent.putExtra("DEVICE_ID", deviceId);
            startActivity(intent);
        });

        dataList = new ArrayList<Event>();
        eventAdminList = findViewById(R.id.admin_events_list);
        eventAdminAdapter = new EventArrayAdapter(this, dataList);
        eventAdminList.setAdapter(eventAdminAdapter);

        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e(TAG, "Error fetching events: ", error);
                    return;
                }
                Log.d(TAG, "onEvent: Fetching events from Firestore");

                dataList.clear();

                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    String eventName = (String) doc.getData().get("eventName");
                    String qrContent = (String) doc.getData().get("qrContent");

                    Log.d(TAG, "onEvent: Event Name: " + eventName);
                    Log.d(TAG, "onEvent: QR Content: " + qrContent);

                    if (qrContent != null) {
                        Bitmap qrCodeBitmap = generateQRCode(qrContent);
                        dataList.add(new Event(eventName, qrCodeBitmap));
                        Log.d(TAG, "onEvent: QR Code generated for event: " + eventName);
                    } else {
                        dataList.add(new Event(eventName, null));
                    }
                }

                eventAdminAdapter.notifyDataSetChanged();
            }
        });

        eventAdminList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectedEvent = dataList.get(i);

                // Log the selected event's name
                String eventName = selectedEvent.getEventName();
                Log.d(TAG, "onItemClick: Event selected: " + eventName);

                // Query Firestore for the eventId associated with this eventName
                db.collection("events")
                        .whereEqualTo("eventName", eventName)
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {
                            if (!queryDocumentSnapshots.isEmpty()) {
                                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                    String eventId = document.getString("eventId");
                                    Log.d(TAG, "onItemClick: Retrieved eventId: " + eventId);
                                    DeleteEventFragment fragment = DeleteEventFragment.newInstance(selectedEvent, eventId);
                                    fragment.show(getSupportFragmentManager(), "Delete Event");

                                    // Pass the eventId to DisplayEventDetails activity
                                    Intent intent = new Intent(getBaseContext(), DisplayEventDetails.class);
                                    intent.putExtra("eventId", eventId);
                                    intent.putExtra("eventName", eventName);
                                    //startActivity(intent);

                                    break; // Exit loop after finding the first match
                                }
                            } else {
                                Log.e(TAG, "onItemClick: No event found with name: " + eventName);
                                Toast.makeText(EventActivity.this, "No event found with this name.", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(e -> {
                            Log.e(TAG, "onItemClick: Firestore query failed: ", e);
                            Toast.makeText(EventActivity.this, "Failed to fetch event details.", Toast.LENGTH_SHORT).show();
                        });



            }
        });
    }

    /**
     * Handles the selection of menu items, specifically the "home" button (up navigation).
     * This method is called when an item in the options menu is selected.
     *
     * @param item The menu item that was selected.
     * @return True if the menu item is handled, false otherwise.
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // Finish the current activity and return to the previous one
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Generates a QR code Bitmap from the provided content string.
     *
     * @param content The string content to encode in the QR code.
     * @return A Bitmap image of the generated QR code, or null if an error occurs.
     */
    private Bitmap generateQRCode(String content) {
        QRCodeWriter writer = new QRCodeWriter();
        try {
            Log.d(TAG, "generateQRCode: Generating QR code for content: " + content);
            BitMatrix bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, 500, 500);
            Bitmap bmp = Bitmap.createBitmap(500, 500, Bitmap.Config.RGB_565);
            for (int x = 0; x < 500; x++) {
                for (int y = 0; y < 500; y++) {
                    bmp.setPixel(x, y, bitMatrix.get(x, y) ? android.graphics.Color.BLACK : android.graphics.Color.WHITE);
                }
            }
            return bmp;
        } catch (WriterException e) {
            Log.e(TAG, "generateQRCode: QR code generation failed", e);
        }
        return null;
    }

    /**
     * Compresses a QR code Bitmap into a PNG format byte array.
     *
     * @param qrCodeBitmap The Bitmap image of the QR code.
     * @return A byte array containing the compressed image data.
     */
    private byte[] getQRCodeImageData(Bitmap qrCodeBitmap) {
        Log.d(TAG, "getQRCodeImageData: Compressing QR code image to byte array");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        qrCodeBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    /**
     * Deletes the QR code associated with the provided event from the Firestore database.
     *
     * @param event The event whose QR code needs to be deleted.
     */
    @Override
    public void deleteQRCode(Event event) {
        Log.d(TAG, "deleteQRCode: Deleting QR code for event: " + event.getEventName());
        if (selectedEvent != null) {
            db.collection("events")
                    .whereEqualTo("eventName", selectedEvent.getEventName())
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            db.collection("events").document(document.getId())
                                    .update("qrContent", null)
                                    .addOnSuccessListener(aVoid -> {
                                        eventAdminAdapter.notifyDataSetChanged();
                                        Toast.makeText(this, "QR code deleted", Toast.LENGTH_SHORT).show();
                                        Log.d(TAG, "deleteQRCode: QR code deleted successfully");
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(this, "QR code not deleted", Toast.LENGTH_SHORT).show();
                                        Log.e(TAG, "deleteQRCode: QR code deletion failed", e);
                                    });
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "QR code not deleted", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "deleteQRCode: Error fetching event for deletion", e);
                    });
        }
    }

    /**
     * Deletes the specified event from the Firestore database.
     *
     * @param event The event to be deleted.
     */
    @Override
    public void deleteEvent(Event event) {
        Log.d(TAG, "deleteEvent: Deleting event: " + event.getEventName());
        if (selectedEvent != null) {
            db.collection("events")
                    .whereEqualTo("eventName", selectedEvent.getEventName())
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            db.collection("events").document(document.getId()).delete()
                                    .addOnSuccessListener(Void -> {
                                        dataList.remove(selectedEvent);
                                        eventAdminAdapter.notifyDataSetChanged();
                                        Toast.makeText(this, "Event is deleted", Toast.LENGTH_SHORT).show();
                                        Log.d(TAG, "deleteEvent: Event deleted successfully");
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(this, "Event not deleted", Toast.LENGTH_SHORT).show();
                                        Log.e(TAG, "deleteEvent: Event deletion failed", e);
                                    });
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Event not deleted", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "deleteEvent: Error fetching event for deletion", e);
                    });
        }
    }
}
