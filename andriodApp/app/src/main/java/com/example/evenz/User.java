package com.example.evenz;

import java.util.ArrayList;

public class User
{
    private String name;
    private String profilePicID;
    private String phone;
    private String email;
    private String userId;
    private String userType;

    private Boolean notificationsEnabled;
    private Boolean locationEnabled;

    // Should be empty initally
    private ArrayList<String> eventsSignedUpFor = new ArrayList<>();

    private String checkedInEvent = null;

    // Defullt constructor
    public User() {}
    // ATTENDEE CONSTRUCTOR
    public User(String userId, String name, String phone, String email, String profilePicID, String userType, Boolean notificationsEnabled, Boolean locationEnabled)
    {
        this.name = name;
        this.profilePicID = profilePicID;
        this.phone = phone;
        this.email = email;
        this.userId = userId;
        this.userType = userType;
        this.notificationsEnabled = notificationsEnabled;
        this.locationEnabled = locationEnabled;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {this.name = name;}

    public String getProfilePicID() {return profilePicID;}

    public void setProfilePicID(String profilePicID) {
        this.profilePicID = profilePicID;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserType() {
        return userType;
    }


    public ArrayList<String> getEventsSignedUpFor() {
        return eventsSignedUpFor;
    }


    public void setEventsSignedUpFor(ArrayList<String> eventsSignedUpFor) {
        this.eventsSignedUpFor = eventsSignedUpFor;
    }

    public String getCheckedInEvent() {
        return checkedInEvent;
    }

    public void setCheckedInEvent(String checkedInEvent) {
        this.checkedInEvent = checkedInEvent;
    }

    //Add an event to the list of events signed up for
    public void addEvent(String eventID) {
        eventsSignedUpFor.add(eventID);
    }

    // Remove an event from the list of events signed up for
    public void removeEvent(String eventID) {
        eventsSignedUpFor.remove(eventID);
    }


    public Boolean getNotificationsEnabled() {
        return notificationsEnabled;
    }

    public void setNotificationsEnabled(Boolean notificationsEnabled) {
        this.notificationsEnabled = notificationsEnabled;
    }

    public Boolean getLocationEnabled() {
        return locationEnabled;
    }

    public void setLocationEnabled(Boolean locationEnabled) {
        this.locationEnabled = locationEnabled;
    }



    public void setUserType(String userType) {
        this.userType = userType;
    }
}

