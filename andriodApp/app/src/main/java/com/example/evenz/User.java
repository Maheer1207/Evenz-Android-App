package com.example.evenz;

import java.util.ArrayList;
/**
 * The User class represents a user in the application with basic personal information.
 * It includes properties such as name, profile picture ID, phone number, email, user ID, and user type.
 * This class is designed to be used for Firebase data mapping and contains default and parameterized constructors,
 * along with getter and setter methods for each property.
 */
public class User
{
    private String name;
    private String profilePicID;
    private String phone;
    private String email;

    private String userId;

    private String userType;

    // Default constructor required for calls to DataSnapshot.getValue(User.class)
    public User() {
    }

    /**
     * Parameterized constructor to create a User instance with specified attributes.
     *
     * @param name         The name of the user.
     * @param profilePicID The ID associated with the user's profile picture.
     * @param phone        The phone number of the user.
     * @param email        The email address of the user.
     * @param userId       The unique identifier for the user.
     * @param userType     The type of the user (e.g., "organizer" or "attendee").
     */
    public User(String name, String profilePicID, String phone, String email, String userId, String userType)
    {
        this.name = name;
        this.profilePicID = profilePicID;
        this.phone = phone;
        this.email = email;
        this.userId = userId;
        this.userType = userType;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfilePicID() {
        return profilePicID;
    }

    public void setProfilePicID(String profilePicID) {
        this.profilePicID = profilePicID;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }
}