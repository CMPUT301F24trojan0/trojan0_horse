package com.example.trojan0project.entrantUnitTests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class Entrant4UpdateUserInfoAndProfilePicture {
    private Map<String, Object> mockProfileData;

    @Before
    public void setup() {
        mockProfileData = new HashMap<>();
        mockProfileData.put("first_name", "Gurleen");
        mockProfileData.put("last_name", "Bamrah");
        mockProfileData.put("username", "gk");
        mockProfileData.put("email", "gurleen@example.com");
        mockProfileData.put("phone_number", "123-456-7890");
        mockProfileData.put("notifications", true);
        mockProfileData.put("profile_picture_url", null);
    }

    @Test
    public void testUpdateProfileInformation() {
        mockProfileData.put("first_name", "Farza");
        mockProfileData.put("last_name", "Sipra");
        mockProfileData.put("email", "farza@example.com");
        mockProfileData.put("phone_number", "987-654-3210");

        assertEquals("Farza", mockProfileData.get("first_name"));
        assertEquals("Sipra", mockProfileData.get("last_name"));
        assertEquals("farza@example.com", mockProfileData.get("email"));
        assertEquals("987-654-3210", mockProfileData.get("phone_number"));
    }

    @Test
    public void testUploadProfilePicture() {
        String mockProfilePictureUrl = "https://example.com/images/profile.jpg";
        mockProfileData.put("profile_picture_url", mockProfilePictureUrl);

        assertNotNull(mockProfileData.get("profile_picture_url"));
        assertEquals(mockProfilePictureUrl, mockProfileData.get("profile_picture_url"));
    }

}
