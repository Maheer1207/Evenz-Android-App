package com.example.evenz;

public class Attendee {
    private final String name;
    private final String phoneNumber;
    private final String email;
    private final int profileImageResource;

    public Attendee(String name, String phoneNumber, String email, int profileImageResource) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.profileImageResource = profileImageResource;
    }

    // Getters
    public String getName() { return name; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getEmail() { return email; }
    public int getProfileImageResource() { return profileImageResource; }
}
