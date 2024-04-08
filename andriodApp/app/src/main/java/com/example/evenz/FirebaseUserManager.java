package com.example.evenz;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

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
        return ref.document(user.getUserId()).set(userMap, SetOptions.merge());
    }

    // submitOrganizer method that will sumbit an organizer to the database which is just a user with only a User ID, Name, UserType, and checkedInEvent


    // Create a method to update a user document in the database to add an event to the eventsSignedUpFor list
    public Task<Void> addEventToUser(String userId, String eventId) {
        return ref.document(userId).update("eventsSignedUpFor", FieldValue.arrayUnion(eventId));
    }

    // Remove user from event
    public Task<Void> removeEventFromUser(String userId, String eventId) {
        return ref.document(userId).update("eventsSignedUpFor", FieldValue.arrayRemove(eventId));
    }

    // Add a checkin event to a user
    public Task<Void> checkInUser(String userId, String eventId) {
        return ref.document(userId).update("checkedInEvent", eventId);
    }

    // Add a checkout event to a user
    public Task<Void> checkOutUser(String userId) {
        return ref.document(userId).update("checkedInEvent", null);
    }
    // Create a method that will return the eventID of the checked-in event for a given user
    public Task<String> getCheckedInEventForUser(String userId) {
        return ref.document(userId).get().continueWith(task -> {
            if (task.isSuccessful()) {
                return task.getResult().getString("checkedInEvent");
            }
            return null;
        });
    }

    // Create a method that will return all of the attendes for a given event it uses the getUser method to get the user object
    public Task<List<User>> getAttendeesForEvent(String eventId) {
        return ref.whereArrayContains("eventsSignedUpFor", eventId).get()
                .continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        // If the task failed, propagate the exception
                        return Tasks.forException(task.getException());
                    }
                    if (task.getResult() == null) {
                        // If the result is null, propagate an exception or handle accordingly
                        return Tasks.forException(new IllegalStateException("Result is null"));
                    }

                    // Call the helper method to process the documents
                    return Tasks.forResult(processDocuments(task.getResult()));
                });
    }

    private List<User> processDocuments(Iterable<QueryDocumentSnapshot> documents) {
        List<User> users = new ArrayList<>();
        for (QueryDocumentSnapshot document : documents) {
            User user = new User(
                    document.getId(),
                    document.getString("name"),
                    document.getString("phone"),
                    document.getString("email"),
                    document.getString("profilePicID"),
                    document.getString("userType"),
                    document.getBoolean("notificationEnabled"),
                    document.getBoolean("locationEnabled")
            );
            // Check if the userType is "attendee" before adding the user to the list
            if ("attendee".equals(user.getUserType())) {
                users.add(user);
                // Log the name of the user
                System.out.println(user.getName());
            }
        }
        return users;
    }

    public Task<List<String>> getEventsSignedUpForUser(String userId) {
        return db.collection("users").document(userId).get().continueWith(task -> {
            List<String> eventsSignedUpFor = new ArrayList<>();
            if (task.isSuccessful() && task.getResult() != null) {
                DocumentSnapshot document = task.getResult();
                if (document.exists() && document.getData() != null) {
                    // Attempt to get the eventsSignedUpFor field as a List
                    Object eventsObject = document.get("eventsSignedUpFor");
                    if (eventsObject instanceof List<?>) {
                        List<?> eventsRawList = (List<?>) eventsObject;
                        for (Object item : eventsRawList) {
                            if (item instanceof String) {
                                eventsSignedUpFor.add((String) item);
                            } else {
                                Log.e("Firestore", "Invalid item type in eventsSignedUpFor array");
                            }
                        }
                    } else {
                        Log.e("Firestore", "'eventsSignedUpFor' field is not a List");
                    }
                } else {
                    Log.e("Firestore", "Document does not exist");
                }
            } else {
                Log.e("Firestore", "Failed to fetch user document", task.getException());
            }
            return eventsSignedUpFor;
        });
    }

    // Create user method that will return a user object for a given userId
    public Task<User> getUser(String userId) {
        return ref.document(userId).get().continueWith(task -> {
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

    /**
     * A firebase task that given the device id of the user returns the eventID
     * they are currently attending or hosting
     * @param deviceID The id of the device (userID)
     * @return the eventID they are currently attending or hosting
     */
    public Task<String> getEventID(String deviceID) {

        return FirebaseFirestore.getInstance()
                .collection("users")
                .get()
                .continueWith(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            if (document.getId().equals(deviceID)) {
                                List<String> eventsSignedUpFor = (List<String>) document.get("eventsSignedUpFor");
                                return eventsSignedUpFor.get(0);
                            }
                        }
                        return "N";
                    } else {
                        throw task.getException();
                    }
                });
    }

    public Task<String> getEventName(String userId) {
        return ref.document(userId).get().continueWith(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                DocumentSnapshot document = task.getResult();
                List<String> eventsSignedUpFor = (List<String>) document.get("eventsSignedUpFor");
                if (eventsSignedUpFor != null && !eventsSignedUpFor.isEmpty()) {
                    return eventsSignedUpFor.get(0);
                } else {
                    // Handle the case where eventsSignedUpFor is null or empty.
                    throw new NoSuchElementException("No events signed up for.");
                }
            } else {
                // Handle the unsuccessful task, e.g., by throwing an exception.
                throw new Exception("Failed to fetch document: " + task.getException());
            }
        });
    }


    /**
     * A firebase task that given the device id of the user returns their userType
     * @param deviceID The id of the device (userID)
     * @return the devices usertype being Attendee, Organizer, or Admin
     */
    public Task<String> getUserType(String deviceID) {
        final List<String> userID = new ArrayList<>();

        return FirebaseFirestore.getInstance()
                .collection("users")
                .get()
                .continueWith(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            if (document.contains("userType")) {
                                if (document.getId().equals(deviceID)) {
                                    userID.add(document.getString("userType"));
                                    return userID.get(0);
                                }
                            }
                        }
                        userID.add("N");
                        return userID.get(0);
                    } else {
                        throw task.getException();
                    }
                });
    }

    /**
     * Gets if a given user is already in the database
     * @param deviceID the id of the user
     * @return true or false if the user exists
     */
    public Task<Boolean> getUserExist(String deviceID) {
        return FirebaseFirestore.getInstance()
                .collection("users")
                .get()
                .continueWith(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            if (document.getId().equals(deviceID)) {
                                return true;
                            }
                        }
                        throw task.getException();
                    } else {
                        throw task.getException();
                    }
                });
    }

    /**
     * Sets a selected event an organizer has organized
     * and assigns it to the first position of the events
     * they have organized, making that event default
     * @param userId ID of the organizer
     * @param eventId ID of the event to be set to the top
     * @return no value
     */
    public Task<Void> setTopOrgEvent(String userId, String eventId) {
        return FirebaseFirestore.getInstance()
                .collection("users")
                .get()
                .continueWith(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            if (document.getId().equals(userId)) {
                                List<String> events = (List<String>)document.get("eventsSignedUpFor");
                                events.remove(eventId);
                                for (String event : events) {
                                    ref.document(userId).update("eventsSignedUpFor", FieldValue.arrayRemove(event));
                                    ref.document(userId).update("eventsSignedUpFor", FieldValue.arrayUnion(event));
                                }
                            }
                        }
                    } else {
                        throw task.getException();
                    }
                    return null;
                });
    }

    // Additional methods for user management can be added here...
}
