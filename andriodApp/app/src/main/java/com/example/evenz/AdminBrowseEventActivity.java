package com.example.evenz;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * AdminBrowseEventActivity is a Java class responsible for the activity allowing for the Administrator
 * to view and browse the list of Events, and select items for more detailed views, or deletion,
 * which sends UI to AdminDeleteEvent.
 * no constructors or methods due to being an activity class, utilizing only onCreate() to define activity behavior.
 * Utilizes EventRecyclerView to display the list, evenAdapter to load data into it.
 * utilizes onclicklistener implemented in the EventAdapter for
 */
public class AdminBrowseEventActivity extends AppCompatActivity {
    private RecyclerView eventsRecyclerView;
    private EventAdapter eventAdapter;
    private ArrayList<Event> eventDataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_browse_event);

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
                Bundle rb = getIntent().getExtras();

                Bundle b = new Bundle();
                b.putString("organizationame",  model.getOrganizationName());
                b.putString("eventName", model.getEventName());
                b.putString("eventpostID", model.getEventPosterID());
                b.putString("eventDesc", model.getDescription());
                b.putString("eventDate", model.getEventDate().toString());
                b.putString("location", model.getLocation());
                b.putString("imgID", model.getEventPosterID());

                intent.putExtras(b);

                startActivity(intent);

            }
        });
        fetchEvents();
        findViewById(R.id.photo_browse).setOnClickListener(v -> startActivity(new Intent(AdminBrowseEventActivity.this, ImageBrowseActivity.class)));
        findViewById(R.id.profile_browse).setOnClickListener(v -> startActivity(new Intent(AdminBrowseEventActivity.this, AdminBrowseProfilesActivity.class)));
        findViewById(R.id.back_attendee_browse_events).setOnClickListener(v -> finish());
    }

    /**
     * fetchEvents is a function that fetches all documents in the "events" collection
     * from firebase, converts each document into an event-object.
     * it in an array of Event Objects
     * . Here function is defined statically and takes and returns the array implicitly.
     */
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