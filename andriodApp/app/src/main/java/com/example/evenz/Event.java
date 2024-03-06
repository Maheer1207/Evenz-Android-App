package com.example.evenz;

import static android.content.ContentValues.TAG;

import android.graphics.Bitmap;
import android.util.Log;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class Event
{
    private String eventName;
    private String eventPosterID;
    private String description;
    private Geolocation geolocation;
    private Bitmap qrCodeBrowse;
    private Bitmap qrCodeCheckIn;
    private Map<String, Integer> userList;

    /**
     * This is the public constructor to create an event
     * @param eventName The name of the event
     * @param eventPosterID The id for the event poster image
     * @param description The description of the event
     * @param geolocation The location of the event
     * @param qrCodeBrowse The qrcode to browse the event
     * @param qrCodeCheckIn The qrcode to check in to the event
     * @param userList The list of users signed up to attend the event which is the number of times checked in (0 is rsvp) and the id of the user
     */
    public Event(String eventName, String eventPosterID, String description, Geolocation geolocation, Bitmap qrCodeBrowse, Bitmap qrCodeCheckIn, Map<String, Integer> userList)
    {
        this.eventName = eventName;
        this.eventPosterID = eventPosterID;
        this.description = description;
        this.geolocation = geolocation;
        this.qrCodeBrowse = qrCodeBrowse;
        this.qrCodeCheckIn = qrCodeCheckIn;
        this.userList = userList;
    }

    public String getEventName() {
        return eventName;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Geolocation getGeolocation() {
        return geolocation;
    }

    public void setGeolocation(Geolocation geolocation) {
        this.geolocation = geolocation;
    }

    public Bitmap getQrCodeBrowse() {
        return qrCodeBrowse;
    }

    public void setQrCodeBrowse(Bitmap qrCodeBrowse) {
        this.qrCodeBrowse = qrCodeBrowse;
    }

    public Bitmap getQrCodeCheckIn() {
        return qrCodeCheckIn;
    }

    public void setQrCodeCheckIn(Bitmap qrCodeCheckIn) {
        this.qrCodeCheckIn = qrCodeCheckIn;
    }

    public Map<String, Integer> getAttendeeIDList() {
        return userList;
    }

    /**
     * This function returns the attendee list for the event
     * @return Returns the list in the format of an ArrayList of Pairs being <Attendee, check-in count>
     */
    public ArrayList<Pair<Attendee, Integer>> getAttendeeList() {
        ArrayList<Pair<Attendee, Integer>> attendees = new ArrayList<Pair<Attendee, Integer>>();
        Enumeration<String> enu = Collections.enumeration(userList.keySet());
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // go through every attendee pulled from the event attendee list
        String tempID;
        while (enu.hasMoreElements())
        {
            // get the next atendee and find refrence on firebase
            tempID = enu.nextElement();
            DocumentReference docRef = db.collection("users").document(tempID);
            String finalTempID = tempID;
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            // gets the attendee and the number of times they have checked into the event
                            attendees.add(new Pair<Attendee, Integer>((Attendee)(document.get(finalTempID)), userList.get(finalTempID)));
                            Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        } else {
                            Log.d(TAG, "No such document");
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });

        }

        return attendees;
    }

    public void setUserList(Map<String, Integer> userList) {
        this.userList = userList;
    }
}