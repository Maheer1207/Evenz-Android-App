package com.example.evenz;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class eventUtilityTest {

    @Test
    public void testGetEventID(){
        String deviceID = "af29ad5875d972fd";
        String eventID = EventUtility.getEventID(deviceID);
        assertEquals("crOsZUHQgo9GUZLQcg6M", eventID);
    }
}
