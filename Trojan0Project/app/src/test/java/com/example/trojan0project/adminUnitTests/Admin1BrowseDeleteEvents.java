package com.example.trojan0project.adminUnitTests;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.*;

import com.example.trojan0project.Model.Event;
import com.example.trojan0project.Controller.Admin.EventActivity;

import java.util.ArrayList;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28, manifest = Config.NONE)
public class Admin1BrowseDeleteEvents {

    private EventActivity eventActivity;
    private ArrayList<Event> mockEventList;

    @Before
    public void setup() {
        eventActivity = Mockito.spy(new EventActivity());

        mockEventList = new ArrayList<>();
        eventActivity.dataList = mockEventList;
    }

    @Test
    public void testAddEventToList() {
        Event event = new Event("Sample Event", null);

        eventActivity.dataList.add(event);

        assertEquals(1, eventActivity.dataList.size());
        assertTrue(eventActivity.dataList.contains(event));
    }

    @Test
    public void testRemoveEventFromList() {
        Event event = new Event("Sample Event", null);
        eventActivity.dataList.add(event);

        eventActivity.dataList.remove(event);

        assertEquals(0, eventActivity.dataList.size());
        assertFalse(eventActivity.dataList.contains(event));
    }

}