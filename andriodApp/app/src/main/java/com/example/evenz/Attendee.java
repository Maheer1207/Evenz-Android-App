package com.example.evenz;


import java.util.ArrayList;


public class Attendee extends User
{
    private Geolocation geolocation;
    private boolean notifications;
    private String eventId;

    public Attendee(String name, String profilePicID, String phone, String email, String eventI ) {
        this(name, profilePicID, phone, email, null, false );
    }

    public Attendee(String name, String profilePicID, String phone, String email, Geolocation geolocation, boolean notifications, String eventId) {
        super(name, profilePicID, phone, email);
        this.geolocation = geolocation;
        this.notifications = notifications;
        this.eventId = eventId;
    }

    // existing methods...


    public Geolocation getGeolocation() {
        return geolocation;
    }

    public void setGeolocation(Geolocation geolocation) {
        this.geolocation = geolocation;
    }

    public boolean isNotifications() {
        return notifications;
    }

    public void setNotifications(boolean notifications) {
        this.notifications = notifications;
    }


    public String getEventId()
    {
        return eventId;
    }

    public void setEventId(String eventId)
    {
        this.eventId = eventId;
    }

}
