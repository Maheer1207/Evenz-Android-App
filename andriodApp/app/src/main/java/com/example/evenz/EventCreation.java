package com.example.evenz;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.UUID;

public class EventCreation extends AppCompatActivity {

    private EditText editTextEventName, editTextEventDate, editTextEventDescription;
    private Button generateQRButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_event);

        // Initialize your EditTexts and Button
        editTextEventName = findViewById(R.id.editTextEventName);
        editTextEventDate = findViewById(R.id.editDate);
        editTextEventDescription = findViewById(R.id.editTextEventInfo);
        generateQRButton = findViewById(R.id.sign_up_button);

        findViewById(R.id.back_less).setOnClickListener(v -> finish());

        generateQRButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createEvent();
            }
        });
    }

    private void createEvent() {
        String eventName = editTextEventName.getText().toString();
        String eventDate = editTextEventDate.getText().toString(); // Assuming this is a simplified date string.
        String eventDescription = editTextEventDescription.getText().toString();

        // TODO: You would also collect other event details here as needed

        // For simplicity, assuming the other required details are hardcoded or fetched from elsewhere
        String eventPosterID = "samplePosterID"; // This should be replaced with actual ID from storage upload
        Geolocation geolocation = new Geolocation(0.0, 0.0); // Example geolocation, replace with actual data
        int qrCodeBrowse = 12345; // Example QR code, replace with actual generated code
        int qrCodeCheckIn = 67890; // Example QR code, replace with actual generated code
        Dictionary<String, Integer> userList = new Hashtable<>(); // Initially empty, users added upon RSVP/check-in

        Event event = new Event(eventName, eventPosterID, eventDescription, geolocation, qrCodeBrowse, qrCodeCheckIn, userList);
        addEventToFirestore(event);
    }

    private void addEventToFirestore(Event event) {
        // Convert event to a Map for Firestore, or use a custom object
        // Example code to add event to Firestore, assuming a static method in your MainActivity or a dedicated DataManager class
        MainActivity.addEvent(UUID.randomUUID().toString(), event);
    }
}
