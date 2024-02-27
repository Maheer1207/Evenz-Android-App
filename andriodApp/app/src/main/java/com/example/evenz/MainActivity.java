package com.example.evenz;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private CollectionReference eventsRef;
    private CollectionReference usersRef;

    private ArrayList<Event> eventDataList;
    private ArrayList<User> userDataList;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                        Event tempEvent = new Event(doc.getString("eventName"), doc.getString("eventPosterID"), doc.getString("description"), (Geolocation)doc.get("geolocation"), (int)doc.get("qrCodeBrowse"), (int)doc.get("qrCodeCheckIn"), (Dictionary<String, Geolocation>)doc.get("userList"));
                        Log.d("Firestore", String.format("Event(%d, %s) fetched", eventID, tempEvent.getEventName()));
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
                        User tempUser = new User(doc.getString("name"), doc.getString("profilePicID"), doc.getString("phone"), doc.getString("email"));
                        userDataList.add(tempUser);
                    }
                }
            }
        });
    }

    private void addUser(String id, User user)
    {
        HashMap<String, User> data = new HashMap<>();
        data.put(id, user);
        usersRef.document(id).set(data);
    }

    private void deleteUser(String id)
    {
        usersRef.document(id).delete();
    }

    private void addEvent(String id, Event event)
    {
        HashMap<String, Event> data = new HashMap<>();
        data.put(id, event);
        eventsRef.document(id).set(data);
    }

    private void deleteEvent(String id)
    {
        usersRef.document(id).delete();
    }
}