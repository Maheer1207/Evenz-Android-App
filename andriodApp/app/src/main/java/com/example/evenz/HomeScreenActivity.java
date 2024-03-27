package com.example.evenz;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileNotFoundException;
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
    private FirebaseFirestore db;
    private CollectionReference usersRef;
    private DocumentReference doc;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle b = getIntent().getExtras();
        assert b != null;
        String role = b.getString("role");
        eventID = b.getString("eventID");

        if (Objects.equals(role, "attendee")) {
            setContentView(R.layout.attendees_home_page);

            notificationsRecyclerView = findViewById(R.id.notificationsRecyclerView);
            notificationsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

            eventPoster = findViewById(R.id.attendee_home_event_poster);
            eventLocation = findViewById(R.id.attendee_home_event_location);
            eventDetail = findViewById(R.id.attendee_home_event_detail);

            if (!eventID.isEmpty()) {
                fetchEventDetailsAndNotifications(eventID);
            }

            ImageView browseEvent = findViewById(R.id.event_list);
            browseEvent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(HomeScreenActivity.this, EventBrowseActivity.class));
                }
            });

            eventPoster.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(HomeScreenActivity.this, EventDetailsActivity.class);
                    Bundle b = new Bundle();
                    b.putString("role", "attendee");
                    b.putString("eventID", eventID);
                    intent.putExtras(b);
                    startActivity(intent);
                }
            });

        } else {
            setContentView(R.layout.org_home_page);

            notificationsRecyclerView = findViewById(R.id.notificationsRecyclerView);
            notificationsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

            eventPoster = findViewById(R.id.org_home_event_poster);
            eventLocation = findViewById(R.id.org_home_event_location);
            eventDetail = findViewById(R.id.org_home_event_detail);

            if (!eventID.isEmpty()) {
                fetchEventDetailsAndNotifications(eventID);
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
                    Intent intent = new Intent(HomeScreenActivity.this, EventDetailsActivity.class);
                    Bundle b = new Bundle();
                    b.putString("role", "organizer");
                    b.putString("eventID", eventID);
                    intent.putExtras(b);
                    startActivity(intent);
                }
            });

        }
    }

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
    private void fetchEventDetailsAndNotifications(String eventId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("events").document(eventId).get().addOnSuccessListener(documentSnapshot -> {
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

