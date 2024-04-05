package com.example.evenz;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private CollectionReference eventsRef;
    private CollectionReference usersRef;

    private ArrayList<Event> eventDataList;
    private ArrayList<User> userDataList;
    private TextView textView;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        eventDataList = new ArrayList<>();
        userDataList = new ArrayList<>();
        
        db = FirebaseFirestore.getInstance();
        eventsRef = db.collection("events");
        usersRef = db.collection("users");

        //  TODO: Demo Button, Need to be deleted
        final Button createEvent = findViewById(R.id.button_create_new_event);
        createEvent.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EventCreationActivity.class);
                startActivity(intent);
            }
        });
        //  TODO: Demo Button, Need to be deleted
        final Button intialPage = findViewById(R.id.initial_who_Screen);
        intialPage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, InitialPageActivity.class);
                startActivity(intent);
            }
        });
        //  TODO: Demo Button, Need to be deleted
        final Button admin_event_browse = findViewById(R.id.button_admin_event_browse);
        admin_event_browse.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AdminBrowseEventActivity.class);
                startActivity(intent);
            }
        });

        //  TODO: Demo Button, Need to be deleted
        final Button event_browse = findViewById(R.id.button_event_browse);
        event_browse.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EventBrowseActivity.class);
                startActivity(intent);
            }
        });

        //  TODO: Demo Button, Need to be deleted
//        final Button sendNotification = findViewById(R.id.send_notification);
//        sendNotification.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                Intent intent = new Intent(MainActivity.this, OrgSendNotificationActivity.class);
//                startActivity(intent);
//            }
//        });

        //  TODO: Demo Button, Need to be deleted
        final Button attendee_list = findViewById(R.id.button_attendee);
        attendee_list.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AttendeesActivity.class);
                startActivity(intent);
            }
        });
        //  TODO: Demo Button, Need to be deleted
        final Button create_new_user = findViewById(R.id.button_create_new_user);
        create_new_user.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, UserEditProfileActivity.class);
                Bundle b = new Bundle();
                b.putString("role", "attendee");
                intent.putExtras(b);
                startActivity(intent);
            }
        });


        //  TODO: Demo Button, Need to be deleted
        final Button attendee_home = findViewById(R.id.attendee_home);
        attendee_home.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String deviceID = "56d8904ace9c2275";
                FirebaseAttendeeManager firebaseAttendeeManager = new FirebaseAttendeeManager();
                Task<String> getEventID = firebaseAttendeeManager.getEventID(deviceID);
                getEventID.addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String eventID) {
                        Intent intent = new Intent(MainActivity.this, HomeScreenActivity.class);
                        Bundle b = new Bundle();
                        b.putString("role", "attendee");
                        b.putString("eventID", eventID);
                        intent.putExtras(b);
                        startActivity(intent);
                    }
                });
            }
        });

        //  TODO: Demo Button, Need to be deleted
        final Button org_home = findViewById(R.id.Org_home);
        org_home.setOnClickListener(new View.OnClickListener() {
            String deviceID = "56d8904ace9c2275";
            public void onClick(View v) {
                FirebaseAttendeeManager firebaseAttendeeManager = new FirebaseAttendeeManager();
                Task<String> getEventID = firebaseAttendeeManager.getEventID(deviceID);
                getEventID.addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String eventID) {
                        Intent intent = new Intent(MainActivity.this, HomeScreenActivity.class);
                        Bundle b = new Bundle();
                        b.putString("role", "org");
                        b.putString("eventID", eventID);
                        intent.putExtras(b);
                        startActivity(intent);
                    }
                });
            }
        });


        //  TODO: Demo Button, Need to be deleted
        final Button qrScanScreen = findViewById(R.id.QR_Screen);
        qrScanScreen.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ScanQRActivity.class);
                startActivity(intent);
            }
        });
    }
}