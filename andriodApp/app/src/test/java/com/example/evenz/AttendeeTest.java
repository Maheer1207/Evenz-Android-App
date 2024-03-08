package com.example.evenz;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

/**
 * Unit tests for the Attendee class.
 */
public class AttendeeTest {

    private Attendee attendee;

    @Before
    public void setUp() {
        ArrayList<String> eventList = new ArrayList<>();
        eventList.add("Event1");
        eventList.add("Event2");
        attendee = new Attendee("John Doe", "profilePicID", "1234567890", "johndoe@example.com", null, true, eventList);
    }

    @Test
    public void testGetName() {
        assertEquals("John Doe", attendee.getName());
    }

    @Test
    public void testGetProfilePicID() {
        assertEquals("profilePicID", attendee.getProfilePicID());
    }

    @Test
    public void testGetPhone() {
        assertEquals("1234567890", attendee.getPhone());
    }

    @Test
    public void testGetEmail() {
        assertEquals("johndoe@example.com", attendee.getEmail());
    }

    @Test
    public void testIsNotifications() {
        assertTrue(attendee.isNotifications());
    }

    @Test
    public void testGetEventList() {
        ArrayList<String> eventList = attendee.getEventList();
        assertTrue(eventList.contains("Event1"));
        assertTrue(eventList.contains("Event2"));
    }
}