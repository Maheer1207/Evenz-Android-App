package com.example.evenz;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * The AttendeesActivity class is an activity that displays a list of attendees for an event.
 * <p>
 * This class extends the AppCompatActivity class and overrides the onCreate method to set up the RecyclerView and load the attendees from Firebase.
 * <p>
 * The AttendeesActivity class has a RecyclerView for displaying the attendees, an AttendeeAdapter for binding the attendees to the RecyclerView, and a list of Attendee objects that it displays.
 *
 * @version 1.0
 * @see AppCompatActivity
 * @see RecyclerView
 * @see AttendeeAdapter
 * @see Attendee
 */
public class AttendeesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AttendeeAdapter adapter;
    private List<Attendee> attendeesList;

    /**
     * Called when the activity is starting. This is where most initialization should go.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down then this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle). Note: Otherwise it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_attendees_at_event);

        // Initialize the RecyclerView and set its layout manager
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the FirebaseAttendeeManager to fetch attendees from Firebase
        FirebaseAttendeeManager attendeeManager = new FirebaseAttendeeManager();

        // Fetch the attendees for the "Drake Concert" event
        attendeeManager.getEventAttendees("Drake Concert").addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // If the task is successful, get the list of attendees and print their names
                attendeesList = task.getResult();
                for (Attendee attendee : attendeesList) {
                    System.out.println(attendee.getName());
                }
                // Initialize the AttendeeAdapter with the list of attendees and set it as the adapter for the RecyclerView
                adapter = new AttendeeAdapter(attendeesList);
                recyclerView.setAdapter(adapter);
            } else {
                // If the task is not successful, print an error message
                System.out.println("Task was not successful");
            }
        });
    }

    /**
     * Called when the header icon is clicked. This method starts the MainActivity.
     *
     * @param view The view that was clicked.
     */
    public void onHeaderIconClicked(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}