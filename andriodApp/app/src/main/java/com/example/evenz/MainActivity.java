package com.example.evenz;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import java.util.ArrayList;

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

        evetnsRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
        @Override
        public void onEvent(@Nullable QuerySnapshot querySnapshots,
                            @Nullable FirebaseFirestoreException error) {
            if (error != null) {
                Log.e("Firestore", error.toString());
                return;
            }
            if (querySnapshots != null) {
                cityDataList.clear();
                for (QueryDocumentSnapshot doc: querySnapshots) {
                    int eventID = doc.getId();
                    String eventName = doc.getString("eventName");
                    Log.d("Firestore", String.format("Event(%d, %s) fetched", eventID, eventName));
                    eventDataList.add(new Event(eventID, eventName));
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
                cityDataList.clear();
                for (QueryDocumentSnapshot doc: querySnapshots) {
                    int userID = doc.getId();
                    String name = doc.getString("name");
                    Log.d("Firestore", String.format("User(%d, %s) fetched", userID, name));
                    userDataList.add(new User(userID, name));
                }
            }
        }
        });
    }
}