package com.example.evenz;

public class Organizer extends User
{
    private int[] eventList;

    public Organizer(String userID, String userType, String name, String profilePicID, String phone, String email) {
        super(userID, userType, name, profilePicID, phone, email);
    }
}