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

import java.util.ArrayList;
import java.util.Hashtable;

//call ArrayList<Event> eventDataList
/**
 * The {@code EventBrowseActivity} class represents an activity in the application for attendees to
 * browse and view a list of events. It extends the {@code AppCompatActivity} class and facilitates
 * the display of events in a RecyclerView, using the {@code EventAdapter} to manage the UI.
 *
 * <p>The activity includes the layout defined in the {@code attendees_browse_events.xml} file,
 * which contains a RecyclerView to display the list of events. Events are fetched from Firestore
 * using the {@code fetchEvents} method, and the data is displayed with the help of the
 * {@code EventAdapter}.
 *
 * <p>The "back" button is set up to finish the activity when clicked, returning to the previous
 * screen. Events are asynchronously loaded from Firestore and displayed using the
 * {@code EventAdapter}, which handles the complexity of loading event poster images from Firebase
 * Storage.
 *
 * <p>This class is part of the attendee module and contributes to the user interface and interaction
 * for event browsing in the attendee section of the application.
 *
 * @author hrithick
 * @version 1.0
 * @see AppCompatActivity
 * @see EventAdapter
 */
public class EventBrowseActivity extends AppCompatActivity {
    private RecyclerView eventsRecyclerView;
    private EventAdapter eventAdapter;
    private ArrayList<Event> eventDataList;
    /**
     * Called when the activity is first created. Responsible for initializing the activity, setting up
     * the layout, configuring the RecyclerView, and fetching events from Firestore.
     *
     * @param savedInstanceState A Bundle containing the activity's previously saved state, if available.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendees_browse_events);
        // Initialize RecyclerView and set its layout manager
        eventsRecyclerView = findViewById(R.id.eventsRecyclerView);

        eventsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize eventDataList
        eventDataList = new ArrayList<>();
        eventAdapter = new EventAdapter(this, eventDataList);
        eventsRecyclerView.setAdapter(eventAdapter);
        // Fetch events from Firestore
        fetchEvents();
        // Set up the "back" button click listener to finish the activity and return to the previous screen.
        findViewById(R.id.back_attendee_browse_events).setOnClickListener(v -> finish());
    }
    /**
     * Fetches events from Firestore and updates the {@code eventDataList} accordingly.
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
                        Event tempEvent = parseEvent(doc);
                        eventDataList.add(tempEvent);
                    }
                    eventAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    /**
     * Helper method to parse the Firestore document into an {@code Event} object.
     *
     * @param doc The Firestore document representing an event.
     * @return An {@code Event} object created from the Firestore document.
     */
    private Event parseEvent(QueryDocumentSnapshot doc) {
        // Example of reconstructing a Geolocation object
        // Assuming you store geolocation as a map with latitude and longitude
        float xcoord = doc.contains("xcoord") ? ((Number) doc.get("xcoord")).floatValue() : 0; //TODO: add geolocation stuff, figure it out
        float ycoord = doc.contains("ycoord") ? ((Number) doc.get("ycoord")).floatValue() : 0;
        String geolocationID = doc.getString("geolocationID"); // Assuming there's an ID field; adjust if necessary

        Geolocation geolocation = new Geolocation(geolocationID, xcoord, ycoord);

        // Using placeholders for Bitmaps as you'll load them asynchronously in the adapter/view
        Bitmap placeholderBitmap = null; // TODO: add actual QR for attendee checkIN and Browse

        return new Event(doc.getString("organizationName"), doc.getString("eventName"), doc.getString("eventPosterID"),
                doc.getString("description"), geolocation, placeholderBitmap,
                placeholderBitmap, 0,
                new Hashtable<>(), doc.getDate("eventDate"), new ArrayList<String>(), doc.getString("location"));
    }

}


