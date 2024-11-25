package com.example.trojan0project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_scanner);

        barcodeScannerView = findViewById(R.id.zxing_barcode_scanner);
        db = FirebaseFirestore.getInstance();

        barcodeScannerView.decodeContinuous(new BarcodeCallback() {
            @Override
            public void barcodeResult(BarcodeResult result) {
                String scannedData = result.getText();
                if (scannedData != null) {
                    handleScannedData(scannedData);
                }
            }

            @Override
            public void possibleResultPoints(@NonNull java.util.List<com.google.zxing.ResultPoint> resultPoints) {
                // Not needed for now
            }
        });
    }

    private void handleScannedData(String scannedData) {
        try {
            JSONObject jsonObject = new JSONObject(scannedData);
            String eventId = jsonObject.getString("id");

            // Query Firebase to find the event
            db.collection("events").document(eventId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            // Event found, pass data to EventDetailsActivity
                            Intent intent = new Intent(QrScannerActivity.this, EventDetailsActivity.class);
                            intent.putExtra("eventName", documentSnapshot.getString("eventName"));
                            intent.putExtra("description", documentSnapshot.getString("description"));
                            intent.putExtra("latitude", documentSnapshot.getDouble("latitude"));
                            intent.putExtra("longitude", documentSnapshot.getDouble("longitude"));
                            intent.putExtra("posterPath", documentSnapshot.getString("posterPath"));
                            intent.putExtra("time", documentSnapshot.getString("time"));
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(QrScannerActivity.this, "Event not found in database.", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error fetching event: ", e);
                        Toast.makeText(QrScannerActivity.this, "Error fetching event.", Toast.LENGTH_SHORT).show();
                    });
        } catch (Exception e) {
            Log.e(TAG, "Invalid QR Code format: ", e);
            Toast.makeText(this, "Invalid QR Code format.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        barcodeScannerView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        barcodeScannerView.pause();
    }
}
