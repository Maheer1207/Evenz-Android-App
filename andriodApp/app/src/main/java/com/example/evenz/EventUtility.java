package com.example.evenz;


import android.graphics.Bitmap;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import com.google.firebase.firestore.SetOptions;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public final class EventUtility {


    private EventUtility() {

    }


    /**
     * Function takes a Event object and parses the fields into a Hashmap
     * in preparation for insertion of it into the
     * NOTE: if additional fields need to be added, remember to modify this function
     *
     * @param pevent Event object to be parsed.
     * @return Map<String, Object>, return
     */
    public static Map<String, Object> evtomMap(Event pevent) {
        Map<String, Object> eventMap = new HashMap<>();
        eventMap.put("eventID", pevent.getEventID());
        eventMap.put("organizationName", pevent.getOrganizationName());
        eventMap.put("eventName", pevent.getEventName());
        eventMap.put("description", pevent.getDescription());
        eventMap.put("AttendLimit", pevent.getEventAttendLimit());
        eventMap.put("eventDate", pevent.getEventDate());
        eventMap.put("location", pevent.getLocation());
        eventMap.put("eventPosterID", pevent.getEventPosterID());
        eventMap.put("notifications", pevent.getNotifications());

        return eventMap;

    }
    /**
     * A firebase task that given the device id of the user returns their userType
     * @param deviceID The id of the device (userID)
     * @return the devices usertype being Attendee, Organizer, or Admin
     */


    //notice, it seems we have not established a system of event IDs, simply adding them.
    //and fetching the events just grabs the entire strcut

    //so I will overload methods, and make 2, one for specified EIDs, and one without.
    //

    /**
     * takes as hashmap of an event and stores into the database,
     * with no specific doc ID, it will be assigned a random one, alongside
     * with success and failure checks printed to Log.d(StoreEvent result
     *
     * @param evmap hashmap for event object to be stored
     */
    public static void storeEventnnm(Map<String, Object> evmap) {

        FirebaseFirestore db1 = FirebaseFirestore.getInstance();

        DocumentReference dr = db1.collection("events").document();
        dr.set(evmap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("storeEvent result", "Successfully added");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("storeEvent result", "Failed to add object");
            }
        });
    }

    /**
     * stores Hashmap as an Event into Firestore, same as other one,
     * but can specify the event-document ID.
     *
     * @param evmap hashmap of event to be stored into firestore
     * @param evid  the document ID
     */
    public static void storeEventwnm(Map<String, Object> evmap, String evid) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference dr = db.collection("events").document(evid);
        dr.set(evmap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("storeEvent result", "Successfully added");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("storeEvent result", "Failed to add object");
            }
        });
    }

    /**
     * Fetches every event currently in Database as an array of Event Objects.
     *
     * @return Arraylist of Events
     */
    public static ArrayList<Event> fetchallEvent() {
        ArrayList<Event> eventDataList = new ArrayList<>();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("events").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshots, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e("Firestore", error.toString());
                    return;
                }

                if (querySnapshots != null) {

                    for (QueryDocumentSnapshot doc : querySnapshots) {
                        String eventID = doc.getId();
                        // Parse the event data and add it to your list, then notify the adapter
                        Event tempEvent = parseEvent(doc);
                        eventDataList.add(tempEvent);
                    }
                }
            }
        });
        return eventDataList;

    }


    /**
     * Translates firebase doc into an Event.
     *
     * @return firebase doc translated into Event.
     */
    public static Event parseEvent(QueryDocumentSnapshot doc) {
        // Example of reconstructing a Geolocation object
        // Assuming you store geolocation as a map with latitude and longitude
        float xcoord = doc.contains("xcoord") ? ((Number) doc.get("xcoord")).floatValue() : 0; //TODO: add geolocation stuff, figure it out
        float ycoord = doc.contains("ycoord") ? ((Number) doc.get("ycoord")).floatValue() : 0;
        String geolocationID = doc.getString("geolocationID"); // Assuming there's an ID field; adjust if necessary

        Geolocation geolocation = new Geolocation(geolocationID, xcoord, ycoord);

        // Using placeholders for Bitmaps as you'll load them asynchronously in the adapter/view
        Bitmap placeholderBitmap = null; // TODO: add actual QR for attendee checkIN and Browse

        return new Event(doc.getString("eventID"), doc.getString("organizationName"), doc.getString("eventName"), doc.getString("eventPosterID"),
                doc.getString("description"), geolocation, placeholderBitmap,
                placeholderBitmap, 0,
                new ArrayList<String>(), doc.getDate("eventDate"), new ArrayList<String>(), doc.getString("location"));
    }

    //Fetch specific events from the database, by Hrithick
    public static void fetchEventsByIds(FirebaseFirestore db, List<String> eventIds, final ArrayList<Event> eventDataList, final EventAdapter eventAdapter) {
        if (eventIds == null || eventIds.isEmpty()) {
            Log.d("fetchEventsByIds", "No event IDs provided");
            return;
        }

        eventDataList.clear(); // Clear the list to prepare for new data

        // A counter to keep track of completed fetch operations
        AtomicInteger pendingFetches = new AtomicInteger(eventIds.size());

        for (String eventId : eventIds) {
            db.collection("events").document(eventId).get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    // Directly use documentSnapshot without casting
                    Event tempEvent = parseEventTemp(documentSnapshot); // Make sure parseEvent can handle DocumentSnapshot
                    if (tempEvent != null) {
                        eventDataList.add(tempEvent);
                    }
                }
                // Check if all fetches are done
                if (pendingFetches.decrementAndGet() == 0) {
                    // All fetches complete, update UI here
                    eventAdapter.notifyDataSetChanged();
                }
            }).addOnFailureListener(e -> {
                Log.e("fetchEventsByIds", "Error getting event " + eventId, e);
                // Check if all fetches are done
                if (pendingFetches.decrementAndGet() == 0) {
                    // All fetches complete, update UI here
                    eventAdapter.notifyDataSetChanged();
                }
            });
        }
    }

    public static Event parseEventTemp(DocumentSnapshot doc) {
        if (!doc.exists()) {
            return null; // or handle this case as needed
        }

        // Example of reconstructing a Geolocation object
        // Assuming you store geolocation as a map with latitude and longitude
        float xcoord = doc.contains("xcoord") ? ((Number) doc.get("xcoord")).floatValue() : 0;
        float ycoord = doc.contains("ycoord") ? ((Number) doc.get("ycoord")).floatValue() : 0;
        String geolocationID = doc.getString("geolocationID"); // Adjust if necessary

        Geolocation geolocation = new Geolocation(geolocationID, xcoord, ycoord);

        // Using placeholders for Bitmaps as you'll load them asynchronously in the adapter/view
        Bitmap placeholderBitmap = null; // Add actual logic to load images as needed

        return new Event(doc.getId(), doc.getString("organizationName"), doc.getString("eventName"), doc.getString("eventPosterID"),
                doc.getString("description"), geolocation, placeholderBitmap,
                placeholderBitmap, 0,
                new ArrayList<>(), doc.getDate("eventDate"), new ArrayList<>(), doc.getString("location"));
    }





    /**
     * Function adds or removes a specified notification from the Notification array in an event.
     * can be extended to generally operate on Array-firebase field
     * @param type  notification type, string   *used to specify or search notification
     * @param details  notification details, string   *used specify to search notification
     * @param edid  the document ID of the vent
     * @param flag  1=add,  0=remove.
     */
    public static void notificationOps(String type, String details, String edid, int flag) {
        FirebaseFirestore db1 = FirebaseFirestore.getInstance();

        //if I understand the code over there correctly, all it is is updating
        //the array of notifications with the event class
        //but you download the document, edit the array, then re-upload
        // but if I am correct, there are ways to directly update an array-type field.
        //https://firebase.google.com/docs/firestore/manage-data/add-data#update_elements_in_an_array
        String t = type + ", "+ details;
        DocumentReference dr = db1.collection("events").document(edid);
        Log.d("bentag", edid);
        Log.d("bentag1", type);
        Log.d("bentag2", details);
        if (flag == 1) {
            dr.update("notifications", FieldValue.arrayUnion(t)).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Log.d("notificationOps","Successfull notification Operation: added");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("notificationOps","unsccessful add operation");

                }
            });
        }
        else if (flag == 0) {
            dr.update("notifications", FieldValue.arrayRemove(t)).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Log.d("notificationOps","Successfull notification Operation: remove");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("notificationOps","Unsuccesful remove oepration");

                }
            });
        }
        else {
            Log.e("errorin notification update", "please enter 0=add or 1=remove");
        }

    }
    public static void sendPushNotificationToEventAttendees(String eventId, String notificationTitle, String notificationBody) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Assume "users" is the collection where user details are stored
        db.collection("users")
                .whereEqualTo("eventID", eventId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<String> deviceTokens = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Assume each user document has a "deviceToken" field
                            String deviceToken = document.getString("deviceToken");
                            if (deviceToken != null && !deviceToken.isEmpty()) {
                                deviceTokens.add(deviceToken);
                            }
                        }

                        // Now you have a list of device tokens, send the notification
                        if (!deviceTokens.isEmpty()) {
                            sendNotificationToDeviceTokens(deviceTokens, notificationTitle, notificationBody);
                        }
                    } else {
                        Log.d("EventUtility", "Error getting documents: ", task.getException());
                    }
                });
    }
    //used for adding an Ever to the event userlist
    public static void addUserToEvent(String userId, String eventId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference eventRef = db.collection("events").document(eventId);

        // Use FieldValue.arrayUnion() to add the userID to the UserList field
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("attending", 0); // 0 means signed up
        map.put("count", 0);
        eventRef.update("UserList", FieldValue.arrayUnion(map))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("EventUtility", "User added to event successfully");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("EventUtility", "Error: " + e.getMessage());
                    }
                });
    }

    //used for removing a user from the event userlist,By Hrithick
    public static void removeAttendeeFromEvent(String userId, String eventId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference ref = db.collection("events").document(eventId);
        ref.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        DocumentSnapshot doc = task.getResult();
                        List<Map<String, Object>> groups = (List<Map<String, Object>>) doc.get("UserList");
                        if (groups == null) {
                            groups = new ArrayList<>(); // Initialize if null
                        }
                        // Flag to check if the attendee was found and modified
                        boolean attendeeModified = false;
                        for (Map<String, Object> group : groups) {
                            String id = group.get("userId").toString();
                            if (id.equals(userId)) {
                                // Modify the attendee's data directly
                                group.put("attending", 0); // 0 means not attending
                                // Optionally, adjust 'count' or other fields as needed
                                attendeeModified = true;
                                break; // Break if the intended attendee is found and modified
                            }
                        }
                        if (attendeeModified) {
                            // Update the document with the modified list
                            ref.update("UserList", groups);
                        }
                    } else {
                        // Handle failure
                    }
                });
    }


    public static void userCheckIn(String userID, String eventID) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference ref = db.collection("events").document(eventID);
        ref.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        DocumentSnapshot doc = task.getResult();
                        Object rawGroups = doc.get("UserList");
                        List<Map<String, Object>> groups;
                        if (rawGroups != null) {
                            groups = (List<Map<String, Object>>) rawGroups;
                        } else {
                            groups = new ArrayList<>();
                        }
                        // Check if the user list is empty or the field doesn't exist
                        if (groups.isEmpty()) {
                            Map<String, Object> map = new HashMap<>();
                            map.put("userId", userID);
                            map.put("attending", 1); // 1 means checked in
                            map.put("count", 1);
                            // Use set with merge option to create the field if it doesn't exist
                            Map<String, Object> update = new HashMap<>();
                            update.put("UserList", Arrays.asList(map));
                            ref.set(update, SetOptions.merge());
                            return;
                        }
                        for (Map<String, Object> group : groups) {
                            String id = group.get("userId").toString();
                            Long attending = (Long) group.get("attending");
                            Long count = (Long) group.get("count");
                            if (id.equals(userID)) {
                                Map<String, Object> map = new HashMap<>();
                                Map<String, Object> newMap = new HashMap<>();
                                map.put("userId", id);
                                map.put("attending", attending);
                                map.put("count", count);
                                // Remove the old attendee information
                                ref.update("UserList", FieldValue.arrayRemove(map));
                                // Add the new attendee information
                                newMap.put("userId", id);
                                newMap.put("attending", 1); // 1 means checked in
                                newMap.put("count", count + 1);
                                ref.update("UserList", FieldValue.arrayUnion(newMap));
                                return;
                            }
                        }
                        // attendee not currently in event
                        Map<String, Object> map = new HashMap<>();
                        map.put("userId", userID);
                        map.put("attending", 1); // 1 means checked in
                        map.put("count", 1);
                        ref.update("UserList", FieldValue.arrayUnion(map));
                    } else {
                        // Handle failure
                        Log.e("userCheckIn", "Error getting document", task.getException());
                    }
                });
    }


    //TODO: unused for now
    private static void sendNotificationToDeviceTokens(List<String> deviceTokens, String title, String body) {
        try {
            OkHttpClient client = new OkHttpClient();
            MediaType mediaType = MediaType.parse("application/json");

            JsonObject payload = new JsonObject();

            // Notification content
            JsonObject notification = new JsonObject();
            notification.addProperty("title", title);
            notification.addProperty("body", body);
            payload.add("notification", notification);

            // Adding device tokens
            JsonArray tokens = new JsonArray();
            for (String token : deviceTokens) {
                tokens.add(token);
            }
            payload.add("registration_ids", tokens);

            RequestBody requestBody = RequestBody.create(mediaType, payload.toString());
            Request request = new Request.Builder()
                    .url("https://fcm.googleapis.com/fcm/send")
                    .post(requestBody)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Authorization", "key=YOUR_SERVER_KEY")
                    .build();

            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                Log.d("EventUtility", "Notification sent successfully: " + response.body().string());
            } else {
                Log.e("EventUtility", "Failed to send notification: " + response.body().string());
            }
        } catch (Exception e) {
            Log.e("EventUtility", "Error sending FCM notification", e);
        }
    }

    /**
     * A firebase task that given the id of an event of the user returns the attendee limit
     * @param eventID The id of the event
     * @return the attendlimit for the event id, returns -1 if no limit
     */
    public static Task<Boolean> eventFull(String eventID, String userID) {
        return FirebaseFirestore.getInstance()
                .collection("events")
                .get()
                .continueWith(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            if (document.contains("AttendLimit")) {
                                if (document.getId().equals(eventID)) {
                                    if (document.contains("UserList")) {
                                        Integer attendLimit = document.getLong("AttendLimit").intValue();
                                        List<Map<String, Object>> attendees = (List<Map<String, Object>>) document.get("UserList");
                                        int count = 0;
                                        for (Map<String, Object> attendee : attendees) {
                                            if ((Long)attendee.get("attending") != -1 && !attendee.get("userId").toString().equals(userID)) { count++;}
                                        }
                                        if (count >= attendLimit) {
                                            return Boolean.TRUE;
                                        }
                                        return Boolean.FALSE;
                                    }
                                    return Boolean.FALSE;
                                }
                            }
                        }
                        return Boolean.FALSE;
                    } else {
                        throw task.getException();
                    }
                });
    }

    public static Task<ArrayList<Integer>> userAttendCount(String eventID, String userID) {
        ArrayList<Integer> numbers = new ArrayList<>();
        return FirebaseFirestore.getInstance()
                .collection("events")
                .get()
                .continueWith(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            if (document.contains("UserList")) {
                                if (document.getId().equals(eventID)) {
                                    List<Map<String, Object>> attendees = (List<Map<String, Object>>) document.get("UserList");
                                    for (Map<String, Object> attendee : attendees) {
                                        if (attendee.get("userId").toString().equals(userID)) {
                                            numbers.add(((Long)attendee.get("count")).intValue());
                                            numbers.add(((Long)attendee.get("attending")).intValue());
                                            return numbers;
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        throw task.getException();
                    }
                    return null;
                });
    }

    public static void addLocationsToEvent(String eventId, double latitude, double longitude) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference eventRef = db.collection("events").document(eventId);

        // Convert the location to a string
        String locationString = latitude + "," + longitude;

        // Use FieldValue.arrayUnion() to add the location string to the checkInLocations field
        eventRef.update("checkInLocations", FieldValue.arrayUnion(locationString))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("EventUtility", "Location added to event successfully");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("EventUtility", "Error: " + e.getMessage());
                    }
                });
    }

    //give a specific event, return the locations
    public static Task<List<String>> getLocationsFromEvent(String eventId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference eventRef = db.collection("events").document(eventId);

        return eventRef.get().continueWith(new Continuation<DocumentSnapshot, List<String>>() {
            @Override
            public List<String> then(@NonNull Task<DocumentSnapshot> task) throws Exception {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        List<String> locationStrings = (List<String>) document.get("checkInLocations");
                        return locationStrings;
                    } else {
                        throw new Exception("No such document");
                    }
                } else {
                    throw task.getException();
                }
            }
        });
    }
    // Create a method that will return the attendlimit for the event from the event name
    public static Task<Integer> getAttendLimitFromEventName(String eventName) {
        final List<Integer> attendLimit = new ArrayList<>();

        return FirebaseFirestore.getInstance()
                .collection("events")
                .get()
                .continueWith(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            if (document.contains("eventAttendLimit")) {
                                if (document.getString("eventID").equals(eventName)) {
                                    attendLimit.add(document.getLong("eventAttendLimit").intValue());
                                    return attendLimit.get(0);
                                }
                            }
                        }
                        attendLimit.add(-1);
                        return attendLimit.get(0);
                    } else {
                        throw task.getException();
                    }
                });
    }

    /**
     * Given the event Id returns the location field
     * @param eventId id of event
     * @return returns location string
     */
    public static Task<String> getEventLocation(String eventId) {

        return FirebaseFirestore.getInstance()
                .collection("events")
                .get()
                .continueWith(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            if (document.getString("eventID").equals(eventId)) {
                                return document.getString("location");
                            }
                        }
                        return "";
                    } else {
                        throw task.getException();
                    }
                });
    }

}
