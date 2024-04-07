package com.example.evenz;
import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.util.Log;
import androidx.media3.common.util.UnstableApi;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.text.ParseException;
import java.util.UUID;

public class EventCreationActivity extends AppCompatActivity  implements DatePickerDialog.OnDateSetListener {

    private static final int PICK_IMAGE_REQUEST = 22;
    private Uri filePath;

    StorageReference photoRef;

    private ImageView imageView, datePickerButton;

    private EditText editTextOrganizerName,editTextEventName, editDate, editTextAttendeeLimit, editTextEventInfo, editTextEventLoc;
    private RelativeLayout submitEventButton;

    private String eventPosterID_temp;
    private String eventID;
    // ImageUtility instance
    private ImageUtility imageUtility;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_event);

        // Initialize Firebase Storage reference
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();

        // Initialize ImageUtility
        imageUtility = new ImageUtility();

        // Get extras from intent
        Bundle b = getIntent().getExtras();
        assert b != null;
        String role = b.getString("role");

        eventPosterID_temp = UUID.randomUUID().toString();
        storageReference = FirebaseStorage.getInstance().getReference();

        photoRef = storageReference.child("images/" + eventPosterID_temp);


        // Initialize UI components
        initUI();

        datePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerFragment mDatePickerDialogFragment = new DatePickerFragment();
                mDatePickerDialogFragment.show(getSupportFragmentManager(), "DATE PICK");
            }
        });

        imageView = findViewById(R.id.vector_ek2);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                select();
            }
        });

        // Submit event button listener for initiating the upload process
        submitEventButton.setOnClickListener(view -> {
            if (filePath != null) {
                ImageUtility.UploadCallback callback = new ImageUtility.UploadCallback() {
                    @Override
                    public void onSuccess(String imageID, String imageURL) {
                        // Image successfully uploaded
                        // Proceed with event submission or other actions as needed
                        eventPosterID_temp = imageID; // If needed for further operations
                        submitEvent();
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
        editDate = findViewById(R.id.editDate); // Date picker
        datePickerButton = findViewById(R.id.event_Date_Picker);
        editTextAttendeeLimit = findViewById(R.id.no_limit);
        editTextEventInfo = findViewById(R.id.editTextEventInfo);
        editTextEventLoc = findViewById(R.id.editTextLocation);
        submitEventButton = findViewById(R.id.create_event_button); //Create event button
    }


    private void navigateToHomeScreen() {
        Intent intent = new Intent(EventCreationActivity.this, HomeScreenActivity.class);
        Bundle b = new Bundle();
        b.putString("role", "organizer");
        b.putString("eventID", eventID);
        intent.putExtras(b);
        startActivity(intent);
    }


    private void submitEvent() {
        // Collect input data
        String eventName = editTextEventName.getText().toString().trim();
        String description = editTextEventInfo.getText().toString().trim();
        String eventPosterID = eventPosterID_temp; // Use the uploaded image ID
        String attendeeLimit = editTextAttendeeLimit.getText().toString().trim();
        String orgName = editTextOrganizerName.getText().toString().trim();
        String eventDatestring = editDate.getText().toString().trim();
        String location = editTextEventLoc.getText().toString().trim();

        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.FULL, Locale.getDefault());
        Date eventDate;
        try {
            eventDate = dateFormat.parse(eventDatestring);
        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(this, "Invalid date format", Toast.LENGTH_LONG).show();
            return;
        }
        // Generate a unique ID for the event
        String eventID = FirebaseFirestore.getInstance().collection("events").document().getId();

        // Prepare the event data map
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("eventName", eventName);
        eventData.put("description", description);
        eventData.put("eventPosterID", eventPosterID);
        eventData.put("eventAttendLimit", Integer.parseInt(attendeeLimit));
        eventData.put("organizationName", orgName);
        eventData.put("eventDate", eventDate);
        eventData.put("location", location);
        eventData.put("eventID", eventID);


        EventUtility.storeEventwnm(eventData, eventID);

        // Update the user's document with the new event ID (if necessary)
        createUserDocumentWithEvent(eventID);

        // Navigate to the home screen after the event has been successfully stored
        navigateToHomeScreen(eventID);
    }

    @OptIn(markerClass = UnstableApi.class)
    private void createUserDocumentWithEvent(String eventID) {
        String deviceID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        // Prepare the user data
        Map<String, Object> userData = new HashMap<>();
        userData.put("userId", deviceID); //deviceID is the UserID
        userData.put("eventList", eventID);
        userData.put("userType", "organizer"); //TODO: add other ORG fields

        FirebaseFirestore.getInstance().collection("users").document(deviceID)
                .set(userData)
                .addOnSuccessListener(aVoid -> Log.d("createUser", "User document created with new event ID"))
                .addOnFailureListener(e -> Log.w("createUser", "Error creating user document", e));
    }

    private void navigateToHomeScreen(String eventID) {
        Intent intent = new Intent(EventCreationActivity.this, HomeScreenActivity.class);
        intent.putExtra("role", "organizer");
        intent.putExtra("eventID", eventID);
        startActivity(intent);
    }


}