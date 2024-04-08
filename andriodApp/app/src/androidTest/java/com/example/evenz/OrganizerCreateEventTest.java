package com.example.evenz;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;

import org.junit.Test;

public class OrganizerCreateEventTest {
    // This test will open up the Create Event page and fill in the required fields
    // It will then click the save button and wait for the event to be saved

    @Test
    public void testOrganizerCreateEvent() {

        // Setup intent with extras
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), EventCreationActivity.class);

        try (ActivityScenario<EventCreationActivity> scenario = ActivityScenario.launch(intent)) {
            // Define all event input data
            String organizerName = "Test Organizer";
            String eventName = "Test Event";
            String eventDescription = "This is a test event";
            String eventLocation = "Edmonton";
            String eventDate = "2022-12-31";
            String eventTime = "12:00 PM";
            String eventCapacity = "100";
            String eventCategory = "Music";

            // Perform actions: open the Create Event page and fill in the required fields


            Espresso.onView(ViewMatchers.withId(R.id.editTextOrganizerName))
                    .perform(ViewActions.typeText(organizerName), ViewActions.closeSoftKeyboard());

            Espresso.onView(ViewMatchers.withId(R.id.editTextEventName))
                    .perform(ViewActions.typeText(eventName), ViewActions.closeSoftKeyboard());

            Espresso.onView(ViewMatchers.withId(R.id.editDate))
                    .perform(ViewActions.typeText(eventDate), ViewActions.closeSoftKeyboard());

            Espresso.onView(ViewMatchers.withId(R.id.no_limit))
                    .perform(ViewActions.typeText(eventCapacity), ViewActions.closeSoftKeyboard());

            Espresso.onView(ViewMatchers.withId(R.id.editTextLocation))
                    .perform(ViewActions.typeText(eventLocation), ViewActions.closeSoftKeyboard());

            // Sent event description
            Espresso.onView(ViewMatchers.withId(R.id.editTextEventInfo))
                    .perform(ViewActions.typeText(eventDescription), ViewActions.closeSoftKeyboard());

            Espresso.onView(ViewMatchers.withId(R.id.create_event_button))
                    .perform(ViewActions.click());

            // Please press the upload button to upload the image
            Espresso.onView(ViewMatchers.withId(R.id.upload_poster_img))
                    .perform(ViewActions.click());


            // wait 2 seconds for the event to be saved
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}