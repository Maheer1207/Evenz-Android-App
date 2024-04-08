package com.example.evenz;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
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



		//findViewById(R.id.header_icon).setOnClickListener(v -> finish());
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