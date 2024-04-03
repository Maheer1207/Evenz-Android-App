package com.example.evenz;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UserEditProfileActivity extends AppCompatActivity {

	private static final int PICK_IMAGE_REQUEST = 22;
	private static final String TAG = "UserEditProfileActivity";
	private Uri filePath;

	private ImageView imageView;
	private EditText nameInput, phoneInput, emailInput;

	private String profilePicID = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_edit_profile);

		imageView = findViewById(R.id.vector_ek2);
		nameInput = findViewById(R.id.name_input);
		phoneInput = findViewById(R.id.phone_input);
		emailInput = findViewById(R.id.email_input);

		imageView.setOnClickListener(v -> selectImage());
		findViewById(R.id.back_button).setOnClickListener(v -> finish());
		findViewById(R.id.save_button).setOnClickListener(v -> saveUserInfo());
	}

	private void saveUserInfo() {

		User user = buildUserObject();
		if (user == null) {
			// A toast message is shown within buildUserObject() for specific errors.
			return; // Exit the method if validation fails.
		}

		// Submit the user to the database
		FirebaseUserManager firebaseUserManager = new FirebaseUserManager();

		// Check if the task submitUser was successful
		firebaseUserManager.submitUser(user).addOnSuccessListener(aVoid -> {
			// If successful print a toast message
			Toast.makeText(UserEditProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();

			// Start the HomeScreenActivity
			Intent intent = new Intent(UserEditProfileActivity.this, HomeScreenActivity.class);
			Bundle homescreenBundle = new Bundle();
			homescreenBundle.putString("role", user.getUserType());
			homescreenBundle.putString("eventID", "CLQuoRALxppaIHscuwnG");
			intent.putExtras(homescreenBundle);
			startActivity(intent);

		}).addOnFailureListener(e -> {
			// If unsuccessful print a toast message
			showToast("Profile Update Failed");
		});


	}

	// Create a class that will handle the UI interaction by building the user object
	private User buildUserObject() {
		String name = nameInput.getText().toString().trim();
		String phone = phoneInput.getText().toString().trim();
		String email = emailInput.getText().toString().trim();

		// Validate name, phone, and email. Add or modify validation as necessary.
		if (name.isEmpty()) {
			showToast("Name cannot be empty.");
			return null;
		}
		if (phone.isEmpty()) {
			showToast("Phone cannot be empty.");
			return null;
		}
		if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
			showToast("Invalid email address.");
			return null;
		}

		@SuppressLint("HardwareIds") String deviceID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

		Bundle b = getIntent().getExtras();
		if (b == null) {
			showToast("User type not provided.");
			return null;
		}
		String userType = b.getString("role");
		if (userType == null) {
			showToast("Invalid user type.");
			return null;
		}

		return new User(deviceID, name, phone, email, profilePicID, userType);
	}


	private void selectImage() {
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(Intent.createChooser(intent, "Select Image from here..."), PICK_IMAGE_REQUEST);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
			filePath = data.getData();
			try {
				Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
				imageView.setImageBitmap(bitmap);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void showToast(String message) {
		Toast.makeText(UserEditProfileActivity.this, message, Toast.LENGTH_SHORT).show();
	}


}

