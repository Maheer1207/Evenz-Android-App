package com.example.evenz;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class UserEditProfileActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_edit_profile);

		findViewById(R.id.back_button).setOnClickListener(v -> finish());

		findViewById(R.id.save_button).setOnClickListener(v -> {
			String name = ((EditText) findViewById(R.id.name_input)).getText().toString();
			String phone = ((EditText) findViewById(R.id.phone_input)).getText().toString();
			String email = ((EditText) findViewById(R.id.email_input)).getText().toString();

			FirebaseFirestore db = FirebaseFirestore.getInstance();

			String deviceID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

			DocumentReference userDocRef = db.collection("users").document(deviceID);

			userDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
				@Override
				public void onComplete(@NonNull Task<DocumentSnapshot> task) {
					if (task.isSuccessful()) {
						DocumentSnapshot document = task.getResult();
						// If same name already exists in the database, print a toast that says "User already exists"
						if (false) {
							// User exists, you can now proceed with event creation or update user data as needed
							// Print a toast that says "User already exists"
							Toast.makeText(UserEditProfileActivity.this, "User already exists", Toast.LENGTH_SHORT).show();



						} else {
							@SuppressLint("HardwareIds") String userID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

							// Create a User object
							User newUser = new User(deviceID, name, phone, email);

							// Convert User object to a Map
							Map<String, Object> userMap = new HashMap<>();
							// get the userID from the person's device using the Settings.Secure class
							userMap.put("userID", userID);
							userMap.put("name", newUser.getName());
							userMap.put("phone", newUser.getPhone());
							userMap.put("email", newUser.getEmail());

							db.collection("users").document(deviceID).set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
								@Override
								public void onComplete(@NonNull Task<Void> task) {
									if (task.isSuccessful()) {
										// After successfully saving the user to Firebase, finish the activity
										finish();
										// You can also show a success message to the user
										// Print a toast that says "Profile updated successfully"
										Toast.makeText(UserEditProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
									} else {
										// Handle the error
										// Print a toast that says "Failed to update profile"
										Toast.makeText(UserEditProfileActivity.this, "Failed to update profile", Toast.LENGTH_SHORT).show();
									}
								}
							});
						}
					}
				}
			});
		});
	}
}