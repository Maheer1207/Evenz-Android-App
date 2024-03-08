package com.example.evenz;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
/**
 * The {@code AttendeeEventInfoActivity} class represents an activity in the application tailored
 * for attendees to view detailed information about a specific event. It extends the
 * {@code AppCompatActivity} class and provides functionality for displaying event details to
 * attendees.
 *
 * <p>The activity includes the layout defined in the {@code attendee_event_info.xml} file, allowing
 * attendees to access information related to the event. The "back" button is set up to finish the
 * activity when clicked, returning to the previous screen.
 *
 * <p>This class is part of the attendee module and contributes to the user interface and interaction
 * for event information viewing in the attendee section of the application.
 *
 * @author maheee1207
 * @version 1.0
 * @see AppCompatActivity
 */
public class AttendeeEventInfoActivity extends AppCompatActivity {
    /**
     * Called when the activity is first created. Responsible for initializing the activity, setting up
     * the layout, and configuring event handlers.
     *
     * @param savedInstanceState A Bundle containing the activity's previously saved state, if available.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendee_event_info);
        // Set up the "back" button click listener to finish the activity and return to the previous screen.
        findViewById(R.id.back_attendee_event_info).setOnClickListener(v -> finish());
    }
}