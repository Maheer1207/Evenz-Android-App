package com.example.evenz;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class AdminBrowseProfilesActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.admin_browse_profiles);
		findViewById(R.id.back_admin_browse_profiles).setOnClickListener(v -> finish());
	}
}