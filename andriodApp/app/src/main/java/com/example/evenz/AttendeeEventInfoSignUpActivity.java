package com.example.evenz;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
/**
 * The {@code AttendeeEventInfoSignUpActivity} class represents an activity in the application designed
 * for attendees to view detailed information about an event and sign up to attend. It extends the
 * {@code AppCompatActivity} class and provides functionality for displaying event details and allowing
 * attendees to sign up for the event.
 *
 * <p>The activity includes the layout defined in the {@code attendee_event_info_sign_up.xml} file,
 * enabling attendees to access information about the event and complete the sign-up process. The "back"
 * button is set up to finish the activity when clicked, returning to the previous screen.
 *
 * <p>This class is part of the attendee module and contributes to the user interface and interaction
 * for event information viewing and sign-up in the attendee section of the application.
 *
 * @author maheee1207
 * @version 1.0
 * @see AppCompatActivity
 */
public class AttendeeEventInfoSignUpActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendee_event_info_sign_up);
        // Set up the "back" button click listener to finish the activity and return to the previous screen.
        findViewById(R.id.back_attendee_event_info_signup).setOnClickListener(v -> finish());
    }
}