package com.example.evenz;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UserEditProfileActivity extends AppCompatActivity {

	private static final int PICK_IMAGE_REQUEST = 22;
	private Uri filePath;
	StorageReference storageReference;
	StorageReference photoRef;

	private ImageView imageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_edit_profile);

		imageView = findViewById(R.id.vector_ek2); // Assuming this is your ImageView for the profile picture
		imageView.setOnClickListener(v -> selectImage());

		findViewById(R.id.back_button).setOnClickListener(v -> finish());

		findViewById(R.id.save_button).setOnClickListener(v -> {
			String name = ((EditText) findViewById(R.id.name_input)).getText().toString();
			String phone = ((EditText) findViewById(R.id.phone_input)).getText().toString();
			String email = ((EditText) findViewById(R.id.email_input)).getText().toString();

			FirebaseFirestore db = FirebaseFirestore.getInstance();

			String deviceID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

			DocumentReference userDocRef = db.collection("users").document(deviceID);

			String profilePic_ID = UUID.randomUUID().toString();
			storageReference = FirebaseStorage.getInstance().getReference();
			photoRef = storageReference.child("images/" + profilePic_ID);

			uploadImage(photoRef);

			userDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
				@Override
				public void onComplete(@NonNull Task<DocumentSnapshot> task) {
					if (task.isSuccessful()) {
						DocumentSnapshot document = task.getResult();
						if (document != null && document.exists()) {
							// User exists, you can now proceed with event creation or update user data as needed
						} else {
							// Create a User object
							User newUser = new User(deviceID, name, phone, email);

							// Convert User object to a Map
							Map<String, Object> userMap = new HashMap<>();
							userMap.put("userId", newUser.getUserId());
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

	private void uploadImage(StorageReference photoRef) {
		if (filePath != null) {
			ProgressDialog progressDialog = new ProgressDialog(this);
			progressDialog.setTitle("Uploading...");
			progressDialog.show();

			photoRef.putFile(filePath)
					.addOnSuccessListener(taskSnapshot -> {
						progressDialog.dismiss();
						Toast.makeText(UserEditProfileActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
					})
					.addOnFailureListener(e -> {
						progressDialog.dismiss();
						Toast.makeText(UserEditProfileActivity.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
					})
					.addOnProgressListener(taskSnapshot -> {
						double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
						progressDialog.setMessage("Uploaded " + (int) progress + "%");
					});
		}
	}
}