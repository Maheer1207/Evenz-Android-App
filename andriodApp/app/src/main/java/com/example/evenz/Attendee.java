package com.example.evenz;

public class Attendee extends User
{
    private Geolocation geolocation;
    private int[] eventList;
    private boolean notification;

    public Attendee(String userID, String name, String profilePicID, String phone, String email) {
        super(userID, name, profilePicID, phone, email);
    }
}