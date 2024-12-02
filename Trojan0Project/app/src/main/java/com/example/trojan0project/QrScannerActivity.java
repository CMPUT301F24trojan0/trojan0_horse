/**
 * Activity that handles QR code scanning and processes the scanned data.
 * It uses the ZXing library to scan QR codes and Firebase Firestore to fetch event details
 * based on the scanned data. If a valid event is found, it navigates to the `EventDetailsActivity`
 * to display the event information.
 */

package com.example.trojan0project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.Result;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

import org.json.JSONObject;

public class QrScannerActivity extends AppCompatActivity {
    private static final String TAG = "QrScannerActivity";
    private DecoratedBarcodeView barcodeScannerView;
    private FirebaseFirestore db;

    private Button cancelButton;

    /**
     * Initializes the activity, sets up the barcode scanner view, and configures the cancel button.
     * It also starts continuous QR code scanning and processes the scanned QR code data.
     *
     * @param savedInstanceState The saved instance state for restoring the activity's previous state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: Activity started");
        setContentView(R.layout.activity_qr_scanner);

        // Initialize views and Firestore
        barcodeScannerView = findViewById(R.id.zxing_barcode_scanner);
        cancelButton = findViewById(R.id.btn_cancel);
        db = FirebaseFirestore.getInstance();

        // Set up cancel button functionality
        cancelButton.setOnClickListener(v -> {
            Log.d(TAG, "Cancel button clicked");
            setResult(RESULT_CANCELED);
            finish();
        });

        // Set up continuous QR code scanning
        barcodeScannerView.decodeContinuous(new BarcodeCallback() {
            @Override
            public void barcodeResult(BarcodeResult result) {
                String scannedData = result.getText();
                if (scannedData != null) {
                    Log.d(TAG, "barcodeResult: QR code scanned successfully.");
                    handleScannedData(scannedData);
                }
            }

            @Override
            public void possibleResultPoints(@NonNull java.util.List<com.google.zxing.ResultPoint> resultPoints) {
                // No action required for now
            }
        });
    }

    /**
     * Processes the scanned QR code data. It parses the QR code data (which is expected to be in JSON format)
     * and queries the Firebase Firestore database to fetch event details. If an event is found, it navigates
     * to the `EventDetailsActivity` to display the event information.
     *
     * @param scannedData The scanned QR code data in string format.
     */
    private void handleScannedData(String scannedData) {
        try {
            // Parse JSON data from QR code
            JSONObject jsonObject = new JSONObject(scannedData);
            String eventId = jsonObject.getString("id");
            Log.d(TAG, "handleScannedData: Parsed eventId = " + eventId);

            // Query Firebase to fetch the event data
            db.collection("events").document(eventId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            Log.d(TAG, "Event found in database: " + documentSnapshot.getData());

                            // Pass data to EventDetailsActivity
                            Intent intent = new Intent(QrScannerActivity.this, EventDetailsActivity.class);
                            intent.putExtra("eventName", documentSnapshot.getString("eventName"));
                            intent.putExtra("description", documentSnapshot.getString("description"));
                            intent.putExtra("latitude", documentSnapshot.getDouble("latitude"));
                            intent.putExtra("longitude", documentSnapshot.getDouble("longitude"));
                            intent.putExtra("posterPath", documentSnapshot.getString("posterPath"));
                            intent.putExtra("time", documentSnapshot.getString("time"));

                            startActivity(intent);

                            // Finish QrScannerActivity to prevent multiple instances
                            finish();
                        } else {
                            Log.d(TAG, "handleScannedData: Event not found.");
                            Toast.makeText(QrScannerActivity.this, "Event not found in database.", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "handleScannedData: Error fetching event: ", e);
                        Toast.makeText(QrScannerActivity.this, "Error fetching event.", Toast.LENGTH_SHORT).show();
                    });
        } catch (Exception e) {
            Log.e(TAG, "handleScannedData: Invalid QR Code format: ", e);
            Toast.makeText(this, "Invalid QR Code format.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Resumes the barcode scanner when the activity is resumed.
     * This method is called when the activity becomes visible to the user.
     */
    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: Resuming barcode scanner");
        barcodeScannerView.resume();
    }

    /**
     * Pauses the barcode scanner when the activity is paused.
     * This method is called when the activity is no longer visible to the user.
     */
    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: Pausing barcode scanner");
        barcodeScannerView.pause();
    }

    /**
     * Handles the destruction of the activity. It logs when the activity is destroyed.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: QrScannerActivity destroyed");
    }
}
