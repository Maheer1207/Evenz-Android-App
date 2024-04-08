package com.example.evenz;


import android.content.Intent;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;


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


//call ArrayList<Event> eventDataList

/**
 * Activity Class for attendees to browse the list of current Events, and select events for more detailed information
 * contains fields of the eventsRecyclerView, eventAdapter, and eventDataList.
 * the UI view the events are displayed in, the adapter, and the arrayList of event objects to be displayed.
 */
public class EventBrowseActivity extends AppCompatActivity {
    private RecyclerView eventsRecyclerView;
    private EventAdapter eventAdapter;
    private ArrayList<Event> eventDataList;

    /**
     * code run on the creation of the activity, and performs all action associated with the activity.
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendees_browse_events);

        eventsRecyclerView = findViewById(R.id.eventsRecyclerView);

        eventsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String eventID = extras.getString("eventID");
            String role = extras.getString("role");
        }
        // Initialize eventDataList
        eventDataList = new ArrayList<>();
        eventAdapter = new EventAdapter(this, eventDataList);
        eventsRecyclerView.setAdapter(eventAdapter);
        eventAdapter.setOnClickListener(new EventAdapter.OnClickListener() {
            @Override
            public void onClick(int position, Event model) {
                Intent intent = new Intent(EventBrowseActivity.this, EventDetailsActivity.class);
                intent.putExtra("eventID", model.getEventID());
                intent.putExtra("source","browse");
                intent.putExtra("role", "attendee"); // TODO: This is hardcoded for now, but you can pass the role from the previous activity
                startActivity(intent);
            }
        });
        fetchEvents();

        findViewById(R.id.home_admin_browse_attendee).setOnClickListener (
                v-> startActivity(new Intent(EventBrowseActivity.this, HomeScreenActivity.class))
        );

        ImageView profileAttendee = findViewById(R.id.profile_attendee);
        profileAttendee.setOnClickListener(v -> {
            Intent intent = new Intent(EventBrowseActivity.this, UserEditProfileActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("role", "attendee");
            intent.putExtras(bundle);
            startActivity(intent);
        });

        findViewById(R.id.back_attendee_browse_events).setOnClickListener(v -> finish());
    }

    /**
     * Fetches all events in the events collection from Firebase, converts them into objects, and stores them
     * in an implicitly provided arraylist
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


