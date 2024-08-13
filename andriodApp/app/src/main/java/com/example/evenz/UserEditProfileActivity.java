package com.example.evenz;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;

import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.Manifest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.Objects;

/**
 * an acitity to allow the user to edit theur profile
 */
public class UserEditProfileActivity extends AppCompatActivity implements ImageOptionsFragment.ImageOptionsListener {

	private static final int PICK_IMAGE_REQUEST = 22;
	private static final String TAG = "UserEditProfileActivity";
	private Uri filePath;

	private ImageView imageView;
	private EditText nameInput, phoneInput, emailInput;
	private CheckBox notificationEnabledInput, locationEnabledInput;
	private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1000; // Request code for location permission

	private String profilePicID = "";
	private FirebaseFirestore db;
	private CollectionReference usersRef;
	private ImageUtility imageUtility = new ImageUtility(); //user imageUtilty for upload DP


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		String deviceID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
		db = FirebaseFirestore.getInstance();
		usersRef = db.collection("users");
		DocumentReference userDocRef = usersRef.document(deviceID);

		// Asynchronously retrieve the document from Firestore
		userDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
			@Override
			public void onComplete(Task<DocumentSnapshot> task) {
				if (task.isSuccessful()) {
					DocumentSnapshot doc = task.getResult();
					if (doc != null && doc.exists()) {
						setUpEditProfile(doc);
					} else {
						setUpCreateProfile();
					}
				}
			}
		});
	}

	private void initCreate() {
		imageView = findViewById(R.id.create_profile_pic);
		nameInput = findViewById(R.id.name_input_create_prf);
		phoneInput = findViewById(R.id.phone_input_create_prf);
		emailInput = findViewById(R.id.email_input_create_prf);
		notificationEnabledInput = findViewById(R.id.enable_notification_create_prf);
		locationEnabledInput = findViewById(R.id.enable_location_create_prf);

		initLocationServiceListener(); //this is for location service
	}
	private void initEdit() {
		imageView = findViewById(R.id.profile_pic);
		nameInput = findViewById(R.id.name_input);
		phoneInput = findViewById(R.id.phone_input);
		emailInput = findViewById(R.id.email_input);
		notificationEnabledInput = findViewById(R.id.enable_notification);
		locationEnabledInput = findViewById(R.id.enable_location);

		locationEnabledInput.setOnCheckedChangeListener((buttonView, isChecked) -> {
			if (!isChecked) {
				// Code to direct user to location settings
				Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				startActivity(intent);
			} else {
				// Check if permission is already granted
				if (ContextCompat.checkSelfPermission(UserEditProfileActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
					// Request permission
					ActivityCompat.requestPermissions(UserEditProfileActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
				}
			}
		});
	}

	private void fillProfile(DocumentSnapshot doc) {
		nameInput.setText(doc.getString("name"));
		phoneInput.setText(doc.getString("phone"));
		emailInput.setText(doc.getString("email"));
		notificationEnabledInput.setChecked(Boolean.TRUE.equals(doc.getBoolean("notificationEnabled")));
		locationEnabledInput.setChecked(Boolean.TRUE.equals(doc.getBoolean("locationEnabled")));
		profilePicID = doc.getString("profilePicID");

		if (Objects.equals(profilePicID, "")) {
			String name = nameInput.getText().toString().trim();
			Bitmap profileImage = ImageGenerator.generateProfileImage(name, 500, 500); // Adjust the size as needed

			imageView.setImageBitmap(profileImage);
		} else {
			ImageUtility.displayImage(profilePicID, imageView);
		}

	}
	//TEST CODe, this is for asking for permission access to the location
	private void initLocationServiceListener() {
		locationEnabledInput.setOnCheckedChangeListener((buttonView, isChecked) -> {
			if (isChecked) {
				// Check if permission is already granted
				if (ContextCompat.checkSelfPermission(UserEditProfileActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
					// Request permission
					ActivityCompat.requestPermissions(UserEditProfileActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
				}
			}
		});
	}
	//TEST CODE, this is for permission to access the location
	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
			if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				// Permission was granted.
				// You can now do something with the location services.
			} else {
				// Permission denied.
				// You might want to disable the location feature or inform the user.
				locationEnabledInput.setChecked(false);
				showToast("Location permission is required to use location services.");
			}
		}
	}



	private void setUpCreateProfile() {
		setContentView(R.layout.user_create_profile);

		initCreate();

		imageView.setOnClickListener(v -> {
			ImageOptionsFragment optionsFragment = new ImageOptionsFragment();
			optionsFragment.show(getSupportFragmentManager(), "imageOptionsDialog");
		});

		findViewById(R.id.back_button_create_prf).setOnClickListener(v -> finish());
		findViewById(R.id.create_prf_button).setOnClickListener(v -> saveUserInfo());
	}

	private void setUpEditProfile(DocumentSnapshot doc) {
		setContentView(R.layout.user_edit_profile);
		initEdit();

		imageView.setOnClickListener(v -> {
			ImageOptionsFragment optionsFragment = new ImageOptionsFragment();
			optionsFragment.show(getSupportFragmentManager(), "imageOptionsDialog");
		});

		findViewById(R.id.back_button).setOnClickListener(v -> finish());
		findViewById(R.id.save_button).setOnClickListener(v -> saveUserInfo());
		fillProfile(doc);
	}

	private void saveUserInfo() {
		if (filePath != null) {
			imageUtility.upload(filePath, new ImageUtility.UploadCallback() {
				@Override
				public void onSuccess(String imageID, String imageURL) {
					profilePicID = imageID; // Update profilePicID with the uploaded image ID

					buildUserObject();
				}
				@Override
				public void onFailure(Exception e) {
					showToast("Profile Picture Upload Failed");
				}
			});
		} else {
			// Proceed without a profile picture
			buildUserObject();
		}
	}

	private void submitUserToDatabase(User user) {
		FirebaseUserManager firebaseUserManager = new FirebaseUserManager();
		firebaseUserManager.submitUser(user).addOnSuccessListener(aVoid -> {
			Toast.makeText(UserEditProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
			Intent intent = new Intent(UserEditProfileActivity.this, HomeScreenActivity.class);
			startActivity(intent);
		}).addOnFailureListener(e -> showToast("Profile Update Failed"));
	}

	// Create a class that will handle the UI interaction by building the user object
	private User buildUserObject() {
		String name = nameInput.getText().toString().trim();
		String phone = phoneInput.getText().toString().trim();
		String email = emailInput.getText().toString().trim();
		boolean notificationEnabled = notificationEnabledInput.isChecked();
		boolean locationEnabled = locationEnabledInput.isChecked();


		// Validate name, phone, and email. Add or modify validation as necessary.
		if (name.isEmpty()) {
			showToast("Name cannot be empty.");
			return null;
		}
		if (phone.isEmpty()) {
			showToast("Phone cannot be empty.");
			return null;
		}
		if (!email.isEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
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

		FirebaseUserManager firebaseUserManager = new FirebaseUserManager();
		firebaseUserManager.getEventIDAttendee(deviceID).addOnSuccessListener(new OnSuccessListener<String>() {
			@Override
			public void onSuccess(String eventID) {
				User user = new User(deviceID, name, phone, email, profilePicID, userType, notificationEnabled, locationEnabled);
				user.setCheckedInEvent(eventID);
				submitUserToDatabase(user);
			}
		});

		return null;
	}

	@Override
	public void onSelectImage() {
		selectImage();
	}

	@Override
	public void onDeleteImage() {
		profilePicID="";
		String name = nameInput.getText().toString().trim();
		Bitmap profileImage = ImageGenerator.generateProfileImage(name, 500, 500); // Adjust the size as needed

		imageView.setImageBitmap(profileImage);
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

