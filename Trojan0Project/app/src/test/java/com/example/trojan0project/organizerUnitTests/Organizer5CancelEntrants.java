package com.example.trojan0project.organizerUnitTests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.Calendar;
import java.util.Date;

public class Organizer5CancelEntrants {

    @Test
    public void testShouldCancel() {
        Calendar pastCalendar = Calendar.getInstance();
        pastCalendar.set(2023, Calendar.JANUARY, 1);
        Date pastDeadline = pastCalendar.getTime();

        Calendar futureCalendar = Calendar.getInstance();
        futureCalendar.set(2025, Calendar.JANUARY, 1);
        Date futureDeadline = futureCalendar.getTime();

        Calendar currentCalendar = Calendar.getInstance();
        currentCalendar.set(2024, Calendar.JANUARY, 1);
        Date currentDate = currentCalendar.getTime();

        assertTrue("Should cancel for past deadline", currentDate.after(pastDeadline));
        assertFalse("Should not cancel for future deadline", currentDate.after(futureDeadline));
    }

}
