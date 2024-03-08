package com.example.evenz;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

public class Event
{
    private String organizationName;
    private String eventName;
    private String eventPosterID;
    private String description;
    private Geolocation geolocation;
    private Bitmap qrCodeBrowse;
    private Bitmap qrCodeCheckIn;
    private int eventAttendLimit;
    private Date eventDate;
    private Map<String, Long> userList;
    private ArrayList<String> notifications;
    private String location;
    public Event(){

    }
    /**
     * This is the public constructor to create an event
     *
     * @param organizationName The name of the Organizer of the event
     * @param eventName          The name of the event
     * @param eventPosterID      The id for the event poster image
     * @param description        The description of the event
     * @param geolocation        The location of the event
     * @param qrCodeBrowse       The qrcode to browse the event
     * @param qrCodeCheckIn      The qrcode to check in to the event
     * @param eventAttendLimit   The limit of attendees in the event
     * @param userList           The list of users signed up to attend the event which is the number of times checked in (0 is rsvp) and the id of the user
     */
    public Event(String organizationName, String eventName, String eventPosterID, String description,
                 Geolocation geolocation,Bitmap qrCodeBrowse, Bitmap qrCodeCheckIn, int eventAttendLimit,
                 Map<String, Long> userList, Date eventDate, ArrayList<String> notificationList, String location)
    {
        this.eventName = eventName;
        this.eventPosterID = eventPosterID;
        this.description = description;
//        this.geolocation = geolocation;
        this.location = location;
        this.qrCodeBrowse = qrCodeBrowse;
        this.qrCodeCheckIn = qrCodeCheckIn;
        this.userList = userList;
        this.eventAttendLimit = eventAttendLimit;
        this.organizationName = organizationName;
        this.eventDate = eventDate;
        this.notifications = notificationList;

    }

    public String getOrganizationName() {
        return this.organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }
    public Date getEventDate() {
        return this.eventDate;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    public String getEventName() {
        return this.eventName;
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

    public String getLocation() {
        return this.location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Geolocation getGeolocation() {
        return this.geolocation;
    }

    public void setGeolocation(Geolocation geolocation) {
        this.geolocation = geolocation;
    }

    public Bitmap getQrCodeBrowse() {
        return this.qrCodeBrowse;
    }

    public void setQrCodeBrowse(Bitmap qrCodeBrowse) {
        this.qrCodeBrowse = qrCodeBrowse;
    }

    public Bitmap getQrCodeCheckIn() {
        return this.qrCodeCheckIn;
    }

    public void setQrCodeCheckIn(Bitmap qrCodeCheckIn) {
        this.qrCodeCheckIn = qrCodeCheckIn;
    }

    public long getEventAttendLimit() {
        return this.eventAttendLimit;
    }

    public int setEventAttendLimit(int eventAttendLimit) {
        return eventAttendLimit;
    }

    public ArrayList<String> getNotifications() {
        return notifications;
    }
    public void setNotifications(ArrayList<String> notifications) {
        this.notifications = notifications;
    }

    public Map<String, Long> getAttendeeIDList() {
        return userList;
    }



    /**
     * This function returns the attendee list for the event
     * @return Returns the list in the format of an ArrayList of Pairs being <Attendee, check-in count>
     *     RIP
     */


    public void setUserList(Map<String, Long> userList) {
        this.userList = userList;
    }
}