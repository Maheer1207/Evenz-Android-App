package com.example.evenz;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class AdminBrowseImagesActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.admin_browse_images);
		findViewById(R.id.back_admin_browse_images).setOnClickListener(v -> finish());
	}
}