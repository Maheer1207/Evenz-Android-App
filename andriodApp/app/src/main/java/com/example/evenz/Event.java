package com.example.evenz;

import java.util.ArrayList;
import javafx.util.Pair;

public class Event
{
    private int eventID;
    private String eventName;
    private int eventPosterID;
    private String description;
    private Geolocation geolocation;
    private int qrCodeBrowse;
    private int qrCodeCheckIn;
    private ArrayList<Pair<int, Geolocation>> userList;

    public Event(int eventID, String eventName)
    {
        this.eventID = eventID;
        this.eventName = eventName;
    }
}