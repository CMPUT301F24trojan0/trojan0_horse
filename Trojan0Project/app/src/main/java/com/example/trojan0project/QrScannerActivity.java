package com.example.trojan0project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

import org.json.JSONException;
import org.json.JSONObject;

public class QrScannerActivity extends AppCompatActivity {
    private static final String TAG = "QrScannerActivity";
    private DecoratedBarcodeView barcodeScannerView;
    private FirebaseFirestore db;
    private Button cancelButton;

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
                if (result == null || result.getText().isEmpty()) {
                    Log.d(TAG, "barcodeResult: No valid QR code detected.");
                    return;
                }

                String scannedData = result.getText();
                Log.d(TAG, "barcodeResult: QR code scanned successfully.");
                Log.d(TAG, "Scanned data: " + scannedData);

                handleScannedData(scannedData);
            }

            @Override
            public void possibleResultPoints(@NonNull java.util.List<com.google.zxing.ResultPoint> resultPoints) {
                Log.d(TAG, "possibleResultPoints: Result points detected - " + resultPoints.size());
            }
        });
    }

    private void handleScannedData(String scannedData) {
        try {
            Log.d(TAG, "handleScannedData: Processing scanned data.");

            // Parse JSON data from QR code
            JSONObject jsonObject = new JSONObject(scannedData);
            String eventId = jsonObject.optString("id", null); // Use optString to avoid exceptions
            if (eventId == null || eventId.isEmpty()) {
                throw new JSONException("Event ID is missing or invalid in QR code data.");
            }
            Log.d(TAG, "handleScannedData: Parsed eventId = " + eventId);

            // Query Firestore to fetch the event data
            db.collection("events").document(eventId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            Log.d(TAG, "Event found in database: " + documentSnapshot.getData());

                            // Extract event details safely
                            String eventName = documentSnapshot.getString("eventName");
                            String description = documentSnapshot.getString("description");
                            Double latitude = documentSnapshot.getDouble("latitude");
                            Double longitude = documentSnapshot.getDouble("longitude");
                            String posterPath = documentSnapshot.getString("posterPath");
                            String time = documentSnapshot.getString("time");

                            Log.d(TAG, "Starting EventDetailsActivity with event data.");
                            Intent intent = new Intent(QrScannerActivity.this, EventDetailsActivity.class);
                            intent.putExtra("eventId", eventId);
                            intent.putExtra("eventName", eventName != null ? eventName : "N/A");
                            intent.putExtra("description", description != null ? description : "N/A");
                            intent.putExtra("latitude", latitude != null ? latitude : 0.0);
                            intent.putExtra("longitude", longitude != null ? longitude : 0.0);
                            intent.putExtra("posterPath", posterPath != null ? posterPath : "");
                            intent.putExtra("time", time != null ? time : "N/A");

                            startActivity(intent);

                            // Finish QrScannerActivity to prevent multiple instances
                            finish();
                        } else {
                            Log.w(TAG, "handleScannedData: Event not found in database.");
                            Toast.makeText(QrScannerActivity.this, "Event not found in database.", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "handleScannedData: Error fetching event: ", e);
                        Toast.makeText(QrScannerActivity.this, "Error fetching event.", Toast.LENGTH_SHORT).show();
                    });
        } catch (JSONException e) {
            Log.e(TAG, "handleScannedData: Invalid QR Code format or missing fields: ", e);
            Toast.makeText(this, "Invalid QR Code format.", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, "handleScannedData: Unexpected error while processing scanned data: ", e);
            Toast.makeText(this, "Unexpected error occurred.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: Resuming barcode scanner");
        barcodeScannerView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: Pausing barcode scanner");
        barcodeScannerView.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: QrScannerActivity destroyed");
    }
}
