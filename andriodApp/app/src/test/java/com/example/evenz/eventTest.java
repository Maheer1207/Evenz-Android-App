package com.example.evenz;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class eventTest {
    @Test
    public void testEvent() {
        // Create a new event
        Event event = new Event("TestOrg", "TestEvent", "TestPosterID", "TestDescription", new Geolocation("mexico", 3, 2), null, null, 10, null, null);
        // Test the event name
        assertEquals("TestEvent", event.getEventName());
        // Test the event poster ID
        assertEquals("TestPosterID", event.getEventPosterID());
        // Test the event description
        assertEquals("TestDescription", event.getDescription());
        // Test the event geolocation
        assertEquals(0, event.getGeolocation().getXcoord(), 3.0);
        assertEquals(0, event.getGeolocation().getYcoord(), 2.0);
        // Test the event QR code browse
        assertEquals(null, event.getQrCodeBrowse());
        // Test the event QR code check in
        assertEquals(null, event.getQrCodeCheckIn());
        // Test the event attendee limit
        assertEquals(10, event.getEventAttendLimit());
        // Test the event user list
        assertEquals(null, event.getAttendeeIDList());
    }
}