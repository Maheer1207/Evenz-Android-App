package com.example.evenz;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class HomeScreenActivity extends AppCompatActivity {
    private ImageView eventPoster;
    private TextView eventLocation, eventDetail;
    private RecyclerView notificationsRecyclerView;
    private NotificationsAdapter notificationsAdapter;
    private EventHomeDetailsAdapter eventHomeDetailsAdapter;
    // Replace with the actual event ID for the home screen
    private String specificEventId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendees_home_page);

        eventDetailTextView = findViewById(R.id.text_event_location); // Initialize the TextView after setting content view

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


    private void fetchEventDetailsAndNotifications(String eventId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("events").document("aoNmFEvMEuyjE1wHsTw1").get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot != null && documentSnapshot.exists()) {
                Event event = documentSnapshot.toObject(Event.class);
                // Directly update the TextView with the event's location

                if (event != null) {
                    eventDetail.setText(event.getDescription());
                    eventLocation.setText(event.getLocation());
                }

                ArrayList<String> notifications = event.getNotificationList(); // Assuming this correctly fetches the notifications
                if (notifications != null) {
                    notificationsAdapter = new NotificationsAdapter(HomeScreenActivity.this, notifications);
                    notificationsRecyclerView.setAdapter(notificationsAdapter);
                }
            } else {
                // TODO: Handle the case where the event doesn't exist in the database
            }
        }).addOnFailureListener(e -> {
            // TODO: handle errors
        });
    }

    private String getEventIdForHomeScreen() {
        // Placeholder method to obtain the event ID
        // Implement this to retrieve the event ID for the home screen
        return "your_specific_event_id";
    }
}

