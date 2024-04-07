package com.example.evenz;

import android.content.Intent;
import android.os.Bundle;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class UserEditProfileActivityTest {

    @Test
    public void testAllUserInputActions() {
        // Setup intent with extras
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), UserEditProfileActivity.class);
        intent.putExtra("role", "attendee");
        try (ActivityScenario<UserEditProfileActivity> scenario = ActivityScenario.launch(intent)) {
            // Your Espresso interactions here
            // Define all user input data
            String name = "Aizaz UI Test User";
            String phone = "780-881-7521";
            String email = "aizazfire123@gmail.com";

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
        }

    }
}
