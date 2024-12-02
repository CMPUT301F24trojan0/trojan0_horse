package com.example.trojan0project.organizerUnitTests;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class organizer2ViewFinalEntrants {

    private List<String> entrantsList;

    @Before
    public void setup() {
        // Initialize the entrants list
        entrantsList = new ArrayList<>();
    }

    @Test
    public void testAddEntrantToList() {
        // Add some mock entrants to the list
        entrantsList.add("John Doe");
        entrantsList.add("Jane Doe");

        // Verify the list contains the entrants
        assertEquals(2, entrantsList.size());
        assertEquals("John Doe", entrantsList.get(0));
        assertEquals("Jane Doe", entrantsList.get(1));
    }


}
















