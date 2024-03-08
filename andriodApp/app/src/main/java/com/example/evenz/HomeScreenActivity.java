package com.example.evenz;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Objects;

public class HomeScreenActivity extends AppCompatActivity {
    private ImageView eventPoster;
    private TextView eventLocation, eventDetail;
    private RecyclerView notificationsRecyclerView;
    private NotificationsAdapter notificationsAdapter;

    // Replace with the actual event ID for the home screen
    private String specificEventId;
    final String eventID = "aoNmFEvMEuyjE1wHsTw1";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle b = getIntent().getExtras();
        assert b != null;
        String role = b.getString("role");

        specificEventId = getEventIdForHomeScreen();

        if (Objects.equals(role, "attendee")) {
            setContentView(R.layout.attendees_home_page);

            notificationsRecyclerView = findViewById(R.id.notificationsRecyclerView);
            notificationsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

            eventPoster = findViewById(R.id.attendee_home_event_poster);
            eventLocation = findViewById(R.id.attendee_home_event_location);
            eventDetail = findViewById(R.id.attendee_home_event_detail);

            if (!specificEventId.isEmpty()) {
                fetchEventDetailsAndNotifications(specificEventId);
            }

            ImageView browseEvent = findViewById(R.id.event_list);
            browseEvent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(HomeScreenActivity.this, EventBrowseActivity.class));
                }
            });

            ImageView eventPoster = findViewById(R.id.attendee_home_event_poster);
            eventPoster.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(HomeScreenActivity.this, AttendeeEventInfoActivity.class));
                }
            });

        } else {
            setContentView(R.layout.org_home_page);

            notificationsRecyclerView = findViewById(R.id.notificationsRecyclerView);
            notificationsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

            eventPoster = findViewById(R.id.org_home_event_poster);
            eventLocation = findViewById(R.id.org_home_event_location);
            eventDetail = findViewById(R.id.org_home_event_detail);

            if (!specificEventId.isEmpty()) {
                fetchEventDetailsAndNotifications(specificEventId);
            }

            FloatingActionButton postNotification = findViewById(R.id.add_fab);
            postNotification.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(HomeScreenActivity.this, OrgSendNotificationActivity.class);
                    Bundle b = new Bundle();
                    b.putString("eventID", eventID);
                    intent.putExtras(b);
                    startActivity(intent);
                }
            });

            ImageView shareQR = findViewById(R.id.shareQR);
            shareQR.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    QRGenerator test = new QRGenerator();
                    Bitmap bitmap = test.generate(eventID, 400, 400);

                    Intent intent = new Intent(HomeScreenActivity.this, ShareQRActivity.class);
//                    intent.putExtra("BitmapImage", bitmap);
                    startActivity(intent);
//                    ImageView qrimg = findViewById(R.id.QRcode);
//                    qrimg.setImageBitmap(finals);
                }
            });

        }
    }

    private void fetchEventDetailsAndNotifications(String eventId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("events").document(eventID).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot != null && documentSnapshot.exists()) {
                Event event = documentSnapshot.toObject(Event.class);
                // Directly update the TextView with the event's location

                if (event != null) {
                    eventDetail.setText("\uD83D\uDC4B Welcome to " + event.getEventName() + "! \uD83D\uDE80");
                    eventLocation.setText(event.getLocation());
                    displayImage(event.getEventPosterID(), eventPoster);

                    ArrayList<String> notifications = event.getNotifications(); // Assuming this correctly fetches the notifications
                    if (notifications != null) {
                        notificationsAdapter = new NotificationsAdapter(HomeScreenActivity.this, notifications);
                        notificationsRecyclerView.setAdapter(notificationsAdapter);
                    }
                }
                notificationsAdapter.notifyDataSetChanged();
            } else {
                // TODO: Handle the case where the event doesn't exist in the database
            }
        }).addOnFailureListener(e -> {
            // TODO: handle errors
        });
    }

    private String getEventIdForHomeScreen() { //TODO: Implement this method populating the event ID
        // Placeholder method to obtain the event ID
        // Implement this to retrieve the event ID for the home screen
        return "your_specific_event_id";
    }
    private void displayImage(String imageID, ImageView imgView)
    {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference photoReference= storageReference.child("images/" + imageID);

        final long ONE_MEGABYTE = 1024 * 1024;
        photoReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                imgView.setImageBitmap(bmp);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(HomeScreenActivity.this, "No Such file or Path found!!", Toast.LENGTH_LONG).show();
            }
        });
    }
}

