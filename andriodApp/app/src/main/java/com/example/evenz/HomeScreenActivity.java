package com.example.evenz;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
    private String role;
    private ImageUtility imageUtility;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // getting the devices id to get the user
        String deviceID = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        FirebaseUserManager firebaseUserManager = new FirebaseUserManager();
        // getting the user type
        Task<String> getUserType = firebaseUserManager.getUserType(deviceID);
        getUserType.addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String type) {
                role = type;

                // getting the signed in event or organized event id
                Task<String> getEventID;
                if (role.equals("attendee")) {
                    getEventID = firebaseUserManager.getEventIDAttendee(deviceID);
                } else {
                    getEventID = firebaseUserManager.getEventIDOrg(deviceID);
                }
                getEventID.addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String ID) {
                        eventID = ID;
                        // If the user has no event in the event
                        if (eventID == null || eventID.equals("N")) {
                            setContentView(R.layout.attendees_home_page);
                            populateEmptyAttendeeFields();
                        }
                        // Checking if the role is "attendee" and setting the layout
                        else if (role.equals("attendee")) {
                            setContentView(R.layout.attendees_home_page);
                            populateAttendeeFields();
                            // User is organizer setting appropriate layout
                        } else {
                            setContentView(R.layout.org_home_page);
                            setupOrganizerView();
                        }

                        imageUtility = new ImageUtility();
                    }
                });
            }
        });
    }

    private void populateAttendeeFields() {
        notificationsRecyclerView = findViewById(R.id.notificationsRecyclerView);
        notificationsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        eventPoster = findViewById(R.id.attendee_home_event_poster);
        eventLocation = findViewById(R.id.attendee_home_event_location);
        eventDetail = findViewById(R.id.attendee_home_event_detail);

        // Fetch event details and notifications only if eventID is not null and not empty
        if (eventID != null && !Objects.equals(eventID, " ")) {
            fetchEventDetailsAndNotifications(eventID);
        }

        setupAttendeeView();
    }

    private void populateEmptyAttendeeFields() {
        notificationsRecyclerView = findViewById(R.id.notificationsRecyclerView);
        notificationsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        eventPoster = findViewById(R.id.attendee_home_event_poster);
        eventLocation = findViewById(R.id.attendee_home_event_location);
        eventDetail = findViewById(R.id.attendee_home_event_detail);

        eventPoster.setScaleType(ImageView.ScaleType.FIT_CENTER);
        eventDetail.setText("\uD83D\uDC4B Welcome to Evenz! \uD83D\uDE80");

        eventLocation.setVisibility(View.INVISIBLE);
        notificationsRecyclerView.setVisibility(View.INVISIBLE);

        findViewById(R.id.text_notifications).setVisibility(View.INVISIBLE);
        findViewById(R.id.location_pin_attendee_home).setVisibility(View.INVISIBLE);

        setupAttendeeView();
    }

    // This method sets up the view for the attendee
    private void setupAttendeeView() {

        ImageView imageEllipse = findViewById(R.id.image_ellipse);
        imageEllipse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeScreenActivity.this, ScanQRActivity.class);
                startActivity(intent);
            }
        });

        ImageView browseEvent = findViewById(R.id.event_list);
        browseEvent.setOnClickListener(v -> startActivity(new Intent(HomeScreenActivity.this, EventBrowseActivity.class)));

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

        //updated share QR option to have promotional and check-in QR code options
        ImageView shareQR = findViewById(R.id.shareQR);
        shareQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an AlertDialog.Builder
                AlertDialog.Builder builder = new AlertDialog.Builder(HomeScreenActivity.this);
                builder.setTitle("Choose QR Code Type")
                        .setItems(new String[]{"Promotional QR code", "Check-in QR code"}, (dialog, which) -> {
                            String qrCodeType =(which == 0) ? "/sign_up" : "/check_in"; // 0 for promotional, 1 for check-in

                            QRGenerator test = new QRGenerator();
                            Bitmap bitmap = test.generate(eventID, qrCodeType, 400, 400);//generate with a string that we can parse
                            Uri bitmapUri = saveBitmapToCache(bitmap);

                            Intent intent = new Intent(HomeScreenActivity.this, ShareQRActivity.class);
                            intent.putExtra("eventID", eventID);
                            if(which == 0) {
                                intent.putExtra("qrCodeType", "Promotional");
                            } else {
                                intent.putExtra("qrCodeType", "Check-in");
                            }
                            assert bitmapUri != null;
                            intent.putExtra("BitmapImage", bitmapUri.toString());
                            startActivity(intent);
                        });
                // Create and show the AlertDialog
                builder.create().show();
            }
        });


        ImageView viewAttendees = findViewById(R.id.profile_attendee);
        viewAttendees.setOnClickListener(v -> {
            Intent intent = new Intent(HomeScreenActivity.this, AttendeesActivity.class);
            startActivity(intent);
        });

        eventPoster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openEventDetailsIntent(eventID, role);
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
