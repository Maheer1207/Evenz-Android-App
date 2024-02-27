package com.example.evenz;

import java.util.ArrayList;

public class User
{
    private String name;
    private String profilePicID;
    private String phone;
    private String email;

    public User(String name, String profilePicID, String phone, String email)
    {
        this.name = name;
        this.profilePicID = profilePicID;
        this.phone = phone;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfilePicID() {
        return profilePicID;
    }

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
}