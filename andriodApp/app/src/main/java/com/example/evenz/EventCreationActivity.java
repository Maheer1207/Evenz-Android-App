package com.example.evenz;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.util.Log;
import androidx.media3.common.util.UnstableApi;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import java.text.ParseException;
import java.util.UUID;
import java.util.zip.CRC32C;

@UnstableApi public class EventCreationActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    // Define constants at the top of your class
    private static final int PICK_IMAGE_REQUEST = 22;
    private static final String DEFAULT_ROLE = "organizer";

    // Declare variables
    private Uri filePath;
    private StorageReference storageReference;
    private StorageReference photoRef;
    private ImageView imageView, datePickerButton;
    private EditText editTextOrganizerName, editTextEventName, editDate, editTextAttendeeLimit, editTextEventInfo, editTextEventLoc;
    private RelativeLayout submitEventButton;
    private String eventPosterID_temp;
    private String eventID;
    private ImageUtility imageUtility;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_event);

        // Initialize Firebase Storage reference only once
        storageReference = FirebaseStorage.getInstance().getReference();

        // Initialize ImageUtility
        imageUtility = new ImageUtility();

        // Get extras from intent
        Bundle b = getIntent().getExtras();
        assert b != null;

        eventPosterID_temp = UUID.randomUUID().toString();
        photoRef = storageReference.child("images/" + eventPosterID_temp);

        // Initialize UI components
        initUI();

        datePickerButton.setOnClickListener(v -> {
            DatePickerFragment mDatePickerDialogFragment = new DatePickerFragment();
            mDatePickerDialogFragment.show(getSupportFragmentManager(), "DATE PICK");
        });

        imageView.setOnClickListener(v -> select());

        // Submit event button listener for initiating the upload process
        submitEventButton.setOnClickListener(view -> {
            if (filePath != null) {
                ImageUtility.UploadCallback callback = new ImageUtility.UploadCallback() {
                    @Override
                    public void onSuccess(String imageID, String imageURL) {
                        // Image successfully uploaded
                        // Proceed with event submission or other actions as needed
                        eventPosterID_temp = imageID; // If needed for further operations
                        try {
                            submitEvent();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        submitOrganizer();
                        navigateToHomeScreen();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        // Handle upload failure
                        Toast.makeText(EventCreationActivity.this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                };

                imageUtility.upload(filePath, callback);
            } else {
                Toast.makeText(EventCreationActivity.this, "Please select an image first", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Method to initiate the image selection
    private void select() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image from here..."), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar mCalendar = Calendar.getInstance();
        mCalendar.set(Calendar.YEAR, year);
        mCalendar.set(Calendar.MONTH, month);
        mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        String selectedDate = DateFormat.getDateInstance(DateFormat.FULL).format(mCalendar.getTime());
        editDate.setText(selectedDate);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void initUI() {
        editTextOrganizerName = findViewById(R.id.editTextOrganizerName);
        editTextEventName = findViewById(R.id.editTextEventName);
        editDate = findViewById(R.id.editDate); // Ensure you have input formatting or parsing for date
        datePickerButton = findViewById(R.id.event_Date_Picker);
        imageView = findViewById(R.id.vector_ek2); // Replace 'your_image_view_id' with the actual ID of your ImageView
        editTextAttendeeLimit = findViewById(R.id.no_limit);
        editTextEventInfo = findViewById(R.id.editTextEventInfo);
        editTextEventLoc = findViewById(R.id.editTextLocation);
        submitEventButton = findViewById(R.id.create_event_button); //Create event button
    }

    private void navigateToHomeScreen() {
        Intent intent = new Intent(EventCreationActivity.this, HomeScreenActivity.class);
        Bundle b = new Bundle();
        b.putString("role", DEFAULT_ROLE);
        b.putString("eventID", eventID);
        intent.putExtras(b);
        startActivity(intent);
    }

    private void submitEvent() throws ParseException {
        String eventName = editTextEventName.getText().toString().trim();
        String eventPosterID = eventPosterID_temp; // Assuming a default or gathered elsewhere
        String description = editTextEventInfo.getText().toString().trim();
        String attendeeLimit = editTextAttendeeLimit.getText().toString().trim();
        String orgName = editTextOrganizerName.getText().toString().trim();
        String eventDatestring = editDate.getText().toString().trim();
        String location = editTextEventLoc.getText().toString().trim();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference newEventRef = db.collection("events").document();

        eventID = newEventRef.getId(); // Use this eventID for your operations

        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.FULL, Locale.getDefault());
        Date eventDate = dateFormat.parse(eventDatestring);

        int eventAttendeeLimit = Integer.parseInt(attendeeLimit);
        Geolocation geolocation = new Geolocation("asd", 0.0f, 0.0f); //TODO: replace with actual values
        Bitmap qrCodeBrowse = Bitmap.createBitmap(4, 4, Bitmap.Config.ARGB_8888); // TODO: get random generated QR code
        Bitmap qrCodeCheckIn = Bitmap.createBitmap(4, 4, Bitmap.Config.ARGB_8888); // TODO: get random generated QR code for events
        ArrayList<String> userList = new ArrayList<>(); // TODO: append attendee who check in the event
        ArrayList<String> notificationList = new ArrayList<>(); // TODO: append notification to the list

        Event newEvent = new Event(eventID, orgName, eventName, eventPosterID, description, geolocation, qrCodeBrowse, qrCodeCheckIn, eventAttendeeLimit, userList, eventDate, notificationList, location);

        Map<String, Object> eventMap1 = EventUtility.evtomMap(newEvent);

        EventUtility.storeEventwnm(eventMap1, eventID);
    }

    private void submitOrganizer() {
        @SuppressLint("HardwareIds") String deviceID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        String name = editTextOrganizerName.getText().toString().trim();
        String eventName = editTextEventName.getText().toString().trim();
        // Create a new user with the feilds and all other set to null
        User organizer = new User(deviceID, name, "", "", "", "Organizer", false, false);

        FirebaseUserManager firebaseUserManager = new FirebaseUserManager();

        firebaseUserManager.submitUser(organizer).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Add the event to the organizer's event list
                firebaseUserManager.addEventToUser(deviceID, eventName).addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        Log.d("EventCreationActivity", "Organizer added successfully");
                    } else {
                        Log.e("EventCreationActivity", "Failed to add event to organizer", task1.getException());

                    }
                });
            } else {
                Log.e("EventCreationActivity", "Failed to submit organizer", task.getException());
            }
        });
    }
}