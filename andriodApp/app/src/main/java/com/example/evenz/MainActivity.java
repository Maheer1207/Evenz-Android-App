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
                userDataList.clear();
                for (QueryDocumentSnapshot doc: querySnapshots) {
                    String userID = doc.getId();
                    String name = doc.getString("name");
                    String profilePicID = doc.getString("profilePicID");
                    String email = doc.getString("profilePicID");
                    String phone = doc.getString("profilePicID");
                    Log.d("Firestore", String.format("User(%s, %s, %s, %s, %s) fetched", userID, name, profilePicID, email, phone));
                    userDataList.add(new User(userID, name, profilePicID, email, phone));
                }
            }
        }
        });
    }
}