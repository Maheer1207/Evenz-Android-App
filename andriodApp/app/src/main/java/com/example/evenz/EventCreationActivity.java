package com.example.evenz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

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
        editTextAttendeeLimit = findViewById(R.id.no_limit); // This was previously missing
        editTextEventInfo = findViewById(R.id.editTextEventInfo);
        submitEventButton = findViewById(R.id.create_event_button); // Ensure this ID matches your button in XML
    }

    private void submitEvent() {
        String eventName = editTextEventName.getText().toString().trim();
        String eventPosterID = "defaultPosterID"; // Assuming a default or gathered elsewhere
        String description = editTextEventInfo.getText().toString().trim();
        Geolocation geolocation = new Geolocation("asd", 0.0f, 0.0f); //TODO: replace with actual values
        int qrCodeBrowse = 0; // TODO: get random generated QR code
        int qrCodeCheckIn = 0; // TODO: get random generated QR code for events
        Dictionary<String, Integer> userList = new Hashtable<>(); // TODO: append attendee who check in the event

        // Constructing the Event object
        Event newEvent = new Event(eventName, eventPosterID, description, geolocation, qrCodeBrowse, qrCodeCheckIn, userList);

        // Now, convert your Event object to a Map or directly use the attributes to add to Firestore
        // For simplicity here, I'll just showcase a Map with minimal data
        Map<String, Object> eventMap = new HashMap<>();
        eventMap.put("eventName", newEvent.getEventName());
        eventMap.put("description", newEvent.getDescription());
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
