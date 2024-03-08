package com.example.evenz;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

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
/**
 * MainActivity serves as the main entry point for the application. It initializes and manages
 * the connection to Firestore, retrieves and updates event and user data, and populates corresponding
 * data lists for display.
 *
 * <p>The class utilizes Firestore's addSnapshotListener to keep the local eventDataList and userDataList
 * updated with real-time changes in the "events" and "users" collections. The fetched data is stored in
 * Event and User objects, respectively.
 *
 * <p>Important Note: The class assumes the presence of specific fields in the Firestore documents such as
 * "organizationName," "eventName," "eventPosterID," "description," "geolocation," "qrCodeBrowse," "qrCodeIn,"
 * "AttendLimit," "eventDate," "location" for events, and "name," "profilePicID," "phone," "email," "userId," "userType"
 * for users. Make sure the Firestore schema aligns with these assumptions.
 *
 * @see Event
 * @see User
 */
public class MainActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private CollectionReference eventsRef;
    private CollectionReference usersRef;

    private ArrayList<Event> eventDataList;
    private ArrayList<User> userDataList;
    /**
     * Initializes the activity, sets up Firestore connections, and registers listeners to keep
     * the local data lists updated with changes in the "events" and "users" collections.
     *
     * @param savedInstanceState A Bundle object containing the activity's previously saved state, or null if unavailable.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        eventDataList = new ArrayList<>();
        userDataList = new ArrayList<>();
        
        db = FirebaseFirestore.getInstance();
        eventsRef = db.collection("events");
        usersRef = db.collection("users");

        /**
         * Called when there is a change in the "events" collection in Firestore. Updates the local eventDataList
         * with the latest event data fetched from the Firestore database.
         *
         * @param querySnapshots A snapshot of the "events" collection containing the updated event data.
         * @param error          An exception that occurred during the event listener execution, if any.
         *                      If null, no errors occurred.
         *
         * @see Event
         */
        eventsRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshots,
                                @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e("Firestore", error.toString());
                    return;
                }

                if (querySnapshots != null) {
                    eventDataList.clear();
                    for (QueryDocumentSnapshot doc: querySnapshots) {
                        String eventID = doc.getId(); //TODO: convert qrcode browse getLong to getInt

                        Object attendLimitObj = doc.get("AttendLimit");
                        long eventAttendLimit = attendLimitObj != null ? (Long) attendLimitObj : 0; // 0 is a default value
                        
                        // Get the Timestamp object from the document
                        Timestamp timestamp = doc.getTimestamp("eventDate");

                        //Convert the Timestamp to a java.util.Date object
                        Date eventDate = null;
                        if (timestamp != null) {
                            eventDate = timestamp.toDate(); // converts Timestamp to Date
                        }

                        Event tempEvent = new Event(doc.getString("organizationName"), doc.getString("eventName"), doc.getString("eventPosterID"),
                                doc.getString("description"), (Geolocation)doc.get("geolocation"), (Bitmap)doc.get("qrCodeBrowse"),
                                (Bitmap)doc.get("qrCodeIn"), (int)eventAttendLimit,
                                new Hashtable<>(), eventDate, new ArrayList<String>(), doc.getString("location")); //TODO: review if this is correct implementation

                        Log.d("Firestore", String.format("Event(%s, %s) fetched", eventID, tempEvent.getEventName()));
                        eventDataList.add(tempEvent);
                    }
                }
            }
        });

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
        final Button attendee_event_info = findViewById(R.id.button_attendee_event_info);
        attendee_event_info.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AttendeeEventInfoActivity.class);
                startActivity(intent);
            }
        });

        //  TODO: Demo Button, Need to be deleted
        final Button attendee_event_info_signup = findViewById(R.id.button_attendee_event_info_signup);
        attendee_event_info_signup.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AttendeeEventInfoSignUpActivity.class);
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
        final Button sendNotification = findViewById(R.id.send_notification);
        sendNotification.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, OrgSendNotificationActivity.class);
                startActivity(intent);
            }
        });

        //  TODO: Demo Button, Need to be deleted
        final Button attendee_home = findViewById(R.id.attendee_home);
        attendee_home.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, HomeScreenActivity.class);
                startActivity(intent);
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
        /**
         * Called when there is a change in the "users" collection in Firestore. Updates the local userDataList
         * with the latest user data fetched from the Firestore database.
         *
         * @param querySnapshots A snapshot of the "users" collection containing the updated user data.
         * @param error          An exception that occurred during the event listener execution, if any.
         *                      If null, no errors occurred.
         *
         * @see User
         */
        usersRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshots,
                                @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e("Firestore", error.toString());
                    return;
                }
                if (querySnapshots != null) {
                    userDataList.clear();
                    for (QueryDocumentSnapshot doc: querySnapshots) {
                        String userID = doc.getId();
                        User tempUser = new User(doc.getString("name"), doc.getString("profilePicID"),
                                doc.getString("phone"), doc.getString("email"), doc.getString("userId"), doc.getString("userType"));
                        userDataList.add(tempUser);
                    }
                }
            }
        });
    }

    /**
     * This function adds a user to the database
     * @param id The id of the user
     * @param user The contents of the user (all variables from user class)
     */
    private void addUser(String id, User user) {
        HashMap<String, User> data = new HashMap<>();
        data.put(id, user);
        usersRef.document(id).set(data);
    }

    /**
     * This function deletes a user from the database
     * @param id The id of the user to be deleted
     */
    private void deleteUser(String id)
    {
        usersRef.document(id).delete();
    }

    /**
     * This function adds an event to the database
     * @param id The id of the event to be added
     * @param event The contents of the event (all variables from event class)
     */
    private void addEvent(String id, Event event) {
        HashMap<String, Event> data = new HashMap<>();
        data.put(id, event);
        eventsRef.document(id).set(data);
    }

    /**
     * This function deletes an event from the database
     * @param id The id of the event to be deleted
     */
    private void deleteEvent(String id)
    {
        usersRef.document(id).delete();
    }

}