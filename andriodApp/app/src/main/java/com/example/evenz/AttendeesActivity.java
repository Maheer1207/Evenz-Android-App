package com.example.evenz;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.List;

/**
 * Class for the Activity allowing organizers to view a list of Attendees at a specified event.
 * contains fields storing the recyclerView it will display the list of Events in, the AttendeeAdapter,
 * list of Attendee objects to be displayed, the eventID whose attendees is being displayed with, and
 * the firebaseManageer.
 * contains methods
 * onCreate links to the XML UI file
 * InitalizeComponents is called, which initalizes the recyclerView.
 * fetchEventID, calls fetchAttendees, fetches the list of attendees from firebase.
 * updateUI() adds the data into the RecyclerView
 * Fields are not initalized in a unified constructor, instead initalization is distributed across function calls
 */
public class AttendeesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AttendeeAdapter adapter;
    private List<User> attendeesList;
    private TextView limit;
    private String limitString;
    private String eventID;
    private FirebaseUserManager firebaseUserManager;
    private FirebaseAttendeeManager firebaseAttendeeManager;

    /**
     * initalizing function linking the code to view_attendees_at_event.xml.
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_attendees_at_event);

        initializeComponents();

        String deviceID = getDeviceID();
        //String deviceID = "1";
        fetchEventID(deviceID);
    }

    /**
     * initalizes recyclerView to display attendees, alongside the header view, and firebase manager
     */
    private void initializeComponents() {
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        limit = findViewById(R.id.header_frame_text);
        limitString = "Attendees ";

        firebaseUserManager = new FirebaseUserManager();
    }


    /**
     * fetches eventID from Firebase, and calls fetchattendees() utilizing the fetched eventID
     * @param deviceID device ID used to fetch EventID.
     */
    private void fetchEventID(String deviceID) {
        Task<String> getEventID = firebaseUserManager.getEventName(deviceID);
        // add an on success listener print the event id as a toast
        getEventID.addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String eventID) {
                fetchAttendees(eventID);
                // log the event id
                Log.d(TAG, "onSuccess: Event ID: " + eventID);

            }
        });

        // add an on failure listener
        getEventID.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AttendeesActivity.this, "Error fetching event ID: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * fetches all attendees of specified event, and updates the UI with the fetched attendees.
     * @param eventID ID of the event.
     */
    private void fetchAttendees(String eventID) {
        this.eventID = eventID;
        Task<List<User>> getAttendeesTask = firebaseUserManager.getAttendeesForEvent(eventID);
        // add an on success listener to update the UI and display the attendees in toast
        getAttendeesTask.addOnSuccessListener(new OnSuccessListener<List<User>>() {
            @Override
            public void onSuccess(List<User> attendees) {
                updateUI(attendees);
                setHeaderString(eventID);
                // log the attendees
//                Log.d(TAG, "onSuccess: Attendees: " + attendees);
            }
        });
        // on failure listener to handle the error
        getAttendeesTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                handleAttendeesFailure(e);
            }
        });
    }

    /**
     * passes list of attendee objects into recyclerview to be displayed in the UI,
     * @param attendees List of Attendee Objects
     */
    private void updateUI(List<User> attendees) {
        attendeesList = attendees;
        adapter = new AttendeeAdapter(attendeesList, eventID);
        recyclerView.setAdapter(adapter);
    }

    // Please create a method that will set the value of the header string
    // to the number of attendees at the event. The header string should be
    // in the format "Attendees: X/Y" where X is the number of attendees. and Y is the limit.

    /**
     * sets the header string of the UI
     * @param eventID the event being displayed.
     */
    private void setHeaderString(String eventID) {
        int attendees = attendeesList.size();
        // Get the max attendees for the event from the getattendlimitfromeventname method in the eventutility class
        EventUtility.getAttendLimitFromEventName(eventID);
        Task<Integer> getAttendLimit = EventUtility.getAttendLimitFromEventName(eventID);
        getAttendLimit.addOnSuccessListener(new OnSuccessListener<Integer>() {
            @Override
            public void onSuccess(Integer maxAttendees) {
                // Convert the max attendees to a string
                limitString = "Attendees: " + attendees + "/" + maxAttendees;
                limit.setText(limitString);
            }
        });
        // Handle the failure of the getAttendLimit task
        getAttendLimit.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "onFailure: Error fetching attendees limit", e);
            }
        });

    }

    private void handleAttendeesFailure(@NonNull Exception e) {
        // print the error message to log
        Log.e(TAG, "onFailure: Error fetching attendees", e);
    }

    public void onHeaderIconClicked(View view) {
        Intent intent = new Intent(this, HomeScreenActivity.class);
        startActivity(intent);
    }

    @SuppressLint("HardwareIds")
    private String getDeviceID() {
        return Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
    }
}