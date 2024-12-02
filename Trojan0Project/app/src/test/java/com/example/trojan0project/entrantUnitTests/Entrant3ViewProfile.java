package com.example.trojan0project.entrantUnitTests;

import static org.junit.Assert.assertEquals;


import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class Entrant3ViewProfile {
    private Map<String, Object> mockProfileData;

    @Before
    public void setup() {
        mockProfileData = new HashMap<>();
        mockProfileData.put("first_name", "John");
        mockProfileData.put("last_name", "Doe");
        mockProfileData.put("username", "johndoe");
        mockProfileData.put("email", "john.doe@example.com");
        mockProfileData.put("phone_number", "123-456-7890");
        mockProfileData.put("notifications", true);
    }

    @Test
    public void testLoadProfileData() {
        String firstName = (String) mockProfileData.get("first_name");
        String lastName = (String) mockProfileData.get("last_name");
        String username = (String) mockProfileData.get("username");
        String email = (String) mockProfileData.get("email");
        String phoneNumber = (String) mockProfileData.get("phone_number");
        boolean notifications = (boolean) mockProfileData.get("notifications");

        assertEquals("John", firstName);
        assertEquals("Doe", lastName);
        assertEquals("johndoe", username);
        assertEquals("john.doe@example.com", email);
        assertEquals("123-456-7890", phoneNumber);
        assertEquals(true, notifications);
    }

    @Test
    public void testSaveProfileData() {
        mockProfileData.put("first_name", "Jane");
        mockProfileData.put("last_name", "Smith");
        mockProfileData.put("email", "jane.smith@example.com");

        assertEquals("Jane", mockProfileData.get("first_name"));
        assertEquals("Smith", mockProfileData.get("last_name"));
        assertEquals("jane.smith@example.com", mockProfileData.get("email"));
    }

}
