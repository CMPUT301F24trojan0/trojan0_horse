package com.example.trojan0project.adminUnitTests;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.example.trojan0project.Model.Facility;
import com.example.trojan0project.Controller.Admin.FacilityActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28, manifest = Config.NONE)
public class Admin3BrowseDeleteFacilities {

    private FacilityActivity facilityActivity;
    private ArrayList<Facility> mockFacilityList;

    @Before
    public void setup() {
        // Create a partial mock of FacilityActivity
        facilityActivity = new FacilityActivity();

        // Initialize mock facility list
        mockFacilityList = new ArrayList<>();
        facilityActivity.dataList = mockFacilityList;
    }

    @Test
    public void testAddFacilityToList() {
        // Arrange
        Facility facility = new Facility("Swimming Pool");

        // Act
        facilityActivity.dataList.add(facility);

        // Assert
        assertEquals(1, facilityActivity.dataList.size());
        assertTrue(facilityActivity.dataList.contains(facility));
    }

    @Test
    public void testRemoveFacilityFromList() {
        // Arrange
        Facility facility = new Facility("Swimming Pool");
        facilityActivity.dataList.add(facility);

        // Act
        facilityActivity.dataList.remove(facility);

        // Assert
        assertEquals(0, facilityActivity.dataList.size());
        assertFalse(facilityActivity.dataList.contains(facility));
    }

}
