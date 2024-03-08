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

public class OrgSendNotificationActivity extends AppCompatActivity {
    TextView post;
    String eventID;
    EditText editTextNotificationType, editTextNotificationInfo;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle b = getIntent().getExtras();
        assert b != null;
        eventID = b.getString("eventID");

        setContentView(R.layout.org_send_event_notification);
        post = findViewById(R.id.post_notification_button);
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initUI();
                postNotificaion();
                Intent intent = new Intent(new Intent(OrgSendNotificationActivity.this, HomeScreenActivity.class));
                Bundle b = new Bundle();
                b.putString("role", "org");
                intent.putExtras(b);
                startActivity(intent);
                startActivity(new Intent(OrgSendNotificationActivity.this, HomeScreenActivity.class));
            }
        });

        findViewById(R.id.back_send_notification).setOnClickListener(v -> finish());
    }

    private void initUI() {
        editTextNotificationType = findViewById(R.id.editTextNotificationType);
        editTextNotificationInfo = findViewById(R.id.editTextNotificationInfo);
    }

    private void postNotificaion () {
        String notificationType = editTextNotificationType.getText().toString().trim();
        String notificationInfo = editTextNotificationInfo.getText().toString().trim();

        fetchEventDetailsAndNotifications(eventID, notificationType, notificationInfo);
    }

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