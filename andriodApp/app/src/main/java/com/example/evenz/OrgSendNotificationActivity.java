package com.example.evenz;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.util.Log;
import androidx.media3.common.util.UnstableApi;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.ArrayList;

public class OrgSendNotificationActivity extends AppCompatActivity {
    TextView post;
    EditText editTextNotificationType, editTextNotificationInfo;
    final String eventID = "sTxGMBeN1Fnw1Slau2j5";
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.org_send_event_notification);
        post = findViewById(R.id.post_notification_button);
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initUI();
                postNotificaion();
            }
        });

        findViewById(R.id.back_send_notification).setOnClickListener(v -> finish());
    }

//    public OrgSendNotificationActivity(String eventId){
//        super();
//    }
    private void initUI() {
        editTextNotificationType = findViewById(R.id.editTextNotificationType);
        editTextNotificationInfo = findViewById(R.id.editTextNotificationInfo);
    }

    private void postNotificaion () {
        String notificationType = editTextNotificationType.getText().toString().trim();
        String notificationInfo = editTextNotificationInfo.getText().toString().trim();

        if (eventID != null && eventID.isEmpty()) {
            fetchEventDetailsAndNotifications(eventID, notificationType, notificationInfo);
        }
    }

    private void fetchEventDetailsAndNotifications(String eventID, String notificationType,
                                                   String notificationInfo) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("events").document(eventID).get()
                .addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot != null && documentSnapshot.exists()) {
                Event event = documentSnapshot.toObject(Event.class);
                // Directly update the TextView with the event's location

                if (event != null) {
                    ArrayList<String> notifications = event.getNotifications(); // Assuming this correctly fetches the notifications
                    if (notifications != null) {
                        notifications.add(notificationType+","+notificationInfo);
                        event.setNotifications(notifications);
                        db.collection("events").add(event).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @OptIn(markerClass = UnstableApi.class)
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        // Successfully added event with auto-generated ID
                                        Log.d("Firestore", "DocumentSnapshot added with ID: " + documentReference.getId());
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