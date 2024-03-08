package com.example.evenz;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.util.Log;
import androidx.media3.common.util.UnstableApi;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
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
import java.util.UUID;

/**
 * The {@code EventCreationActivity} class represents an activity in the application for organizers
 * to create and submit new events. It extends the {@code AppCompatActivity} class and provides
 * functionality for capturing event details, including an event poster image, and submitting the
 * event to Firestore.
 *
 * <p>The activity includes the layout defined in the {@code create_event.xml} file. It utilizes
 * Firebase Storage for uploading event poster images and Firestore for storing event data. The
 * {@code EventCreationActivity} class is part of the organizer module and contributes to the
 * user interface and interaction for event creation in the organizer section of the application.
 *
 * <p>This class uses asynchronous image uploading with a progress dialog to keep the user informed
 * of the upload status. It also checks for existing users in Firestore and creates new users if
 * necessary, associating them with the organizer role.
 *
 * @author hrithick
 * @version 1.0
 * @see AppCompatActivity
 */
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
    /**
     * Called when the activity is first created. Responsible for initializing the activity, setting up
     * the layout, configuring UI components, and defining event handlers.
     *
     * @param savedInstanceState A Bundle containing the activity's previously saved state, if available.
     */
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
        // Set up the click listener for the "Create Event" button
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
        // Set up the click listener for the "Back" button
        findViewById(R.id.back_less).setOnClickListener(v->finish());
    }
    /**
     * Called when an activity launched by this one returns a result. In this case, it handles the result
     * of selecting an image using the image picker.
     *
     * @param requestCode The integer request code originally supplied to startActivityForResult(),
     *                    allowing you to identify who this result came from.
     * @param resultCode  The integer result code returned by the child activity through its setResult().
     * @param data        An Intent, which can return result data to the caller (various data can be attached to Intent).
     */
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

    /**
     * Initializes the UI components by finding and assigning references to the corresponding
     * views in the layout. This method is called during the activity's creation to set up the
     * EditText fields, ImageView, and the submitEventButton.
     */
    private void initUI() {
        editTextOrganizerName = findViewById(R.id.editTextOrganizerName);
        editTextEventName = findViewById(R.id.editTextEventName);
        editDate = findViewById(R.id.editDate); // Ensure you have input formatting or parsing for date
        editTextAttendeeLimit = findViewById(R.id.no_limit);
        editTextEventInfo = findViewById(R.id.editTextEventInfo);
        editTextEventLoc = findViewById(R.id.editTextLocation);
        submitEventButton = findViewById(R.id.create_event_button); //Create event button
    }
    /**
     * Submits the created event to Firestore after gathering information from the input fields
     * and user selections. This method constructs an Event object, checks user existence in Firestore,
     * and either creates a new user with UserType set to "organizer" or updates existing user data.
     * It then adds the event to the Firestore "events" collection and associates it with the user.
     *
     * <p>The method parses user input, such as the event name, description, attendee limit, organizer name,
     * event date, and location. It also retrieves the current device ID to identify the user. The event is
     * created with a default attendee limit, geolocation, and placeholder QR codes. The Event object is
     * then converted to a Map, and the map is added to the "events" collection in Firestore. The user's
     * "eventList" field is updated with the newly created event ID.
     *
     * <p>This method handles the process of creating or updating the user and adding the event to Firestore
     * in an asynchronous manner, ensuring a seamless user experience.
     *
     * @throws ParseException If there is an error parsing the event date from the user input.
     */
    private void submitEvent() throws ParseException {

        String eventName = editTextEventName.getText().toString().trim();
        String eventPosterID = eventPosterID_temp; // Assuming a default or gathered elsewhere
        String description = editTextEventInfo.getText().toString().trim();
        String attendeeLimit = editTextAttendeeLimit.getText().toString().trim();
        String orgName = editTextOrganizerName.getText().toString().trim();
        String eventDatestring = editDate.getText().toString().trim();
        String location = editTextEventLoc.getText().toString().trim();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        String deviceID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        // Reference to 'users' collection
        DocumentReference userDocRef = db.collection("users").document(deviceID);

        userDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            /**
             * Callback method triggered when the Firestore document representing the user's information
             * is successfully retrieved or not found. If the document exists, it indicates that the user
             * already exists in Firestore, and the event creation or user data update can proceed. If the
             * document does not exist, a new user is created with UserType set to "organizer," and the user
             * data is initialized as needed.
             *
             * @param task The task containing the result of the asynchronous operation. It holds the
             *             DocumentSnapshot, which can be queried for information about the document.
             */
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        // User exists, you can now proceed with event creation or update user data as needed
                    } else {
                        // User does not exist, create a new user with UserType set to "organizer"
                        Map<String, Object> newUser = new HashMap<>();
                        newUser.put("userType", "organizer");
                        // Add other user details as needed
                        newUser.put("userId", deviceID); // Assuming you want to use deviceID as userId

                        // Save the new user
                        db.collection("users").document(deviceID).set(newUser);
                    }
                }
            }
        });
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
        eventMap.put("location", newEvent.getLocation());
        eventMap.put("eventPosterID", newEvent.getEventPosterID());
        eventMap.put("notifications", newEvent.getNotifications());

        // added add() so, event ID will be automatically generated.
        // TODO: review with TEAM
        db.collection("events").add(eventMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    /**
                     * Asynchronously adds the event information to the Firestore "events" collection. Upon successful addition,
                     * the auto-generated document ID is logged, and the user's "eventList" field is updated with the new event ID.
                     * If the addition fails, the error is logged for further investigation.
                     *
                     * <p>This method uses Firestore's add method to insert the event data into the "events" collection. It then
                     * listens for the success or failure of this operation using the provided OnSuccessListener and OnFailureListener.
                     *
                     * @param eventMap         A Map containing the event information to be added to Firestore.
                     * @param userDocRef       The DocumentReference to the user's document in Firestore.
                     */
                    @OptIn(markerClass = UnstableApi.class)
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        // Successfully added event with auto-generated ID
                        Log.d("Firestore", "DocumentSnapshot added with ID: " + documentReference.getId());
                        userDocRef.update("eventList", documentReference.getId());
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
    /**
     * Called when the user clicks the "Select Image" ImageView. Initiates the process of
     * selecting an image from the device's gallery using an intent.
     */
        private void select()
        {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Image from here..."), PICK_IMAGE_REQUEST);
        }

    /**
     * Given that filePath has already been defined within the activity, uploads an image
     * previously selected by the user. This includes adding the image to Firebase Storage
     * with proper notifications for upload progress.
     *
     * @param photoRef The StorageReference for the location where the image will be stored.
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

