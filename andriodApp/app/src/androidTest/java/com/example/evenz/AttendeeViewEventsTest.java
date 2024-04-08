package com.example.evenz;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;

import org.junit.Test;

public class AttendeeViewEventsTest {

    @Test
    public void testAttendeeSignUp() {
        // Setup intent with extras
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), UserEditProfileActivity.class);
        intent.putExtra("role", "attendee");
        try (ActivityScenario<UserEditProfileActivity> scenario = ActivityScenario.launch(intent)) {
            // Define all user input data
            String name = "Aizaz UI Test User";
            String phone = "780-881-7521";
            String email = "Aizazfire10@gmail.com";

            // Perform actions: input the name, phone number, email, toggle checkboxes, and click save
            Espresso.onView(ViewMatchers.withId(R.id.name_input))
                    .perform(ViewActions.typeText(name), ViewActions.closeSoftKeyboard());

            Espresso.onView(ViewMatchers.withId(R.id.phone_input))
                    .perform(ViewActions.typeText(phone), ViewActions.closeSoftKeyboard());

            Espresso.onView(ViewMatchers.withId(R.id.email_input))
                    .perform(ViewActions.typeText(email), ViewActions.closeSoftKeyboard());

            Espresso.onView(ViewMatchers.withId(R.id.enable_notification))
                    .perform(ViewActions.click());

            Espresso.onView(ViewMatchers.withId(R.id.enable_location))
                    .perform(ViewActions.click());

            Espresso.onView(ViewMatchers.withId(R.id.save_button))
                    .perform(ViewActions.click());

            // wait 2 seconds for the user to be saved
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }



            // Perform actions: open the EventSignUp page and sign up for an event
            Espresso.onView(ViewMatchers.withId(R.id.event_list))
                    .perform(ViewActions.click());

            // Sleep for 2 seconds to allow the event list to load
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }




        }
    }
}