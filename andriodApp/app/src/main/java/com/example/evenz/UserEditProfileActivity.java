package com.example.evenz;

import android.os.Bundle;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class UserEditProfileActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_edit_profile);

		findViewById(R.id.back_button).setOnClickListener(v -> finish());

		findViewById(R.id.save_button).setOnClickListener(v -> {
			// TODO: Save the user's profile
			// 1. Get the user's name, phone, and email from the edit text fields
			// 2. Update the user's information

			String name = ((EditText) findViewById(R.id.name_input)).getText().toString();
			String phone = ((EditText) findViewById(R.id.phone_input)).getText().toString();
			String email = ((EditText) findViewById(R.id.email_input)).getText().toString();

			// Create an attendee object
			Attendee attendee = new Attendee(name, phone, email);

			// use the sumbitAttendee method to submit the attendee to Firebase
			FirebaseAttendeeManager firebaseAttendeeManager = new FirebaseAttendeeManager();
			firebaseAttendeeManager.submitAttendee(attendee);

			// Close the activity
			finish();


		});



	}
}