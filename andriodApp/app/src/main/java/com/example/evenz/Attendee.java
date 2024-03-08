package com.example.evenz;

import java.util.ArrayList;

public class Attendee extends User
{
    private Geolocation geolocation;
    private boolean notifications;

    public Attendee(String userID, String userType, String name, String profilePicID, String phone, String email, Geolocation geolocation, boolean notifications) {
        super(userID, userType, name, profilePicID, phone, email);
        this.geolocation = geolocation;
        this.notifications = notifications;
    }
}