package com.example.trojan0project;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class MapEntrants extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap googleMap;
    private FirebaseFirestore db;
    private List<LatLng> entrantLocations;
    private String eventId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.map_entrants);

        db = FirebaseFirestore.getInstance();

        eventId = getIntent().getStringExtra("eventID");
        if (eventId == null || eventId.isEmpty()) {
            Log.e("MapEntrants", "Event ID is null or empty!");
            finish(); // Close activity if eventId is missing
            return;
        }
        Log.d("MapEntrants", "Received Event ID: " + eventId);

        entrantLocations = new ArrayList<>();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }


    }
    @Override
    public void onMapReady(@NonNull GoogleMap map){
        this.googleMap = map;

        db.collection("events")
                .document(eventId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    entrantLocations.clear();
                    if (documentSnapshot.exists()) {
                        // Retrieve latitude and longitude
                        Double lat = documentSnapshot.getDouble("latitude");
                        Double lng = documentSnapshot.getDouble("longitude");
                        //String eventName = documentSnapshot.getString("eventName");

                        if (lat != null && lng != null) {
                            LatLng location = new LatLng(lat, lng);
                            entrantLocations.add(location);
                            googleMap.addMarker(new MarkerOptions()
                                    .position(location));
                                    //.title(eventName != null ? eventName : "Event Location"));

                            // Center and zoom the map on the event location
                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 12));
                        } else {
                            Log.e("MapEntrants", "Latitude or Longitude is null for event ID: " + eventId);
                        }
                    } else {
                        Log.e("MapEntrants", "No document found for event ID: " + eventId);
                    }

                })
                .addOnFailureListener(e -> {
                    Log.e("MapEntrants", "Error getting entrant location", e);
                });

    }
}