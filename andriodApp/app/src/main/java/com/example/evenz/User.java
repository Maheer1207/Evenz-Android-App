package com.example.evenz;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class User
{
    private String userID;
    private String userType;
    private String name;
    private String profilePicID;
    private String phone;
    private String email;

    public User(String userID, String userType, String name, String profilePicID, String phone, String email)
    {
        this.userID = userID;
        this.userType = userType;
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