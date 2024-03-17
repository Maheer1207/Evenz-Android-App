package com.example.evenz;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


/**
 * This class is responsible for allowing the user to edit their profile information and upload a profile picture.
 * The user's name, phone, and email are saved to Firebase Firestore, and the user's profile picture is uploaded to Firebase Storage.
 */
public class UserEditProfileActivity extends AppCompatActivity {

	private static final int PICK_IMAGE_REQUEST = 22;
	private Uri filePath;
	private StorageReference storageReference;

	private ImageView imageView;
	private EditText nameInput, phoneInput, emailInput;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_edit_profile);

		imageView = findViewById(R.id.vector_ek2);
		nameInput = findViewById(R.id.name_input);
		phoneInput = findViewById(R.id.phone_input);
		emailInput = findViewById(R.id.email_input);

		storageReference = FirebaseStorage.getInstance().getReference();

		imageView.setOnClickListener(v -> selectImage());
		findViewById(R.id.back_button).setOnClickListener(v -> finish());
		findViewById(R.id.save_button).setOnClickListener(v -> saveUserInfo());
	}

	/**
	 * This method is responsible for launching an activity for the user to select an image from their device.
	 */
	private void selectImage() {
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(Intent.createChooser(intent, "Select Image from here..."), PICK_IMAGE_REQUEST);
	}

	/**
	 * This method is called when an activity you launched exits, giving you the requestCode you started it with,
	 * the resultCode it returned, and any additional data from it.
	 *
	 * @param requestCode The integer request code originally supplied to startActivityForResult(), allowing you to identify who this result came from.
	 * @param resultCode The integer result code returned by the child activity through its setResult().
	 * @param data An Intent, which can return result data to the caller (various data can be attached to Intent "extras").
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		// Check if the request code matches PICK_IMAGE_REQUEST, the result code is RESULT_OK, and the data Intent is not null and contains a data URI.
		if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
			// Retrieve the data URI of the selected image and store it in filePath.
			filePath = data.getData();
			try {
				// Create a Bitmap object from the image file at the data URI.
				Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
				// Set the Bitmap to the ImageView.
				imageView.setImageBitmap(bitmap);
			} catch (IOException e) {
				// Print the stack trace if there's an IOException while creating the Bitmap.
				e.printStackTrace();
			}
		}
	}

	/**
	 * This method is responsible for saving user information to Firebase Firestore and uploading a profile picture to Firebase Storage if one is selected.
	 * If a user with the same device ID already exists in Firestore, the user's profile is updated.
	 * If no image is selected, the profilePicId is set to null.
	 */
	private void saveUserInfo() {
		// Retrieve the user's name, phone, and email from the respective EditText fields and trim any leading or trailing whitespace.
		String name = nameInput.getText().toString().trim();
		String phone = phoneInput.getText().toString().trim();
		String email = emailInput.getText().toString().trim();

		// Retrieve the device ID, which is used as the document ID in Firestore for the user's document.
		String deviceID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

		FirebaseFirestore db = FirebaseFirestore.getInstance();
		DocumentReference userDocRef = db.collection("users").document(deviceID);

		// Check if a user with the same device ID already exists in Firestore.
		userDocRef.get().addOnCompleteListener(task -> {
			if (task.isSuccessful()) {
				DocumentSnapshot document = task.getResult();
				if (document.exists()) {
					// If a user with the same device ID already exists, update the user's profile.
					updateUserProfile(name, phone, email, userDocRef, null);
					// Print a toast message to the user indicating that their profile was updated successfully.
					Toast.makeText(UserEditProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
				} else {
					// If filePath is not null, meaning an image was selected, upload the image to Firebase Storage.
					if (filePath != null) {
						final String profilePicId = UUID.randomUUID().toString();
						StorageReference photoRef = storageReference.child("images/" + profilePicId);

						ProgressDialog progressDialog = new ProgressDialog(this);
						progressDialog.setTitle("Uploading...");
						progressDialog.show();

						photoRef.putFile(filePath)
								.addOnSuccessListener(taskSnapshot -> {
									progressDialog.dismiss();
									Toast.makeText(UserEditProfileActivity.this, "Uploaded Profile Picture", Toast.LENGTH_SHORT).show();
									updateUserProfile(name, phone, email, userDocRef, profilePicId);
								})
								.addOnFailureListener(e -> {
									progressDialog.dismiss();
									Toast.makeText(UserEditProfileActivity.this, "Your Profile Picture was WHACK and didn't upload" + e.getMessage(), Toast.LENGTH_SHORT).show();
								})
								.addOnProgressListener(taskSnapshot -> {
									double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
									progressDialog.setMessage("Uploaded " + (int) progress + "%");
								});
					} else {
						// If filePath is null, meaning no image was selected, set profilePicId to null and save the user's information to Firestore.
						updateUserProfile(name, phone, email, userDocRef, null);
						// Print a toast message to the user indicating that their profile was added successfully.
						Toast.makeText(UserEditProfileActivity.this, "Profile added successfully", Toast.LENGTH_SHORT).show();
					}
				}
			} else {
				Toast.makeText(UserEditProfileActivity.this, "Failed to check if user exists: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
			}
		});
	}

	/**
	 * This method is responsible for updating the user's profile in Firestore with the provided name, phone, and email.
	 * If a profile picture ID is available, it is also included in the user's information.
	 *
	 * @param name The user's name.
	 * @param phone The user's phone number.
	 * @param email The user's email address.
	 * @param userDocRef The DocumentReference for the user's document in Firestore.
	 * @param profilePicId The profile picture ID for the user's profile picture in Firebase Storage. If no profile picture is available, this parameter is null.
	 */
	private void updateUserProfile(String name, String phone, String email, DocumentReference userDocRef, String profilePicId) {
		Map<String, Object> userMap = new HashMap<>();
		userMap.put("name", name);
		userMap.put("phone", phone);
		userMap.put("email", email);
		// If a profile picture ID is available, include it in the user's information.
		if (profilePicId != null) {
			userMap.put("profilePicId", profilePicId);
		}

		userDocRef.set(userMap)
				.addOnSuccessListener(aVoid -> {
					finish(); // Finish activity after save
				})
				.addOnFailureListener(e -> Toast.makeText(UserEditProfileActivity.this, "Failed to update profile: " + e.getMessage(), Toast.LENGTH_SHORT).show());
	}
}