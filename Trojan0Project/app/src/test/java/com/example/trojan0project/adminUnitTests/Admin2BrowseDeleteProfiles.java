package com.example.trojan0project.adminUnitTests;

import static org.junit.Assert.*;

import com.example.trojan0project.Model.Profile;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;


public class Admin2BrowseDeleteProfiles {

    private ArrayList<Profile> mockDataList;

    @Before
    public void setup() {
        mockDataList = new ArrayList<>();
    }

    @Test
    public void testAddProfileToList() {
        Profile profile = new Profile("User1", "Device1");

        mockDataList.add(profile);

        assertEquals(1, mockDataList.size());
        assertTrue(mockDataList.contains(profile));
    }

    @Test
    public void testRemoveProfileFromList() {
        Profile profile = new Profile("User1", "Device1");
        mockDataList.add(profile);

        mockDataList.remove(profile);

        assertEquals(0, mockDataList.size());
        assertFalse(mockDataList.contains(profile));
    }
}