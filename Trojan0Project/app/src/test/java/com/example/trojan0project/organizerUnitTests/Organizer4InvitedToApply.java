package com.example.trojan0project.organizerUnitTests;

import static org.junit.Assert.assertEquals;
import com.example.trojan0project.Profile;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

public class Organizer4InvitedToApply {

    private ArrayList<Profile> invited;
    @Before
    public void setup() {
        invited = new ArrayList<>();
    }

    @Test
    public void testAddEntrantToList() {
        // Add a mock entrant directly to the invited list
        Profile mockProfile = new Profile("John", "Doe", "john.doe@example.com");
        invited.add(mockProfile);

        // Verify the list contains the entrant
        assertEquals(1, invited.size());
        assertEquals("John", invited.get(0).getFirstName());
        assertEquals("Doe", invited.get(0).getLastName());
        assertEquals("john.doe@example.com", invited.get(0).getEmail());
    }
}
