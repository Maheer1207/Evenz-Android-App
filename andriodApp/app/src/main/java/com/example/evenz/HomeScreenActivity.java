package com.example.evenz;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
/**
 * HomeScreenActivity is the main activity for the home screen of the attendees' app.
 * It displays event details, such as the event poster, location, and additional notifications.
 *
 * This activity includes functionality to fetch event details and notifications from
 * the Firestore database and display them on the home screen.
 *
 * The class uses a specific event ID, obtained by the getEventIdForHomeScreen() method,
 * to fetch details for the home screen. The fetched details include the event name, location,
 * event poster, and notifications, which are displayed using various UI elements such as
 * TextViews, ImageViews, and a RecyclerView.
 *
 * @author hrithick
 * @version 1.0
 */
public class HomeScreenActivity extends AppCompatActivity {
    private ImageView eventPoster;
    private TextView eventLocation, eventDetail;
    private RecyclerView notificationsRecyclerView;
    private NotificationsAdapter notificationsAdapter;

    // Replace with the actual event ID for the home screen
    private String specificEventId;
    /**
     * Called when the activity is first created.
     * Initializes UI elements and fetches event details and notifications.
     *
     * @param savedInstanceState The saved state of the activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendees_home_page);

        notificationsRecyclerView = findViewById(R.id.notificationsRecyclerView);
        notificationsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        eventPoster = findViewById(R.id.attendee_home_event_poster);
        eventLocation = findViewById(R.id.attendee_home_event_location);
        eventDetail = findViewById(R.id.attendee_home_event_detail);

        specificEventId = getEventIdForHomeScreen();

        if (specificEventId != null && !specificEventId.isEmpty()) {
            fetchEventDetailsAndNotifications(specificEventId);
        }
    }

    /**
     * Fetches event details and notifications from Firestore.
     *
     * @param eventId The ID of the event to fetch details for.
     */
    private void fetchEventDetailsAndNotifications(String eventId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("events").document("sTxGMBeN1Fnw1Slau2j5").get().addOnSuccessListener(documentSnapshot -> {
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
            } else {
                // TODO: Handle the case where the event doesn't exist in the database
            }
        }).addOnFailureListener(e -> {
            // TODO: handle errors
        });
    }
    /**
     * Retrieves the event ID for the home screen.
     *
     * @return The event ID for the home screen.
     */
    private String getEventIdForHomeScreen() { //TODO: Implement this method populating the event ID
        // Placeholder method to obtain the event ID
        // Implement this to retrieve the event ID for the home screen
        return "your_specific_event_id";
    }

    /**
     * Displays the image associated with the given image ID in the specified ImageView.
     *
     * @param imageID The ID of the image to display.
     * @param imgView The ImageView to display the image.
     */
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

