package com.example.evenz;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseUserManager {

    private final FirebaseFirestore db;
    private final CollectionReference ref;

    public FirebaseUserManager() {
        this.db = FirebaseFirestore.getInstance();
        this.ref = db.collection("users");
    }

    public FirebaseUserManager(FirebaseFirestore db) {
        this.db = db;
        this.ref = db.collection("users");
    }

    public Task<Void> submitUser(User user) {
        if (user == null) {
            return Tasks.forException(new IllegalArgumentException("User cannot be null"));
        }

        Map<String, Object> userMap = new HashMap<>();
        userMap.put("name", user.getName());
        userMap.put("profilePicID", user.getProfilePicID());
        userMap.put("phone", user.getPhone());
        userMap.put("email", user.getEmail());
        // No need to put "userId" in the map since it's used as document ID.
        userMap.put("userType", user.getUserType());
        userMap.put("eventsSignedUpFor", user.getEventsSignedUpFor());

        // Use userId as the document ID
        return db.collection("users").document(user.getUserId()).set(userMap, SetOptions.merge());
    }

    // Create a method to update a user document in the database to add an event to the eventsSignedUpFor list
    public Task<Void> addEventToUser(String userId, String eventId) {
        return db.collection("users").document(userId).update("eventsSignedUpFor", FieldValue.arrayUnion(eventId));
    }

    // Remove user from event
    public Task<Void> removeEventFromUser(String userId, String eventId) {
        return db.collection("users").document(userId).update("eventsSignedUpFor", FieldValue.arrayRemove(eventId));
    }

    // Create a method that will return all of the attendes for a given event
    public Task<List<User>> getAttendeesForEvent(String eventId) {
        final List<User> userList = new ArrayList<>();

        return db.collection("users").whereArrayContains("eventsSignedUpFor", eventId).get().continueWith(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    User user = new User(
                            document.getString("name"),
                            document.getString("profilePicID"),
                            document.getString("phone"),
                            document.getString("email"),
                            document.getString("userId"),
                            document.getString("userType")
                    );
                    // Initialize the eventsSignedUpFor ArrayList if it exists in the document
                    List<String> eventsSignedUpFor = (List<String>) document.get("eventsSignedUpFor");
                    if (eventsSignedUpFor != null) {
                        user.setEventsSignedUpFor(new ArrayList<>(eventsSignedUpFor));
                    }
                    userList.add(user);
                }
            }
            return userList;
        });
    }


    public Task<List<User>> getAllUsers() {
        final List<User> userList = new ArrayList<>();

        return db.collection("users").get().continueWith(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    User user = new User(
                            document.getString("name"),
                            document.getString("profilePicID"),
                            document.getString("phone"),
                            document.getString("email"),
                            document.getString("userId"),
                            document.getString("userType")
                    );
                    // Initialize the eventsSignedUpFor ArrayList if it exists in the document
                    List<String> eventsSignedUpFor = (List<String>) document.get("eventsSignedUpFor");
                    if (eventsSignedUpFor != null) {
                        user.setEventsSignedUpFor(new ArrayList<>(eventsSignedUpFor));
                    }
                    userList.add(user);
                }
            }
            return userList;
        });
    }

    // Additional methods for user management can be added here...
}
