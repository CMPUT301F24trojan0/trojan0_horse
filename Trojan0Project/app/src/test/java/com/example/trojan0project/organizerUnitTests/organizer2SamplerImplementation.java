package com.example.trojan0project.organizerUnitTests;

import static org.mockito.Mockito.*;

import android.widget.ArrayAdapter;

import com.example.trojan0project.Profile;
import com.example.trojan0project.SystemSample;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;

@RunWith(RobolectricTestRunner.class)
public class organizer1SystemSample {

    private SystemSample systemSample;
    private ArrayAdapter<Profile> profileArrayAdapter;

    @Before
    public void setup() {
        // Mock the ArrayAdapter to avoid interacting with real UI components
        profileArrayAdapter = mock(ArrayAdapter.class);

        // Initialize the SystemSample instance
        systemSample = new SystemSample();
        systemSample.waitList = new ArrayList<>();

        // Set the mock profileArrayAdapter field in SystemSample (simulating its internal use)
        systemSample.profileArrayAdapter = profileArrayAdapter;
    }

    /**
     * Test the behavior of the sampleWaitlist method to ensure it correctly samples profiles.
     */
    @Test
    public void testSampleWaitlist() {
        // Create mock profiles
        Profile mockProfile1 = new Profile("John", "Doe", "john.doe@example.com", "device-id-1");
        Profile mockProfile2 = new Profile("Jane", "Doe", "jane.doe@example.com", "device-id-2");
        Profile mockProfile3 = new Profile("Mike", "Smith", "mike.smith@example.com", "device-id-3");

        // Add mock profiles to the waitlist
        systemSample.waitList.add(mockProfile1);
        systemSample.waitList.add(mockProfile2);
        systemSample.waitList.add(mockProfile3);

        // Define the number of attendees to sample
        int numAttendees = 2; // We will sample 2 attendees

        // Act: Call the sampleWaitlist method in SystemSample (this will remove 2 profiles)
        systemSample.sampleWaitlist(numAttendees);

        // Assert: The waitlist should now have 1 remaining profile after sampling
        assert(systemSample.waitList.size() == 1);
        assert(systemSample.waitList.contains(mockProfile3));  // Mike should remain

        // Verify that the adapter was notified
        verify(profileArrayAdapter).notifyDataSetChanged();
    }

    /**
     * Test the behavior of sampleWaitlist when the waitlist is empty.
     */
    @Test
    public void testSampleWaitlistEmpty() {
        // The waitlist is empty
        systemSample.waitList.clear();

        // Call the sampling method
        systemSample.sampleWaitlist(1);

        // Assert: The waitlist should still be empty
        assert(systemSample.waitList.size() == 0);

        // Verify that notifyDataSetChanged was not called since no profiles were sampled
        verify(profileArrayAdapter, never()).notifyDataSetChanged();
    }

    /**
     * Test the behavior of sampleWaitlist when trying to sample more profiles than available.
     */
    @Test
    public void testSampleWaitlistNoProfiles() {
        // The waitlist is empty
        systemSample.waitList.clear();

        // Act: Try to sample more profiles than are available
        systemSample.sampleWaitlist(5);

        // Assert: The waitlist should remain empty
        assert(systemSample.waitList.size() == 0);

        // Verify that notifyDataSetChanged was not called
        verify(profileArrayAdapter, never()).notifyDataSetChanged();
    }
}

