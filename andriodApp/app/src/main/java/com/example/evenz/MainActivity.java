package com.example.evenz;

import androidx.annotation.Nullable;
import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.util.UnstableApi;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;

public class MainActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private CollectionReference eventsRef;
    private CollectionReference usersRef;

    private ArrayList<Event> eventDataList;
    private ArrayList<User> userDataList;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        eventDataList = new ArrayList<>();
        userDataList = new ArrayList<>();
        
        db = FirebaseFirestore.getInstance();
        eventsRef = db.collection("events");
        usersRef = db.collection("users");


        eventsRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshots,
                                @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e("Firestore", error.toString());
                    return;
                }

                if (querySnapshots != null) {
                    eventDataList.clear();
                    for (QueryDocumentSnapshot doc: querySnapshots) {
                        String eventID = doc.getId();

                        Object attendLimitObj = doc.get("AttendLimit");
                        long eventAttendLimit = attendLimitObj != null ? (Long) attendLimitObj : 0; // 0 is a default value
                        
                        // Get the Timestamp object from the document
                        Timestamp timestamp = doc.getTimestamp("eventDate");

                        //Convert the Timestamp to a java.util.Date object
                        Date eventDate = null;
                        if (timestamp != null) {
                            eventDate = timestamp.toDate(); // converts Timestamp to Date
                        }

                        Event tempEvent = new Event(doc.getString("eventID"), doc.getString("organizationName"), doc.getString("eventName"), doc.getString("eventPosterID"),
                                doc.getString("description"), (Geolocation)doc.get("geolocation"), (Bitmap)doc.get("qrCodeBrowse"),
                                (Bitmap)doc.get("qrCodeIn"), (int)eventAttendLimit,
                                new ArrayList<>(), eventDate, new ArrayList<String>(), doc.getString("location"));

                        Log.d("Firestore", String.format("Event(%s, %s) fetched", eventID, tempEvent.getEventName()));
                        eventDataList.add(tempEvent);
                    }
                }
            }
        });


        usersRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshots,
                                @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e("Firestore", error.toString());
                    return;
                }
                if (querySnapshots != null) {
                    userDataList.clear();
                    for (QueryDocumentSnapshot doc: querySnapshots) {
                        String userID = doc.getId();
                        User tempUser = new User(doc.getString("name"), doc.getString("profilePicID"),
                                doc.getString("phone"), doc.getString("email"), doc.getString("userId"), doc.getString("userType"), doc.getBoolean("notificationEnabled"), doc.getBoolean("locationEnabled"));
                        userDataList.add(tempUser);
                    }
                }
            }
        });
    }


    /**
     * This function adds a user to the database
     * @param id The id of the user
     * @param user The contents of the user (all variables from user class)
     */
    private void addUser(String id, User user) {
        HashMap<String, User> data = new HashMap<>();
        data.put(id, user);
        usersRef.document(id).set(data);
    }

    /**
     * This function deletes a user from the database
     * @param id The id of the user to be deleted
     */
    private void deleteUser(String id)
    {
        usersRef.document(id).delete();
    }

    /**
     * This function adds an event to the database
     * @param id The id of the event to be added
     * @param event The contents of the event (all variables from event class)
     */
    private void addEvent(String id, Event event) {
        HashMap<String, Event> data = new HashMap<>();
        data.put(id, event);
        eventsRef.document(id).set(data);
    }

    /**
     * This function deletes an event from the database
     * @param id The id of the event to be deleted
     */
    private void deleteEvent(String id)
    {
        usersRef.document(id).delete();
    }

}