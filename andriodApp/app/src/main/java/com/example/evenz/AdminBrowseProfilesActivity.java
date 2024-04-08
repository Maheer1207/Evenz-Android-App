package com.example.evenz;


import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import android.content.Context;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;


/**

 */
public class AdminBrowseProfilesActivity extends AppCompatActivity {
	private RecyclerView userRecyclerView;
	private AttendeeAdapter attendeeAdapter;
	private List<User> userDataList;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.admin_browse_profiles); //todo.

		userRecyclerView = findViewById(R.id.recycler_view);

		userRecyclerView.setLayoutManager(new LinearLayoutManager(this));

		userDataList = new ArrayList<>();

		attendeeAdapter = new AttendeeAdapter(userDataList);
		userRecyclerView.setAdapter(attendeeAdapter);
		attendeeAdapter.setOnClickListener(new AttendeeAdapter.OnClickListener() {
			@Override
			public void onClick(int position, User model) {
				Log.d("Ben8888", "clicksuccess");

				Intent intent = new Intent(AdminBrowseProfilesActivity.this, AdminDeleteUser.class);
				Bundle rb = getIntent().getExtras();

				Bundle b = new Bundle();
				b.putString("x1",  model.getName());
				b.putString("x2", model.getEmail());
				b.putString("x3", model.getPhone());
				b.putString("x4", model.getProfilePicID());
				b.putString("x5", model.getCheckedInEvent());
				b.putString("x6", model.getUserId());
				b.putString("x7", model.getUserType());

				intent.putExtras(b);

				startActivity(intent);

			}
		});
		fetchUsers();


		findViewById(R.id.back_admin_browse_profiles).setOnClickListener(v -> finish());
	}


	private void fetchUsers() {

		FirebaseFirestore db = FirebaseFirestore.getInstance();
		db.collection("users").addSnapshotListener(new EventListener<QuerySnapshot>() {
			@Override
			public void onEvent(@Nullable QuerySnapshot querySnapshots, @Nullable FirebaseFirestoreException error) {
				if (error != null) {
					Log.e("Firestore", error.toString());
					return;
				}

				if (querySnapshots != null) {
					userDataList.clear();
					for (QueryDocumentSnapshot document : querySnapshots) {
						User user = new User(
								document.getId(),
								document.getString("name"),
								document.getString("phone"),
								document.getString("email"),
								document.getString("profilePicID"),
								document.getString("userType"),
								document.getBoolean("notificationEnabled"),
								document.getBoolean("locationEnabled")
						);
						// Check if the userType is "attendee" before adding the user to the list


						userDataList.add(user);
					}
					attendeeAdapter.notifyDataSetChanged();
				}
			}
		});
	}


}