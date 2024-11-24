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

import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.google.zxing.ResultPoint;

import java.util.List;

public class QrScannerActivity extends AppCompatActivity {
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;
    private DecoratedBarcodeView barcodeScannerView;
    private Button cancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_scanner);

        barcodeScannerView = findViewById(R.id.zxing_barcode_scanner);
        cancelButton = findViewById(R.id.btn_cancel);

        // Request Camera Permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    CAMERA_PERMISSION_REQUEST_CODE);
        } else {
            initializeScanner();
        }

        // Handle cancel button click
        cancelButton.setOnClickListener(v -> {
            finish(); // Close the scanner and return to the previous activity
        });
    }

    private void initializeScanner() {
        barcodeScannerView.decodeContinuous(new BarcodeCallback() {
            @Override
            public void barcodeResult(BarcodeResult result) {
                if (result != null && result.getText() != null) {
                    Intent intent = new Intent();
                    intent.putExtra("SCANNED_DATA", result.getText()); // Pass scanned data back
                    setResult(RESULT_OK, intent);
                    finish(); // Close the scanner
                }
            }

            @Override
            public void possibleResultPoints(List<ResultPoint> resultPoints) {
                // Optional: Handle possible result points
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initializeScanner();
            } else {
                Toast.makeText(this, "Camera permission is required to scan QR codes.", Toast.LENGTH_SHORT).show();
                finish(); // Close the activity if permission is denied
            }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        barcodeScannerView.pause();
    }
}
