package com.example.evenz;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

public class AttendeeAdapterTest {
    @Test
    public void testAttendeeAdapter() {
        // Initialize a list of attendees
        List<Attendee> attendees = new ArrayList<>();
        // Add one attendee to the list
        attendees.add(new Attendee("John Doe", "profilePicID", "1234567890", "jhon@gmail.com", null, true, null));

        // Create a new attendee adapter with sample data and emails
        AttendeeAdapter attendeeAdapter = new AttendeeAdapter(attendees);
        // Test the attendee adapter data
        assertEquals(attendeeAdapter.getItemCount(), 1);
    }
}