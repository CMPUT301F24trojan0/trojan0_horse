package com.example.trojan0project.entrantUnitTests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class Entrant6GeneratedProfilePicture {

    @Test
    public void testNameInitials() {
        String name = "Gurleen";

        String[] parts = name.split(" ");
        StringBuilder initialsBuilder = new StringBuilder();
        for (String part : parts) {
            if (!part.isEmpty()) {
                initialsBuilder.append(part.charAt(0));
            }
        }
        String initials = initialsBuilder.toString().toUpperCase();

        assertEquals("G", initials);
    }

    @Test
    public void testEmptyNameInitials() {
        String name = "";

        String[] parts = name.split(" ");
        StringBuilder initialsBuilder = new StringBuilder();
        for (String part : parts) {
            if (!part.isEmpty()) {
                initialsBuilder.append(part.charAt(0));
            }
        }
        String initials = initialsBuilder.toString().toUpperCase();

        assertEquals("", initials);
    }
}
