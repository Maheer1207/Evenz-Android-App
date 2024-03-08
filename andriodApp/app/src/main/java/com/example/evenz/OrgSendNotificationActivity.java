package com.example.evenz;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.util.Log;
import androidx.media3.common.util.UnstableApi;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
/**
 * Activity for organizers to send notifications related to a specific event.
 * This activity allows organizers to post notifications for attendees of a particular event,
 * providing details such as notification type and information.
 *
 * <p>The class includes methods to initialize UI components, handle notification posting,
 * and fetch event details and notifications from Firestore.
 *
 * @see AppCompatActivity
 */
public class OrgSendNotificationActivity extends AppCompatActivity {
    TextView post;
    EditText editTextNotificationType, editTextNotificationInfo;
    final String eventID = "sTxGMBeN1Fnw1Slau2j5";
    /**
     * Called when the activity is first created. Initializes the activity's UI,
     * sets click listeners, and handles notification posting when the "Post" button is clicked.
     *
     * @param savedInstanceState A Bundle containing the activity's previously saved state, if any.
     *                           If null, this is the first time the activity is being created.
     * @see AppCompatActivity#onCreate(Bundle)
     */
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.org_send_event_notification);
        post = findViewById(R.id.post_notification_button);
        post.setOnClickListener(new View.OnClickListener() {
            /**
             * Handles the click event when the "Post" button is clicked.
             * Invokes the initialization of UI components and triggers the posting of the entered notification.
             *
             * @param v The View that was clicked (in this case, the "Post" button).
             * @see View.OnClickListener#onClick(View)
             */
            @Override
            public void onClick(View v) {
                initUI();
                postNotificaion();
                startActivity(new Intent(OrgSendNotificationActivity.this, MainActivity.class));
            }
        });

        findViewById(R.id.back_send_notification).setOnClickListener(v -> finish());
    }

    //    public OrgSendNotificationActivity(String eventId){
//        super();
//    }
    /**
     * Initializes the UI components such as notification type and information fields.
     */
    private void initUI() {
        editTextNotificationType = findViewById(R.id.editTextNotificationType);
        editTextNotificationInfo = findViewById(R.id.editTextNotificationInfo);
    }
    /**
     * Posts the entered notification by fetching event details and updating the notifications list in Firestore.
     */
    private void postNotificaion () {
        String notificationType = editTextNotificationType.getText().toString().trim();
        String notificationInfo = editTextNotificationInfo.getText().toString().trim();

        fetchEventDetailsAndNotifications(eventID, notificationType, notificationInfo);
    }
    /**
     * Fetches event details and notifications from Firestore, adds the new notification, and updates the Firestore document.
     *
     * @param eventID           The ID of the event to which the notification is related.
     * @param notificationType The type of the notification (e.g., announcement, update).
     * @param notificationInfo Additional information or details about the notification.
     */
    private void fetchEventDetailsAndNotifications(String eventID, String notificationType,
                                                   String notificationInfo) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference specificEventRef = db.collection("events").document(eventID);
        specificEventRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot != null && documentSnapshot.exists()) {
                Event event = documentSnapshot.toObject(Event.class);
                // Directly update the TextView with the event's location

                if (event != null) {
                    ArrayList<String> notifications = event.getNotifications(); // Assuming this correctly fetches the notifications
                    if (notifications != null) {
                        notifications.add(notificationType+", "+notificationInfo);

                        Map<String, Object> eventMap = new HashMap<>();
                        eventMap.put("organizationName", event.getOrganizationName());
                        eventMap.put("eventName", event.getEventName());
                        eventMap.put("description", event.getDescription());
                        eventMap.put("AttendLimit", event.getEventAttendLimit());
                        eventMap.put("eventDate", event.getEventDate());
                        eventMap.put("location", event.getLocation());
                        eventMap.put("eventPosterID", event.getEventPosterID());
                        eventMap.put("notifications", notifications);
                        specificEventRef.set(eventMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(OrgSendNotificationActivity.this, "Course has been updated..", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @OptIn(markerClass = UnstableApi.class)
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Handle the error
                                        Log.w("Firestore", "Error adding document", e);
                                    }
                                });
                    }
                }
            } else {
                // TODO: Handle the case where the event doesn't exist in the database
            }
        }).addOnFailureListener(e -> {
            // TODO: handle errors
        });
    }
}