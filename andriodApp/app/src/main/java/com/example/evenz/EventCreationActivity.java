package com.example.evenz;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.util.Log;
import androidx.media3.common.util.UnstableApi;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.util.Date;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;

import java.text.ParseException;
import java.text.SimpleDateFormat;


public class EventCreationActivity extends AppCompatActivity {

    private EditText editTextOrganizerName, editTextEventName, editDate, editTextAttendeeLimit, editTextEventInfo;
    private Button submitEventButton;

    // Firestore instance
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference ref = db.collection("events");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_event);

        // Initialize UI components
        initUI();

        submitEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    submitEvent();
                    startActivity(new Intent(EventCreationActivity.this, MainActivity.class));
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        findViewById(R.id.back_less).setOnClickListener(v -> finish());
    }

    private void initUI() {
        editTextOrganizerName = findViewById(R.id.editTextOrganizerName);
        editTextEventName = findViewById(R.id.editTextEventName);
        editDate = findViewById(R.id.editDate); // Ensure you have input formatting or parsing for date
        editTextAttendeeLimit = findViewById(R.id.no_limit);
        editTextEventInfo = findViewById(R.id.editTextEventInfo);
        submitEventButton = findViewById(R.id.create_event_button); //Create event button
    }

    private void submitEvent() throws ParseException {

        String eventName = editTextEventName.getText().toString().trim();
        String eventPosterID = "defaultPosterID"; // Assuming a default or gathered elsewhere
        String description = editTextEventInfo.getText().toString().trim();
        String attendeeLimit = editTextAttendeeLimit.getText().toString().trim();
        String orgName = editTextOrganizerName.getText().toString().trim();
        String eventDatestring = editDate.getText().toString().trim();

        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault());
        Date eventDate = null;

        eventDate = dateFormat.parse(eventDatestring);

        // handle attendee limit error:
        int eventAttendeeLimit = 0; // Default to 0 or some other appropriate default value

        eventAttendeeLimit = Integer.parseInt(attendeeLimit);
        Geolocation geolocation = new Geolocation("asd", 0.0f, 0.0f); //TODO: replace with actual values
        Bitmap qrCodeBrowse = Bitmap.createBitmap(4, 4, Bitmap.Config.ARGB_8888); // TODO: get random generated QR code
        Bitmap qrCodeCheckIn = Bitmap.createBitmap(4, 4, Bitmap.Config.ARGB_8888); // TODO: get random generated QR code for events
        Map<String, Long> userList = new Hashtable<>(); // TODO: append attendee who check in the event

        // Constructing the Event object
        Event newEvent = new Event(orgName, eventName, eventPosterID, description, geolocation, qrCodeBrowse, qrCodeCheckIn, eventAttendeeLimit, userList, eventDate);

        // Now, convert  Event object to a Map or directly use the attributes to add to Firestore
        Map<String, Object> eventMap = new HashMap<>();
        eventMap.put("organizationName", newEvent.getOrganizationName());
        eventMap.put("eventName", newEvent.getEventName());
        eventMap.put("description", newEvent.getDescription());
        eventMap.put("AttendLimit", newEvent.getEventAttendLimit());
        eventMap.put("eventDate", newEvent.getEventDate());

        // added add() so, event ID will be automatically generated.
        // TODO: review with TEAM
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("events").add(eventMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @OptIn(markerClass = UnstableApi.class) @Override
                    public void onSuccess(DocumentReference documentReference) {
                        // Successfully added event with auto-generated ID
                        Log.d("Firestore", "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @OptIn(markerClass = UnstableApi.class) @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle the error
                        Log.w("Firestore", "Error adding document", e);
                    }
                });
    }
}

