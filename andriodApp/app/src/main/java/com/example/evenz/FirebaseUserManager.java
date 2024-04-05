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
        userMap.put("checkedInEvent", user.getCheckedInEvent());
        userMap.put("notificationEnabled", user.getNotificationsEnabled());
        userMap.put("locationEnabled", user.getLocationEnabled());


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

    // Add a checkin event to a user
    public Task<Void> checkInUser(String userId, String eventId) {
        return db.collection("users").document(userId).update("checkedInEvent", eventId);
    }

    // Create a method that will return the eventID of the checked-in event for a given user
    public Task<String> getCheckedInEventForUser(String userId) {
        return db.collection("users").document(userId).get().continueWith(task -> {
            if (task.isSuccessful()) {
                return task.getResult().getString("checkedInEvent");
            }
            return null;
        });
    }

    // Create a method that will return all of the attendes for a given event it uses the getUser method to get the user object
    public Task<List<User>> getAttendeesForEvent(String eventId) {
        return db.collection("users").whereArrayContains("eventsSignedUpFor", eventId).get().continueWithTask(task -> {
            if (task.isSuccessful()) {
                List<Task<User>> tasks = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String userId = document.getString("userId");
                    tasks.add(getUser(userId));
                }
                return Tasks.whenAllSuccess(tasks);
            } else {
                return Tasks.forException(task.getException());
            }
        });
    }

    // Create user method that will return a user object for a given userId
    public Task<User> getUser(String userId) {
        return db.collection("users").document(userId).get().continueWith(task -> {
            if (task.isSuccessful()) {
                QueryDocumentSnapshot document = (QueryDocumentSnapshot) task.getResult();
                User user = new User(
                        document.getString("userId"),
                        document.getString("name"),
                        document.getString("phone"),
                        document.getString("email"),
                        document.getString("profilePicID"),
                        document.getString("userType"),
                        document.getBoolean("notificationEnabled"),
                        document.getBoolean("locationEnabled")
                );
                // Initialize the eventsSignedUpFor ArrayList if it exists in the document
                List<String> eventsSignedUpFor = (List<String>) document.get("eventsSignedUpFor");
                if (eventsSignedUpFor != null) {
                    user.setEventsSignedUpFor(new ArrayList<>(eventsSignedUpFor));
                }
                return user;
            }
            return null;
        });
    }


    // Additional methods for user management can be added here...
}
