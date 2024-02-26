package com.example.evenz;

public class User
{
    private String userID;
    private String name;
    private String profilePicID;
    private String phone;
    private String email;

    public User(String userID, String name, String profilePicID, String phone, String email)
    {
        this.userID = userID;
        this.name = name;
        this.profilePicID = profilePicID;
        this.phone = phone;
        this.email = email;
    }
}