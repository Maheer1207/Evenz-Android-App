package com.example.evenz;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

/**
 * The {@code AdminBrowseEventActivity} class represents an activity in the application
 * specifically designed for administrative users to browse events. It extends the
 * {@code AppCompatActivity} class and provides functionality for displaying a list of events
 * available to the administrator.
 *
 * <p>The activity includes the layout defined in the {@code admin_browse_users.xml} file, and
 * it allows the administrator to navigate through events. The "back" button is set up to finish
 * the activity when clicked, returning to the previous screen.
 *
 * <p>This class is part of the administrative module and contributes to the user interface and
 * interaction for event management in the administrative section of the application.
 *
 * @author maheer1207
 * @version 1.0
 * @see AppCompatActivity
 */
public class AdminBrowseEventActivity extends AppCompatActivity {
    /**
     * Called when the activity is first created. Responsible for initializing the activity,
     * setting up the layout, and configuring event handlers.
     *
     * @param savedInstanceState A Bundle containing the activity's previously saved state, if
     *                           available.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_browse_users);
        // Set up the "back" button click listener to finish the activity and return to the previous screen
        findViewById(R.id.back_admin_browse_events).setOnClickListener(v -> finish());
    }
}