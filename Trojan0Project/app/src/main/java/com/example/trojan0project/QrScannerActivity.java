package com.example.trojan0project;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.Result;
import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

import org.json.JSONObject;

import java.util.List;

public class QrScannerActivity extends AppCompatActivity {
    private static final String TAG = "QrScannerActivity";
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 200;

    private DecoratedBarcodeView barcodeScannerView;
    private FirebaseFirestore db;
    private Button cancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: Activity started");
        setContentView(R.layout.activity_qr_scanner);

        // Initialize views
        barcodeScannerView = findViewById(R.id.zxing_barcode_scanner);
        cancelButton = findViewById(R.id.btn_cancel);
        db = FirebaseFirestore.getInstance();

        // Request camera permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    CAMERA_PERMISSION_REQUEST_CODE);
        } else {
            initializeScanner();
        }

        // Cancel button functionality
        cancelButton.setOnClickListener(v -> {
            Log.d(TAG, "Cancel button clicked");
            setResult(RESULT_CANCELED);
            finish();
        });
    }

    // Handle the result of the camera permission request
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Camera permission granted");
                initializeScanner();
            } else {
                Log.e(TAG, "Camera permission denied");
                Toast.makeText(this, "Camera permission is required to scan QR codes", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    // Initialize the QR scanner
    private void initializeScanner() {
        barcodeScannerView.decodeContinuous(new BarcodeCallback() {
            @Override
            public void barcodeResult(BarcodeResult result) {
                String scannedData = result.getText();
                if (scannedData != null) {
                    Log.d(TAG, "QR code scanned successfully.");
                    handleScannedData(scannedData);
                }
            }

            @Override
            public void possibleResultPoints(@NonNull List<ResultPoint> resultPoints) {
                // No action required for now
            }
        });
    }

    // Handle the scanned QR code data
    private void handleScannedData(String scannedData) {
        try {
            JSONObject jsonObject = new JSONObject(scannedData);
            String eventId = jsonObject.getString("id");
            Log.d(TAG, "Parsed eventId = " + eventId);

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
                            intent.putExtra("deadline", documentSnapshot.getTimestamp("deadline").toDate().getTime());
                            intent.putExtra("maxNumberOfEntrants", documentSnapshot.getLong("maxNumberOfEntrants").intValue());

                            startActivity(intent);
                            finish();
                        } else {
                            Log.e(TAG, "Event not found in database.");
                            Toast.makeText(QrScannerActivity.this, "Event not found.", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error fetching event data", e);
                        Toast.makeText(QrScannerActivity.this, "Error fetching event.", Toast.LENGTH_SHORT).show();
                    });
        } catch (Exception e) {
            Log.e(TAG, "Invalid QR Code format", e);
            Toast.makeText(this, "Invalid QR Code format.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (barcodeScannerView != null) {
            barcodeScannerView.resume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (barcodeScannerView != null) {
            barcodeScannerView.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "QrScannerActivity destroyed");
    }
}
