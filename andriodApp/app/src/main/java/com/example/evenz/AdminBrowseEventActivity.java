package com.example.evenz;


import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import android.content.Context;

import java.util.ArrayList;
import java.util.Hashtable;

//call ArrayList<Event> eventDataList

public class AdminBrowseEventActivity extends AppCompatActivity {
    private RecyclerView eventsRecyclerView;
    private EventAdapter eventAdapter;
    private ArrayList<Event> eventDataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_browse_users);

        eventsRecyclerView = findViewById(R.id.eventsRecyclerView22);

        eventsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize eventDataList
        eventDataList = new ArrayList<>();
        eventAdapter = new EventAdapter(this, eventDataList);
        eventsRecyclerView.setAdapter(eventAdapter);

        eventAdapter.setOnClickListener(new EventAdapter.OnClickListener() {
            @Override
            public void onClick(int position, Event model) {
                Log.d("Ben8888", "clicksuccess");
                Log.d("Ben8888", "clicked event: " + model.getEventName());

                Intent intent = new Intent(AdminBrowseEventActivity.this, AdminDeleteEvent.class);
                startActivity(intent);

            }
        });
        fetchEvents();

        findViewById(R.id.back_attendee_browse_events).setOnClickListener(v -> finish());
    }

    private void fetchEvents() {
        // Code to fetch events from Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("events").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshots, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e("Firestore", error.toString());
                    return;
                }

                if (querySnapshots != null) {
                    eventDataList.clear();
                    for (QueryDocumentSnapshot doc : querySnapshots) {
                        String eventID = doc.getId();
                        // Parse the event data and add it to your list, then notify the adapter
                        Event tempEvent = EventUtility.parseEvent(doc);

                        eventDataList.add(tempEvent);
                    }
                    eventAdapter.notifyDataSetChanged();
                }
            }
        });
    }


}


