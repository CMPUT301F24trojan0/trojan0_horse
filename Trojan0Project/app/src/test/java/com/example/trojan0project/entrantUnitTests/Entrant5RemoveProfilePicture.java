package com.example.trojan0project.entrantUnitTests;

import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class Entrant5RemoveProfilePicture {

    private Map<String, Object> mockProfileData;

    @Before
    public void setup() {
        mockProfileData = new HashMap<>();
        mockProfileData.put("first_name", "Gurleen");
        mockProfileData.put("last_name", "Bamrah");
        mockProfileData.put("username", "gk");
        mockProfileData.put("email", "gurleen@example.com");
        mockProfileData.put("phone_number", "1456-678-2344");
        mockProfileData.put("notifications", true);
        mockProfileData.put("profile_picture_url", "https://example.com/images/profile.jpg");
    }

    @Test
    public void testRemoveProfilePicture() {
        mockProfileData.put("profile_picture_url", null);

        assertNull(mockProfileData.get("profile_picture_url"));
    }
}
