package com.example.evenz;

import android.net.Uri;

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

    public void uploadProfilePicture(String filePath) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        StorageReference profilePicRef = storageRef.child("profilePic.jpg");
        //StorageReference profilePicImageRef = storageRef.child("images/profilePic.jpg");

        Uri file = Uri.fromFile(new File(filePath));
        UploadTask uploadTask = profilePicRef.putFile(file);

    }
}