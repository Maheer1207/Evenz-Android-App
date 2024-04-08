package com.example.evenz;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.Date;

public class Event
{
    private String eventID;

    private String organizationName;
    private String eventName;
    private String eventPosterID;
    private String description;
    private Geolocation geolocation;
    private Bitmap qrCodeBrowse;
    private Bitmap qrCodeCheckIn;
    private int eventAttendLimit;
    private Date eventDate;
    private ArrayList<String> userList;
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
    public Event(String eventID, String organizationName, String eventName, String eventPosterID, String description,
                 Geolocation geolocation,Bitmap qrCodeBrowse, Bitmap qrCodeCheckIn, int eventAttendLimit,
                 ArrayList<String> userList, Date eventDate, ArrayList<String> notificationList, String location)
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
        this.eventID = eventID;

    }

    /**
     * gets the eventID
     * @return the eventID
     */
    public String getEventID() {
        return eventID;
    }

    /**
     * sets the eventID
     * @param eventID the eventID
     */
    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    /**
     * gets the org name
     * @return the org name
     */
    public String getOrganizationName() {
        return this.organizationName;
    }

    /**
     * sets the org name
     * @param organizationName the org name
     */
    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    /**
     * gets the events date
     * @return the date of the event
     */
    public Date getEventDate() {
        return this.eventDate;
    }

    /**
     * sets teh events date
     * @param eventDate the date of the event
     */
    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    /**
     * gets the events name
     * @return the name of the event
     */
    public String getEventName() {
        return this.eventName;
    }

    /**
     * sets the events name
     * @param eventName the name of the event
     */
    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    /**
     * gets the events poster image id
     * @return the events poster image id
     */
    public String getEventPosterID() {
        return eventPosterID;
    }

    /**
     * sets the events poster image id
     * @param eventPosterID the events poster image id
     */
    public void setEventPosterID(String eventPosterID) {
        this.eventPosterID = eventPosterID;
    }

    /**
     * gets the location of the event
     * @return the location of the event
     */
    public String getLocation() {
        return this.location;
    }

    /**
     * sets the location of the event
     * @param location the location of the event
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * gets the description of the event
     * @return the description of the event
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * sets the description of the event
     * @param description the description of the event
     */
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

    public ArrayList<String> getUserList() {
        return userList;
    }


    /**
     * This function returns the attendee list for the event
     * @return Returns the list in the format of an ArrayList of Pairs being <Attendee, check-in count>
     *     RIP
     */

    public void setUserList(ArrayList<String> userList) {
        this.userList = userList;
    }
}