package com.example.evenz;
// Create a junit test for the Geolocation class
import org.junit.Test;
import static org.junit.Assert.assertEquals;
public class GeolocationTest {
    @Test
    public void testGeolocation() {
        // Create a new geolocation
        Geolocation geolocation = new Geolocation("TestGeolocation", 3, 2);
        // Test the geolocation ID
        assertEquals("TestGeolocation", geolocation.getGeolocationID());
        // Test the geolocation x coordinate
        assertEquals(3, geolocation.getXcoord(), 3.0);
        // Test the geolocation y coordinate
        assertEquals(2, geolocation.getYcoord(), 2.0);
    }
}
