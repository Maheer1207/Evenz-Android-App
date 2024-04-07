package com.example.evenz;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.evenz.Event;
import com.example.evenz.EventAdapter;
import com.example.evenz.EventUtility;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class EventSignedUpForAttendeeBrowse extends AppCompatActivity {
    private RecyclerView eventsRecyclerView;
    private EventAdapter eventAdapter;
    private ArrayList<Event> eventDataList;
    private FirebaseFirestore db;
    private String role;
    private String eventID;
    private FirebaseUserManager firebaseUserManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendees_browse_signedup_events);

        eventsRecyclerView = findViewById(R.id.eventsRecyclerView);
        eventsRecyclerView.setLayoutManager(new LinearLayoutManager(this));


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            eventID = extras.getString("eventID");
            role = extras.getString("role");
        }

        // Initialize eventDataList
        eventDataList = new ArrayList<>();
        eventAdapter = new EventAdapter(this, eventDataList);
        eventsRecyclerView.setAdapter(eventAdapter);

        db = FirebaseFirestore.getInstance();
        firebaseUserManager = new FirebaseUserManager(db);

        String userId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        firebaseUserManager.getEventsSignedUpForUser(userId).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<String> eventIds = task.getResult();
                if (eventIds != null && !eventIds.isEmpty()) {
                    // Call the new method that handles fetching by document IDs
                    EventUtility.fetchEventsByIds(db, eventIds, eventDataList, eventAdapter);
                } else {
                    // Handle the case where there are no event IDs (e.g., user hasn't signed up for any events)
                    Log.d("Firestore", "User hasn't signed up for any events or eventIds list is null");
                }
            } else {
                Log.e("Firestore", "Error getting signed up events", task.getException());
            }
        });


        findViewById(R.id.back_attendee_browse_events).setOnClickListener(v -> finish());

        RelativeLayout lightGreenButton = findViewById(R.id.light_green_signup_button);
        lightGreenButton.setOnClickListener(v -> {
            Log.d("Button Click", "Button was clicked");
            Intent intent = new Intent(EventSignedUpForAttendeeBrowse.this, EventBrowseActivity.class);
            intent.putExtra("role", role);
            intent.putExtra("eventID", eventID);
            startActivity(intent);
        });
    }
}