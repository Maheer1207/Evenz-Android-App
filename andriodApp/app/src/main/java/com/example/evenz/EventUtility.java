package com.example.evenz;

import static androidx.core.content.ContextCompat.getSystemService;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.nfc.Tag;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

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
     * @param doc
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

        return new Event(doc.getString("organizationName"), doc.getString("eventName"), doc.getString("eventPosterID"),
                doc.getString("description"), geolocation, placeholderBitmap,
                placeholderBitmap, 0,
                new Hashtable<>(), doc.getDate("eventDate"), new ArrayList<String>(), doc.getString("location"));
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

}

