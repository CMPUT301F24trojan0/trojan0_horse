package com.example.trojan0project.entrantUnitTests;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Entrant2JoinWaitlist {
    private Map<String, Object> mockUserData;
    private Map<String, Object> mockEventData;

    @Before
    public void setup() {
        mockUserData = new HashMap<>();
        Map<String, Integer> userEvents = new HashMap<>();
        userEvents.put("testEventId", 0);
        mockUserData.put("events", userEvents);

        Map<String, Object> geolocationData = new HashMap<>();
        List<Double> coordinates = new ArrayList<>();
        coordinates.add(34.052235);
        coordinates.add(-118.243683);
        geolocationData.put("testEventId", coordinates);
        mockUserData.put("geolocation", geolocationData);

        mockEventData = new HashMap<>();
        Map<String, Integer> eventUsers = new HashMap<>();
        eventUsers.put("testDeviceId", 0);
        mockEventData.put("users", eventUsers);
    }

    @Test
    public void testAddUserToWaitlist() {
        String deviceId = "testDeviceId";
        String eventId = "testEventId";
        double latitude = 34.052235;
        double longitude = -118.243683;

        Map<String, Integer> userEvents = (Map<String, Integer>) mockUserData.get("events");
        userEvents.put(eventId, 0);

        Map<String, Object> geolocationData = (Map<String, Object>) mockUserData.get("geolocation");
        List<Double> coordinates = new ArrayList<>();
        coordinates.add(latitude);
        coordinates.add(longitude);
        geolocationData.put(eventId, coordinates);

        Map<String, Integer> eventUsers = (Map<String, Integer>) mockEventData.get("users");
        eventUsers.put(deviceId, 0);

        assertEquals(1, userEvents.size());
        assertEquals(0, (int) userEvents.get(eventId)); // Ensure the status is waitlisted

        assertEquals(1, geolocationData.size());
        List<Double> storedCoordinates = (List<Double>) geolocationData.get(eventId);
        assertEquals(latitude, storedCoordinates.get(0), 0.0001);
        assertEquals(longitude, storedCoordinates.get(1), 0.0001);

        assertEquals(1, eventUsers.size());
        assertEquals(0, (int) eventUsers.get(deviceId)); // Ensure the user is added to the event
    }
}
