package com.example.evenz;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Document;

public class InitialPageActivity extends AppCompatActivity {

    private DocumentReference doc;
    private FirebaseFirestore db;
    private CollectionReference usersRef;
    private String role;

    private String deviceID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.container_initial_page);

        FrameLayout attendeeLayout = findViewById(R.id.container_group1);
        FrameLayout organizerLayout = findViewById(R.id.container_group2);
        FrameLayout guestLayout = findViewById(R.id.container_group3);

        db = FirebaseFirestore.getInstance();
        usersRef = db.collection("users");

        // getting the devices id to get the user
        deviceID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        FirebaseUserManager firebaseUserManager = new FirebaseUserManager();

        // getting the user type
        Task<String> getUserType = firebaseUserManager.getUserType(deviceID);
        getUserType.addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String type) {
                role = type;
                // role is attendee/organizer so sends to event page
                if (role.equals("attendee") || role.equals("Organizer")) {
                    Intent intent = new Intent(InitialPageActivity.this, HomeScreenActivity.class);
                    startActivity(intent);
                // role is admin so sends to admin browse event
                } else if (role.equals("admin")){
                    Intent intent = new Intent(InitialPageActivity.this, AdminBrowseEventActivity.class);
                    startActivity(intent);
                }
            }
        });

        attendeeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InitialPageActivity.this, UserEditProfileActivity.class);
                Bundle b = new Bundle();
                b.putString("role", "attendee");
                intent.putExtras(b);
                startActivity(intent);
            }
        });

        organizerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InitialPageActivity.this, EventCreationActivity.class);
                Bundle b = new Bundle();
                b.putString("role", "organizer");
                intent.putExtras(b);
                startActivity(intent);
            }
        });

        guestLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User user = new User(deviceID, "Guest", "", "", "", "attendee", false, false);
                Task<Void> submitUser = firebaseUserManager.submitUser(user);
                // add an on success listener to update the UI and display the attendees in toast
                submitUser.addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void x) {
                        Intent intent = new Intent(InitialPageActivity.this, HomeScreenActivity.class);
                        Bundle b = new Bundle();
                        b.putString("role", "attendee");
                        intent.putExtras(b);
                        startActivity(intent);
                    }
                });
            }
        });
    }
}