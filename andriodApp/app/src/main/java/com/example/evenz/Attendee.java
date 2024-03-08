package com.example.evenz;

import java.util.ArrayList;

public class Attendee extends User
{
    private Geolocation geolocation;
    private boolean notifications;
    private ArrayList<String> eventList;

    public Attendee(String userID, String userType, String name, String profilePicID, String phone, String email, Geolocation geolocation, boolean notifications, ArrayList<String> eventList) {
        super(userID, userType, name, profilePicID, phone, email);
        this.geolocation = geolocation;
        this.notifications = notifications;
        this.eventList.addAll(eventList);
    }
}