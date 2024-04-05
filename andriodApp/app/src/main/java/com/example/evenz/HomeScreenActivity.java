package com.example.evenz;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class HomeScreenActivity extends AppCompatActivity {
    private ImageView eventPoster;
    private TextView eventLocation, eventDetail;
    private RecyclerView notificationsRecyclerView;
    private NotificationsAdapter notificationsAdapter;
    private String eventID;
    private String role; // This is the role of the user, either "attendee" or "organizer"

    private FirebaseFirestore db;
    private CollectionReference usersRef;
    private DocumentReference doc;

    private ImageUtility imageUtility;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Extracting the role and eventID from the intent extras
        Bundle b = getIntent().getExtras();
        assert b != null;
        role = b.getString("role");
        eventID = b.getString("eventID");


        // Checking if the role is "attendee" and setting the appropriate layout
        if (Objects.equals(role, "attendee")) {
            setContentView(R.layout.attendees_home_page);
            setupAttendeeView();
        } else {
            setContentView(R.layout.org_home_page);
            setupOrganizerView();
        }

        imageUtility = new ImageUtility();
    }

    // This method sets up the view for the attendee
    private void setupAttendeeView() {
        notificationsRecyclerView = findViewById(R.id.notificationsRecyclerView);
        notificationsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        eventPoster = findViewById(R.id.attendee_home_event_poster);
        eventLocation = findViewById(R.id.attendee_home_event_location);
        eventDetail = findViewById(R.id.attendee_home_event_detail);

        // Fetch event details and notifications only if eventID is not null and not empty
        if (eventID != null && !Objects.equals(eventID, " ")) {
            fetchEventDetailsAndNotifications(eventID);
        }
        ImageView imageEllipse = findViewById(R.id.image_ellipse);
        imageEllipse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeScreenActivity.this, ScanQRActivity.class);
                startActivity(intent);
            }
        });

        ImageView browseEvent = findViewById(R.id.event_list);
        browseEvent.setOnClickListener(v -> {
            Intent intent = new Intent(HomeScreenActivity.this, EventBrowseActivity.class);
            intent.putExtra("eventID", eventID);
            intent.putExtra("role", role);
            startActivity(intent);
        });
        eventPoster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openEventDetailsIntent(eventID, "attendee");
            }
        });

        eventLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMapIntent(eventID, "attendee", (String) eventLocation.getText());
            }
        });

        ImageView profileAttendee = findViewById(R.id.profile_attendee);
        profileAttendee.setOnClickListener(v -> {
            Intent intent = new Intent(HomeScreenActivity.this, UserEditProfileActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("eventID", eventID);
            bundle.putString("role", "attendee");
            intent.putExtras(bundle);
            startActivity(intent);
        });
    }

    // This method sets up the view for the organizer
    private void setupOrganizerView() {
        notificationsRecyclerView = findViewById(R.id.notificationsRecyclerView);
        notificationsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        eventPoster = findViewById(R.id.org_home_event_poster);
        eventLocation = findViewById(R.id.org_home_event_location);
        eventDetail = findViewById(R.id.org_home_event_detail);

        // Fetch event details and notifications only if eventID is not null and not empty
        if (eventID != null && !eventID.isEmpty()) {
            fetchEventDetailsAndNotifications(eventID);
        }

        FloatingActionButton postNotification = findViewById(R.id.add_fab);
        postNotification.setOnClickListener(v -> {
            Intent intent = new Intent(HomeScreenActivity.this, OrgSendNotificationActivity.class);
            Bundle b = new Bundle();
            b.putString("eventID", eventID);
            intent.putExtras(b);
            startActivity(intent);
        });

        ImageView shareQR = findViewById(R.id.shareQR);
        shareQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                QRGenerator test = new QRGenerator();
                Bitmap bitmap = test.generate(eventID, "SignUp", 400, 400);
                Uri bitmapUri = saveBitmapToCache(bitmap);

                Intent intent = new Intent(HomeScreenActivity.this, ShareQRActivity.class);

                intent.putExtra("eventID", eventID);
                intent.putExtra("BitmapImage", bitmapUri.toString());
                startActivity(intent);
            }
        });

        eventPoster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openEventDetailsIntent(eventID, "organizer");
            }
        });
        eventLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMapIntent(eventID, "organizer", (String) eventLocation.getText());
            }
        });
    }

    private void openMapIntent(String eventID, String role, String addressString) {
        Intent intent = new Intent(HomeScreenActivity.this, MapsActivity.class);

        intent.putExtra("eventID", eventID);
        intent.putExtra("role", role);
        intent.putExtra("addressString", addressString);
        intent.putExtra("from", "homeScreen");
        startActivity(intent);
    }

    private void openEventDetailsIntent(String eventID, String role) {
        Intent intent = new Intent(HomeScreenActivity.this, EventDetailsActivity.class);
        Bundle b = new Bundle();
        b.putString("role", role);
        b.putString("eventID", eventID);
        intent.putExtra("source", "homepage");
        intent.putExtras(b);
        startActivity(intent);
    }

    // This method saves the bitmap to cache and returns the Uri
    private Uri saveBitmapToCache(Bitmap bitmap) {
        try {
            File cachePath = new File(getCacheDir(), "images");
            cachePath.mkdirs(); // don't forget to make the directory
            FileOutputStream stream = new FileOutputStream(cachePath + "/image.png"); // overwrites this image every time
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.close();

            File imagePath = new File(getCacheDir(), "images");
            File newFile = new File(imagePath, "image.png");
            return Uri.fromFile(newFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // This method fetches event details and notifications from Firestore
    private void fetchEventDetailsAndNotifications(String eventId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("events").document(eventId).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot != null && documentSnapshot.exists()) {
                Event event = documentSnapshot.toObject(Event.class);

                if (event != null) {
                    eventDetail.setText("\uD83D\uDC4B Welcome to " + event.getEventName() + "! \uD83D\uDE80");
                    eventLocation.setText(event.getLocation());
                    imageUtility.displayImage(event.getEventPosterID(), eventPoster);

                    ArrayList<String> notifications = event.getNotifications(); // Assuming this correctly fetches the notifications
                    if (notifications != null) {
                        notificationsAdapter = new NotificationsAdapter(HomeScreenActivity.this, notifications);
                        notificationsRecyclerView.setAdapter(notificationsAdapter);
                        notificationsAdapter.notifyDataSetChanged();
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