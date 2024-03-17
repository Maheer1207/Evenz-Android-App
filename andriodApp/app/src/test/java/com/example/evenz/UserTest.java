package com.example.evenz;
// Create a junit test for the User class
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.util.ArrayList;
public class UserTest {
    @Test
    public void testUser() {
        // Create a new user with devie id name phone email
        User user = new User("deviceID", "John Doe", "1234567890", "jhon@gmail.com");
        // Test the user name
        assertEquals("John Doe", user.getName());
        // Test the user phone
        assertEquals("1234567890", user.getPhone());
        // Test the user email
        assertEquals("jhon@gmail.com", user.getEmail());
    }
}