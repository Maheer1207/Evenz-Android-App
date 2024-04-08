package com.example.evenz;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.List;

public class AdminBrowseProfilesActivity extends AppCompatActivity {
	private RecyclerView recyclerView;
	private AdminAttendeeAdapter adapter;
	private List<User> attendeesList;
	private FirebaseUserManager firebaseUserManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.admin_attendee_list);

		recyclerView = findViewById(R.id.recycler_view);

		recyclerView.setLayoutManager(new LinearLayoutManager(this));

		fetchUsers();

		findViewById(R.id.icon2).setOnClickListener( v -> startActivity(new Intent(AdminBrowseProfilesActivity.this, AdminBrowseEventActivity.class)));
		findViewById(R.id.icon3).setOnClickListener( v -> startActivity(new Intent(AdminBrowseProfilesActivity.this, ImageBrowseActivity.class)));
		findViewById(R.id.header_icon).setOnClickListener(v -> finish());
	}

	private void fetchUsers() {
		firebaseUserManager = new FirebaseUserManager();
		Task<List<User>> getAttendees = firebaseUserManager.getAttendees();
		// add an on success listener to update the UI and display the attendees in toast
		getAttendees.addOnSuccessListener(new OnSuccessListener<List<User>>() {
			@Override
			public void onSuccess(List<User> attendees) {
				attendeesList = attendees;
				setUp();
			}
		});
	}

	private void setUp(){
		// Initialize eventDataList
		adapter = new AdminAttendeeAdapter(attendeesList);
		recyclerView.setAdapter(adapter);

		adapter.setOnClickListener(new AdminAttendeeAdapter.OnClickListener() {
			@Override
			public void onClick(int position, User user) {
				new AlertDialog.Builder(AdminBrowseProfilesActivity.this)
						.setTitle("Delete Confirmation")
						.setMessage("Are you sure you want to delete " + user.getName())
						.setPositiveButton("Yes", (dialog, which) -> {
							Task<Void> deleteUser = firebaseUserManager.deleteUser(user.getUserId());
							// add an on success listener to update the UI and display the attendees in toast
							deleteUser.addOnSuccessListener(new OnSuccessListener<Void>() {
								@Override
								public void onSuccess(Void x) {
									// Display toast message
									attendeesList.remove(user);
									adapter.notifyDataSetChanged();
									Toast.makeText(AdminBrowseProfilesActivity.this, "Successfully Deleted!!!", Toast.LENGTH_SHORT).show();
								}
							});
						})
						.setNegativeButton("No", null)
						.show();
			}
		});
	}
}