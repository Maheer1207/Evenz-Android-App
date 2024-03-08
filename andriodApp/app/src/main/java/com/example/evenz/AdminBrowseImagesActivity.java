package com.example.evenz;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
/**
 * The {@code AdminBrowseImagesActivity} class represents an activity in the application specifically
 * designed for administrative users to browse images. It extends the {@code AppCompatActivity} class
 * and provides functionality for displaying and managing images available to the administrator.
 *
 * <p>The activity includes the layout defined in the {@code admin_browse_images.xml} file, and it allows
 * the administrator to navigate through images. The "back" button is set up to finish the activity when
 * clicked, returning to the previous screen.
 *
 * <p>This class is part of the administrative module and contributes to the user interface and
 * interaction for image management in the administrative section of the application.
 *
 * @author Ofazal
 * @version 1.0
 * @see AppCompatActivity
 */
public class AdminBrowseImagesActivity extends AppCompatActivity {
	/**
	 * Called when the activity is first created. Responsible for initializing the activity, setting up
	 * the layout, and configuring event handlers.
	 *
	 * @param savedInstanceState A Bundle containing the activity's previously saved state, if available.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.admin_browse_images);
		// Set up the "back" button click listener to finish the activity and return to the previous screen.
		findViewById(R.id.back_admin_browse_images).setOnClickListener(v -> finish());
	}
}