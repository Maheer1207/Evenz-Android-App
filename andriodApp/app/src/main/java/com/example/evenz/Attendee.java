package com.example.evenz;

import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;

public class Attendee {
    String name;
    String phone;
    String email;
    int checkIns;
    Drawable profilePicture;

    // Constructor, getters, and setters
    public Attendee(String name, String phone, String email, int checkIns, Drawable profilePicture) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.checkIns = checkIns;
        this.profilePicture = profilePicture;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

public int getCheckIns() {
        return checkIns;
    }

    public void setCheckIns(int checkIns) {
        this.checkIns = checkIns;
    }

    public Drawable getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(Drawable profilePicture) {
        this.profilePicture = profilePicture;
    }

    public void checkIn() {
        checkIns++;
    }

    public void checkOut() {
        checkIns--;
    }

    public void resetCheckIns() {
        checkIns = 0;
    }

    public void updateProfilePicture(Drawable newProfilePicture) {
        profilePicture = newProfilePicture;
    }

    @NonNull
    public String toString() {
        return name + " " + phone + " " + email + " " + checkIns;
    }

    public boolean equals(Attendee other) {
        return name.equals(other.name) && phone.equals(other.phone) && email.equals(other.email);
    }


}
