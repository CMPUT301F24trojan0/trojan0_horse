package com.example.trojan0project.adminUnitTests;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import android.content.Context;
import android.widget.ListView;

import com.example.trojan0project.BrowseProfileAdmin;
import com.example.trojan0project.Profile;
import com.example.trojan0project.ProfileAdapter;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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