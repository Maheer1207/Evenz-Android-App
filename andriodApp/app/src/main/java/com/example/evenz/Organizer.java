package com.example.evenz;

public class Organizer extends User
{
    private int[] eventList;

    public Organizer(String name, String profilePicID, String phone, String email, String userID, String userType) {
        super(name, profilePicID, phone, email, userID, userType);
    }
}