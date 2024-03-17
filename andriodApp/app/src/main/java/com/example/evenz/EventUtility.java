package com.example.evenz;

import android.app.Activity;
import android.graphics.Bitmap;
import android.nfc.Tag;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

public final class EventUtility {


    private EventUtility(){

    }


    /**
     * Function takes a Event object and parses the fields into a Hashmap
     * in preparation for insertion of it into the
     *  NOTE: if additional fields need to be added, remember to modify this function
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
     *stores Hashmap as an Event into Firestore, same as other one,
     * but can specify the event-document ID.
     * @param evmap   hashmap of event to be stored into firestore
     * @param evid   the document ID
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


   /*
    public static void evtomap(String orgnm,
                               String evnm,
                               String evdesc,
                               Long maxat,
                               Date evdt,
                               String evloc,
                               ) {

    }

     */


}