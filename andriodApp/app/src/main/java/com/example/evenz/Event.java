package com.example.evenz;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;

public class Event
{
    private String eventName;
    private String eventPosterID;
    private String description;
    private Geolocation geolocation;
    private int qrCodeBrowse;
    private int qrCodeCheckIn;
    private Dictionary<String, Geolocation> userList;

    public Event(String eventName, String eventPosterID, String description, Geolocation geolocation, int qrCodeBrowse, int qrCodeCheckIn, Dictionary<String, Geolocation> userList)
    {
        this.eventName = eventName;
        this.eventPosterID = eventPosterID;
        this.description = description;
        this.geolocation = geolocation;
        this.qrCodeBrowse = qrCodeBrowse;
        this.qrCodeCheckIn = qrCodeCheckIn;
        this.userList = userList;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventPosterID() {
        return eventPosterID;
    }

    public void setEventPosterID(String eventPosterID) {
        this.eventPosterID = eventPosterID;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Geolocation getGeolocation() {
        return geolocation;
    }

    public void setGeolocation(Geolocation geolocation) {
        this.geolocation = geolocation;
    }

    public int getQrCodeBrowse() {
        return qrCodeBrowse;
    }

    public void setQrCodeBrowse(int qrCodeBrowse) {
        this.qrCodeBrowse = qrCodeBrowse;
    }

    public int getQrCodeCheckIn() {
        return qrCodeCheckIn;
    }

    public void setQrCodeCheckIn(int qrCodeCheckIn) {
        this.qrCodeCheckIn = qrCodeCheckIn;
    }

    public Dictionary<String, Geolocation> getUserList() {
        return userList;
    }

    public void setUserList(Dictionary<String, Geolocation> userList) {
        this.userList = userList;
    }
}