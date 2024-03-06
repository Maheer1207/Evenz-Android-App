package com.example.evenz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

    // Assuming Geolocation is a custom class you've defined,
    // for simplicity, I'm skipping its initialization.
    // You'll need to implement how you gather this data, e.g., from a map or user input.

    // Firestore instance
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_event);

        // Initialize UI components
        initUI();

        submitEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitEvent();
            }
        });
    }

    private void initUI() {
        editTextOrganizerName = findViewById(R.id.editTextOrganizerName);
        editTextEventName = findViewById(R.id.editTextEventName);
        editDate = findViewById(R.id.editDate); // Ensure you have input formatting or parsing for date
        editTextAttendeeLimit = findViewById(R.id.no_limit);
        editTextEventInfo = findViewById(R.id.editTextEventInfo);
        submitEventButton = findViewById(R.id.create_event_button); // Ensure this ID matches your button in XML
    }

    private void submitEvent() {
        String eventName = editTextEventName.getText().toString().trim();
        String eventPosterID = "defaultPosterID"; // Assuming a default or gathered elsewhere
        String description = editTextEventInfo.getText().toString().trim();
        String attendeeLimit = editTextAttendeeLimit.getText().toString().trim();
        String orgName = editTextOrganizerName.getText().toString().trim();
        String eventDatestring = editDate.getText().toString().trim();


        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault());
        Date eventDate = null;

        try {
            eventDate = dateFormat.parse(eventDatestring);
        } catch (ParseException e) {
            // Handle the case where the string is not in the expected format
            Toast.makeText(getApplicationContext(), "Invalid date format", Toast.LENGTH_SHORT).show();


            // handle attendee limit error:
            int eventAttendeeLimit = 0; // Default to 0 or some other appropriate default value

            try {
                eventAttendeeLimit = Integer.parseInt(attendeeLimit);
            } catch (NumberFormatException x) {
                // Handle the case where the string is not a valid integer
                Toast.makeText(getApplicationContext(), "Invalid number format for attendee limit", Toast.LENGTH_SHORT).show();
            }
            Geolocation geolocation = new Geolocation("asd", 0.0f, 0.0f); //TODO: replace with actual values
            Bitmap qrCodeBrowse = Bitmap.createBitmap(4,4,Bitmap.Config.ARGB_8888); // TODO: get random generated QR code
            Bitmap qrCodeCheckIn = Bitmap.createBitmap(4,4,Bitmap.Config.ARGB_8888); // TODO: get random generated QR code for events
            Map<String, Long> userList = new Hashtable<>(); // TODO: append attendee who check in the event

            // Constructing the Event object
            Event newEvent = new Event(orgName, eventName, eventPosterID, description, geolocation, qrCodeBrowse, qrCodeCheckIn, eventAttendeeLimit, userList, eventDate);

            // Now, convert  Event object to a Map or directly use the attributes to add to Firestore
            Map<String, Object> eventMap = new HashMap<>();
            eventMap.put("organization Name", newEvent.getOrganizationName());
            eventMap.put("eventName", newEvent.getEventName());
            eventMap.put("description", newEvent.getDescription());
            eventMap.put("Attend Limit", newEvent.getEventAttendLimit());
            eventMap.put("Event Date", newEvent.getEventDate());


            // Add other fields as per your Firestore document structure

            db.collection("events").add(eventMap)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Toast.makeText(EventCreationActivity.this, "Event successfully added!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(EventCreationActivity.this, "Error adding event: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}
