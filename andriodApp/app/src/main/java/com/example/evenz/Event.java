package com.example.evenz;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;

public class Event
{
    private String eventID;
    private String eventName;
    private int eventPosterID;
    private String description;
    private Geolocation geolocation;
    private int qrCodeBrowse;
    private int qrCodeCheckIn;
    private Dictionary<String, Geolocation> userList;

    public Event(String eventID, String eventName)
    {
        this.eventID = eventID;
        this.eventName = eventName;
    }
}