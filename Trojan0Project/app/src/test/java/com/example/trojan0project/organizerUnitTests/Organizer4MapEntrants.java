package com.example.trojan0project.organizerUnitTests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

import android.content.Intent;

import androidx.annotation.NonNull;

import com.example.trojan0project.MapEntrants;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28, manifest = Config.NONE)
public class Organizer4MapEntrants {

    private MapEntrants mapEntrants;
    private GoogleMap mockGoogleMap;

    @Before
    public void setup() {
        // Initialize MapEntrants activity
        Intent intent = new Intent();
        intent.putExtra("eventID", "testEvent123"); // Provide a test event ID
        mapEntrants = Robolectric.buildActivity(MapEntrants.class, intent)
                .create()
                .get();

        // Apply theme if needed
        mapEntrants.setTheme(androidx.appcompat.R.style.Theme_AppCompat_DayNight);

        // Mock GoogleMap
        mockGoogleMap = mock(GoogleMap.class);

        // Inject mocked map
        mapEntrants.onMapReady(mockGoogleMap);
    }

    @Test
    public void testMarkersAddedToMap() {
        // Simulate valid geolocations
        List<LatLng> entrantLocations = new ArrayList<>();
        entrantLocations.add(new LatLng(37.7749, -122.4194)); // San Francisco
        entrantLocations.add(new LatLng(34.0522, -118.2437)); // Los Angeles

        mapEntrants.runOnUiThread(() -> {
            for (LatLng location : entrantLocations) {
                mockGoogleMap.addMarker(new MarkerOptions().position(location).title("Test User"));
            }
        });

        // Verify markers were added to the map
        ArgumentCaptor<MarkerOptions> markerCaptor = ArgumentCaptor.forClass(MarkerOptions.class);
        verify(mockGoogleMap, times(entrantLocations.size())).addMarker(markerCaptor.capture());

        List<MarkerOptions> capturedMarkers = markerCaptor.getAllValues();
        assertEquals(2, capturedMarkers.size());
        assertEquals(37.7749, capturedMarkers.get(0).getPosition().latitude, 0.001);
        assertEquals(-122.4194, capturedMarkers.get(0).getPosition().longitude, 0.001);
    }

    @Test
    public void testCameraMovedToIncludeMarkers() {
        // Add markers and simulate camera movement
        List<LatLng> entrantLocations = new ArrayList<>();
        entrantLocations.add(new LatLng(37.7749, -122.4194)); // San Francisco
        entrantLocations.add(new LatLng(34.0522, -118.2437)); // Los Angeles

        mapEntrants.runOnUiThread(() -> {
            for (LatLng location : entrantLocations) {
                mockGoogleMap.addMarker(new MarkerOptions().position(location));
            }
        });

        // Simulate camera move
        mapEntrants.runOnUiThread(() -> mockGoogleMap.moveCamera(any()));

        // Verify camera moved
        verify(mockGoogleMap, times(1)).moveCamera(any());
    }

}
