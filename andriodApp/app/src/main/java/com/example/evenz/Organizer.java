package com.example.evenz;

public class Organizer extends User
{
    private int[] eventList;

    public Organizer(String name, String profilePicID, String phone, String email) {
        super(name, profilePicID, phone, email);
    }
}