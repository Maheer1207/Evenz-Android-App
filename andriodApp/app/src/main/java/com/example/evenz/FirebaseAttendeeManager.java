package com.example.evenz;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class FirebaseAttendeeManager {



    // Firestore instance
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference ref = db.collection("attendees");

    // Create another constructor that will take no parameters
    public FirebaseAttendeeManager() {
    }

    // Create a basic constructor that takes a Firestore instance as a parameter
    public FirebaseAttendeeManager(FirebaseFirestore db) {
        this.db = db;
    }
    public void submitAttendee(Attendee attendee) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Convert Attendee object to a Map
        Map<String, Object> attendeeMap = new HashMap<>();
        attendeeMap.put("name", attendee.getName());
        attendeeMap.put("profilePicID", attendee.getProfilePicID());
        attendeeMap.put("phone", attendee.getPhone());
        attendeeMap.put("email", attendee.getEmail());
        attendeeMap.put("geolocation", attendee.getGeolocation() != null ? attendee.getGeolocation() : null);
        attendeeMap.put("notification", attendee.isNotifications());
        attendeeMap.put("eventList", attendee.getEventList());

        // It's better to use a unique ID instead of the name to avoid overwriting documents
        // with the same name. The following line generates a new document ID.
        // If you want to use a specific property as the ID, you can set it instead of 'newId'.
        String documentId = db.collection("attendee").document().getId();

        // You can also use attendee.getName() if you're sure it will be unique
        // Or use another unique identifier

        db.collection("attendee").document(documentId).set(attendeeMap)
                .addOnSuccessListener(aVoid -> Log.d("submitAttendee", "DocumentSnapshot successfully written!"))
                .addOnFailureListener(e -> Log.w("submitAttendee", "Error writing document", e));
    }




    // Create a method to fetch all attendees from Firestore and store them in a list that is then reutrned to the caller

    public Task<List<Attendee>> getAllAttendeesAsynchronously() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final List<Attendee> attendeesList = new ArrayList<>();

        return db.collection("attendee").get().continueWith(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                    Attendee attendee = new Attendee(
                            document.getString("name"),
                            document.getString("profilePicID"),
                            document.getString("phone"),
                            document.getString("email"),
                            null,
                            Boolean.TRUE.equals(document.getBoolean("notification")),
                            null
                    );
                    attendeesList.add(attendee);
                }
            }
            return attendeesList;
        });
    }




}