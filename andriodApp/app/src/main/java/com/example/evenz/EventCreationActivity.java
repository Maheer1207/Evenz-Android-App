package com.example.evenz;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.util.Log;
import androidx.media3.common.util.UnstableApi;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.UUID;


public class EventCreationActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 22;
    private Uri filePath;
    StorageReference storageReference;
    StorageReference phtoRef;


    private ImageView imageView;


    private EditText editTextOrganizerName,editTextEventName, editDate, editTextAttendeeLimit, editTextEventInfo, editTextEventLoc;
    private Button submitEventButton;

    private String eventPosterID_temp;





    // Firestore instance
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference ref = db.collection("events");

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        eventPosterID_temp = UUID.randomUUID().toString();
        storageReference = FirebaseStorage.getInstance().getReference();

        phtoRef = storageReference.child("images/" + eventPosterID_temp);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_event);

        // Initialize UI components
        initUI();
        imageView = findViewById(R.id.vector_ek2);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                select();
            }
        });

        submitEventButton = findViewById(R.id.create_event_button);
        submitEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    upload(phtoRef);
                    submitEvent();
                    startActivity(new Intent(EventCreationActivity.this, MainActivity.class));
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        findViewById(R.id.back_less).setOnClickListener(v->finish());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // checking request code and result code
        // if request code is PICK_IMAGE_REQUEST and
        // resultCode is RESULT_OK
        // then set image in the image view
        if (requestCode == PICK_IMAGE_REQUEST
                && resultCode == RESULT_OK
                && data != null
                && data.getData() != null) {

                // Get the Uri of data
                filePath = data.getData();
                try {

                    // Setting image on image view using Bitmap
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                    imageView.setImageBitmap(bitmap);
                } catch (IOException e) {
                    // Log the exception
                    e.printStackTrace();
                }
            }
    }


    private void initUI() {
        editTextOrganizerName = findViewById(R.id.editTextOrganizerName);
        editTextEventName = findViewById(R.id.editTextEventName);
        editDate = findViewById(R.id.editDate); // Ensure you have input formatting or parsing for date
        editTextAttendeeLimit = findViewById(R.id.no_limit);
        editTextEventInfo = findViewById(R.id.editTextEventInfo);
        editTextEventLoc = findViewById(R.id.editTextLocation);
        submitEventButton = findViewById(R.id.create_event_button); //Create event button
    }

    private void submitEvent() throws ParseException {

        String eventName = editTextEventName.getText().toString().trim();
        String eventPosterID = eventPosterID_temp; // Assuming a default or gathered elsewhere
        String description = editTextEventInfo.getText().toString().trim();
        String attendeeLimit = editTextAttendeeLimit.getText().toString().trim();
        String orgName = editTextOrganizerName.getText().toString().trim();
        String eventDatestring = editDate.getText().toString().trim();
        String location = editTextEventLoc.getText().toString().trim();


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
        ArrayList<String> notificationList = new ArrayList<String>(); // TODO: append notification to the list

        // Constructing the Event object
        Event newEvent = new Event(orgName, eventName, eventPosterID, description, geolocation, qrCodeBrowse, qrCodeCheckIn, eventAttendeeLimit, userList, eventDate, notificationList, location);

        // Now, convert  Event object to a Map or directly use the attributes to add to Firestore
        Map<String, Object> eventMap = new HashMap<>();
        eventMap.put("organizationName", newEvent.getOrganizationName());
        eventMap.put("eventName", newEvent.getEventName());
        eventMap.put("description", newEvent.getDescription());
        eventMap.put("AttendLimit", newEvent.getEventAttendLimit());
        eventMap.put("eventDate", newEvent.getEventDate());
        eventMap.put("location", newEvent.getEventLoc());
        eventMap.put("eventPosterID", newEvent.getEventPosterID());
        eventMap.put("notifications", newEvent.getNotificationList());

        // added add() so, event ID will be automatically generated.
        // TODO: review with TEAM
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("events").add(eventMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @OptIn(markerClass = UnstableApi.class)
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        // Successfully added event with auto-generated ID
                        Log.d("Firestore", "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @OptIn(markerClass = UnstableApi.class)
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle the error
                        Log.w("Firestore", "Error adding document", e);
                    }
                });
    }

        private void select()
        {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Image from here..."), PICK_IMAGE_REQUEST);
        }

        /**
         * Given that filePath has already been defined within the activity, uploads an image previously selected by the user
         * This includes adding the image to the firebase storage with proper notifications for upload progress.
         */
        private void upload(StorageReference photoRef) {
            if (filePath != null) {
                ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setTitle("Uploading...");
                progressDialog.show();

                // Defining the child of storageReference
                StorageReference storageReference = FirebaseStorage.getInstance().getReference();


                // adding listeners on upload
                // or failure of image
                photoRef.putFile(filePath)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                // Image uploaded successfully
                                // Dismiss dialog
                                progressDialog.dismiss();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                // Error, Image not uploaded
                                progressDialog.dismiss();
                            }
                        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {

                            // Progress Listener for loading
                            // percentage on the dialog box
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                                progressDialog.setMessage("Uploaded " + (int) progress + "%");
                            }
                        });
            }
        }
}

