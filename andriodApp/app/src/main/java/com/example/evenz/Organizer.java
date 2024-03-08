package com.example.evenz;
/**
 * Represents an organizer user, extending the base User class.
 * An organizer is a user with additional properties related to organizing events.
 *
 * <p>The Organizer class includes an array of event IDs associated with events organized by the user.
 * This class inherits properties such as name, profile picture ID, phone, email, user ID, and user type
 * from the base User class.
 *
 * @see User
 */
public class Organizer extends User
{
    /**
     * An array containing the IDs of events organized by the user.
     */
    private int[] eventList;
    /**
     * Constructs a new Organizer with the specified details.
     *
     * @param name         The name of the organizer.
     * @param profilePicID The profile picture ID of the organizer.
     * @param phone        The phone number of the organizer.
     * @param email        The email address of the organizer.
     * @param userID       The unique user ID of the organizer.
     * @param userType     The type of user, in this case, "organizer".
     */
    public Organizer(String name, String profilePicID, String phone, String email, String userID, String userType) {
        super(name, profilePicID, phone, email, userID, userType);
    }
}