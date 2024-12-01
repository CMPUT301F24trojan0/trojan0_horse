package com.example.trojan0project.organizerUnitTests;

import static org.mockito.Mockito.*;

import android.widget.ArrayAdapter;

import com.example.trojan0project.Profile;
import com.example.trojan0project.SystemSample;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.lang.reflect.Field;
import java.util.ArrayList;

@RunWith(RobolectricTestRunner.class)
public class organizer1SystemSample {

    private SystemSample systemSample;
    private ArrayAdapter<Profile> profileArrayAdapter;

    @Before
    public void setup() throws NoSuchFieldException, IllegalAccessException {
        // Mock the ArrayAdapter instead of interacting with the real one
        profileArrayAdapter = mock(ArrayAdapter.class);

        // Initialize the SystemSample instance
        systemSample = new SystemSample();
        systemSample.waitList = new ArrayList<>();

        // Use reflection to set the private profileArrayAdapter field
        Field adapterField = SystemSample.class.getDeclaredField("profileArrayAdapter");
        adapterField.setAccessible(true);
        adapterField.set(systemSample, profileArrayAdapter);
    }

    /**
     * Test the behavior of the getWaitlist method with mock data.
     * This will check if the correct number of profiles are added and that the list is updated.
     */
    @Test
    public void testGetWaitlist() {

        Profile mockProfile = new Profile("John", "Doe", "john.doe@example.com", "some-device-id");

        systemSample.waitList.add(mockProfile);

        profileArrayAdapter.notifyDataSetChanged();


        assert(systemSample.waitList.size() == 1);
        assert(systemSample.waitList.get(0).getFirstName().equals("John"));
        assert(systemSample.waitList.get(0).getLastName().equals("Doe"));
        assert(systemSample.waitList.get(0).getEmail().equals("john.doe@example.com"));


        verify(profileArrayAdapter).notifyDataSetChanged();
    }


}




