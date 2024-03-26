package com.example.evenz;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Objects;

public class EventDetailsActivity  extends AppCompatActivity {
    private String eventID;
    private ImageView eventPoster, backButton;
    private TextView eventLocation, eventDetail;

    private NotificationsAdapter notificationsAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle b = getIntent().getExtras();
        assert b != null;
        String role = b.getString("role");
        eventID = b.getString("eventID");

        if (Objects.equals(role, "attendee")) {
            setContentView(R.layout.attendee_event_info_sign_up);

            eventPoster = findViewById(R.id.poster_attendee_eventInfo);
            eventLocation = findViewById(R.id.loc_attendee_eventInfo);
            eventDetail = findViewById(R.id.info_attendee_eventInfo);
            backButton = findViewById(R.id.back_attendee_event_info_signup);
            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(new Intent(EventDetailsActivity.this, HomeScreenActivity.class));
                    Bundle b = new Bundle();
                    b.putString("role", "attendee");
                    b.putString("eventID", eventID);
                    intent.putExtras(b);
                    startActivity(intent);
                }
            });

        } else {
            setContentView(R.layout.org_event_info);

            eventPoster = findViewById(R.id.poster_org_eventInfo);
            eventLocation = findViewById(R.id.loc_org_eventInfo);
            eventDetail = findViewById(R.id.info_org_eventInfo);
            backButton = findViewById(R.id.back_org_event_info);
            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(new Intent(EventDetailsActivity.this, HomeScreenActivity.class));
                    Bundle b = new Bundle();
                    b.putString("role", "organizer");
                    b.putString("eventID", eventID);
                    intent.putExtras(b);
                    startActivity(intent);
                }
            });
        }
        if (!eventID.isEmpty()) {
            fetchEventDetailsAndNotifications(eventID);
        }
    }

    private void fetchEventDetailsAndNotifications(String eventId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("events").document(eventId).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot != null && documentSnapshot.exists()) {
                Event event = documentSnapshot.toObject(Event.class);
                // Directly update the TextView with the event's location

                if (event != null) {
                    eventDetail.setText(event.getDescription());
                    eventLocation.setText(event.getLocation());
                    displayImage(event.getEventPosterID(), eventPoster);
                }
            } else {
                // TODO: Handle the case where the event doesn't exist in the database
            }
        }).addOnFailureListener(e -> {
            // TODO: handle errors
        });
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
                Toast.makeText(EventDetailsActivity.this, "No Such file or Path found!!", Toast.LENGTH_LONG).show();
            }
        });
    }
}
