package com.example.trojan0project.organizerUnitTests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.Arrays;
import java.util.List;

//@RunWith(RobolectricTestRunner.class)
//@Config(sdk = 28, manifest = Config.NONE)
public class Organizer4InvitedToApply {

    @Test
    public void testAddMarkersAndCamera() {
        // Mock GoogleMap
        GoogleMap mockGoogleMap = mock(GoogleMap.class);

        // Mock addMarker behavior to return a mock Marker
        when(mockGoogleMap.addMarker(any(MarkerOptions.class))).thenReturn(mock(Marker.class));

        // Manually define entrant locations
        List<LatLng> entrantLocations = Arrays.asList(
                new LatLng(34.0522, -118.2437), // Los Angeles
                new LatLng(40.7128, -74.0060)  // New York
        );

        // Simulate adding markers and moving the camera
        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
        for (LatLng location : entrantLocations) {
            mockGoogleMap.addMarker(new MarkerOptions().position(location).title("Entrant"));
            boundsBuilder.include(location); // Add to bounds
        }

        // Build the bounds for the camera
        LatLngBounds bounds = boundsBuilder.build();
        mockGoogleMap.moveCamera(any());

        // Verify markers were added
        ArgumentCaptor<MarkerOptions> markerCaptor = ArgumentCaptor.forClass(MarkerOptions.class);
        verify(mockGoogleMap, times(2)).addMarker(markerCaptor.capture());

        // Validate marker positions
        List<MarkerOptions> capturedMarkers = markerCaptor.getAllValues();
        assertEquals(2, capturedMarkers.size());
        assertEquals(new LatLng(34.0522, -118.2437), capturedMarkers.get(0).getPosition());
        assertEquals(new LatLng(40.7128, -74.0060), capturedMarkers.get(1).getPosition());

        // Verify the camera was moved
        verify(mockGoogleMap).moveCamera(any()); // Ensure camera moved
    }
}
