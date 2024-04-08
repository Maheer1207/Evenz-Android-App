package com.example.evenz;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

/**
 * View the details of the event and given a role allows
 * the user to interact with the event:
 * attendee + signup: allows user to sign up to event
 * attendee + checkout: allows the user to check out of the event
 * organizer: allows organizer to view their event
 */
public class EventDetailsActivity  extends AppCompatActivity {
    private String eventID;
    private ImageView eventPoster, homeButton, browseEvent, profileButton;
    private TextView eventLocation, eventDetail, eventWelcomeNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle b = getIntent().getExtras();
        assert b != null;
        String role = b.getString("role");
        eventID = b.getString("eventID");

        if (Objects.equals(role, "attendee")) {
            setContentView(R.layout.attendee_event_info_sign_up);
            setupAttendeeView();
        } else {
            setContentView(R.layout.org_event_info);
            setupOrganizerView();
        }
        if (!eventID.isEmpty()) {
            fetchEventDetailsAndNotifications(eventID);
        }
    }

    /**
     * sets up the view that the attendee will have when viewing an event
     * depending on their status they have different actions
     * signup: allows user to sign up to event
     * checkout: allows the user to check out of the event
     */
    private void setupAttendeeView() {
        eventPoster = findViewById(R.id.poster_attendee_eventInfo);
        eventLocation = findViewById(R.id.loc_attendee_eventInfo);
        eventDetail = findViewById(R.id.info_attendee_eventInfo);
        eventWelcomeNote = findViewById(R.id.attendee_event_detail_welcome);
        homeButton = findViewById(R.id.home_event_details_attendee);
        browseEvent = findViewById(R.id.event_list);
        profileButton = findViewById(R.id.attendees_admin_browse_event);

        browseEvent.setOnClickListener (
                v -> startActivity(new Intent(EventDetailsActivity.this, EventSignedUpForAttendeeBrowse.class))
        );

        profileButton.setOnClickListener(v -> {
            Intent intent = new Intent(EventDetailsActivity.this, UserEditProfileActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("role", "attendee");
            intent.putExtras(bundle);
            startActivity(intent);
        });
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openHomePageIntent("attendee", eventID);
            }
        });
        TextView signUpButton = findViewById(R.id.sign_up_button);
        View lightButton= findViewById(R.id.light_green_button_rect);//this is for changing the color of the button
        String source = getIntent().getStringExtra("source");
        if ("homepage".equals(source)) {
            lightButton.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.red)); //If they need checkout button set color to be RED
            signUpButton.setText("Check Out");
        } else if ("browse".equals(source)) {
            signUpButton.setText("Sign Up");
        }
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the device ID
                String userID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

                String source = getIntent().getStringExtra("source");
                if ("homepage".equals(source)) {
                    new AlertDialog.Builder(EventDetailsActivity.this)
                            .setTitle("Check Out Confirmation")
                            .setMessage("Are you sure you want to check out of the event?")
                            .setPositiveButton("Yes", (dialog, which) -> {
                                // Call method to remove event from user's list of events
                                FirebaseUserManager firebaseUserManager = new FirebaseUserManager();
                                firebaseUserManager.checkOutUser(userID);

                                EventUtility.removeAttendeeFromEvent(userID, eventID);//remove user from event

                                // Display toast message
                                Toast.makeText(EventDetailsActivity.this, "Successfully Checked Out of Event!!!", Toast.LENGTH_SHORT).show();

                                // Close the activity and go back
                                Intent intent = new Intent(EventDetailsActivity.this, HomeScreenActivity.class);
                                startActivity(intent);
                            })
                            .setNegativeButton("No", null)
                            .show();
                } else if ("browse".equals(source)) {
                    // check the attendee limit for the event
                    FirebaseUserManager firebaseUserManager = new FirebaseUserManager();
                    Task<Boolean> eventFull = EventUtility.eventFull(getIntent().getStringExtra("eventID"), userID);
                    eventFull.addOnSuccessListener(new OnSuccessListener<Boolean>() {
                        @Override
                        public void onSuccess(Boolean isFull) {
                            if (!isFull) {
                                // Call  addUserToEvent method. This will add the user to the event
                                EventUtility.addUserToEvent(userID, eventID);

                                // Add user to the list of events they've signed up for
                                firebaseUserManager.addEventToUser(userID, eventID);

                                // Display toast message
                                Toast.makeText(EventDetailsActivity.this, "Successfully Signed Up for Event!!!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(EventDetailsActivity.this, HomeScreenActivity.class);
                                intent.putExtra("role", "attendee");
                                intent.putExtra("eventID", eventID);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                startActivity(intent);
                            }
                            else {
                                Toast.makeText(EventDetailsActivity.this, "Sorry, Event is full", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(EventDetailsActivity.this, HomeScreenActivity.class);
                                intent.putExtra("role", "attendee");
                                intent.putExtra("eventID", eventID);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                startActivity(intent);
                            }

                        }
                    });
                    // Close the activity and go back
                    finish();

                }
            }
        });
        eventLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMapIntent(eventID, "attendee", (String) eventLocation.getText());
            }
        });
    }

    /**
     * sets up the organizer to view their own event
     * including all details as the user would see
     */
    private void setupOrganizerView() {
        eventPoster = findViewById(R.id.poster_org_eventInfo);
        eventLocation = findViewById(R.id.loc_org_eventInfo);
        eventDetail = findViewById(R.id.info_org_eventInfo);
        eventWelcomeNote = findViewById(R.id.org_event_detail_welcome);
        homeButton = findViewById(R.id.home_event_details_org);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openHomePageIntent("organizer", eventID);
            }
        });

            ImageView shareQR = findViewById(R.id.shareQR_event_details);
            shareQR.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Create an AlertDialog.Builder
                    AlertDialog.Builder builder = new AlertDialog.Builder(EventDetailsActivity.this);
                    builder.setTitle("Choose QR Code Type")
                            .setItems(new String[]{"Promotional QR code", "Check-in QR code"}, (dialog, which) -> {
                                String qrCodeType =(which == 0) ? "/sign_up" : "/check_in"; // 0 for promotional, 1 for check-in

                                QRGenerator test = new QRGenerator();
                                Bitmap bitmap = test.generate(eventID, qrCodeType, 400, 400);//generate with a string that we can parse
                                Uri bitmapUri = saveBitmapToCache(bitmap);

                                Intent intent = new Intent(EventDetailsActivity.this, ShareQRActivity.class);
                                intent.putExtra("eventID", eventID);
                                if(which == 0) {
                                    intent.putExtra("qrCodeType", "Promotional");
                                } else {
                                    intent.putExtra("qrCodeType", "Check-in");
                                }
                                assert bitmapUri != null;
                                intent.putExtra("BitmapImage", bitmapUri.toString());
                                startActivity(intent);
                            });
                    // Create and show the AlertDialog
                    builder.create().show();
            }
        });

        eventLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMapIntent(eventID, "organizer", (String) eventLocation.getText());
            }
        });

        findViewById(R.id.attendees_list_event_details).setOnClickListener(v -> openAttendeeListIntent());
    }

    /**
     * opens the intent for the organizer to see attendee list
     */
    private void openAttendeeListIntent(){
        Intent intent = new Intent(EventDetailsActivity.this, AttendeesActivity.class);
        startActivity(intent);
    }

    /**
     * opens the intent for the organizer to see the map of user locations
     * @param eventID id of event
     * @param role organizers role
     * @param addressString location of event
     */
    private void openMapIntent(String eventID, String role, String addressString) {
        Intent intent = new Intent(EventDetailsActivity.this, MapsActivity.class);

        intent.putExtra("eventID", eventID);
        intent.putExtra("role", role);
        intent.putExtra("addressString", addressString);
        intent.putExtra("from", "eventDetails");
        startActivity(intent);
    }

    /**
     * goes back to main homepage of event
     * @param role role of user
     * @param eventID id of current event
     */
    private void openHomePageIntent(String role, String eventID) {
        Intent intent = new Intent(new Intent(EventDetailsActivity.this, HomeScreenActivity.class));
        Bundle b = new Bundle();
        b.putString("role", role);
        b.putString("eventID", eventID);
        intent.putExtras(b);
        startActivity(intent);
    }

    private Uri saveBitmapToCache(Bitmap bitmap) {
        try {
            File cachePath = new File(getCacheDir(), "images");
            cachePath.mkdirs(); // don't forget to make the directory
            FileOutputStream stream = new FileOutputStream(cachePath + "/image.png"); // overwrites this image every time
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.close();

            File imagePath = new File(getCacheDir(), "images");
            File newFile = new File(imagePath, "image.png");
            return Uri.fromFile(newFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * gets the notifcations and details of the event
     * and sets them in the view
     * @param eventId id of event to set
     */
    private void fetchEventDetailsAndNotifications(String eventId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("events").document(eventId).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot != null && documentSnapshot.exists()) {
                Event event = documentSnapshot.toObject(Event.class);
                // Directly update the TextView with the event's location

                if (event != null) {
                    String eventName = event.getEventName();
                    eventWelcomeNote.setText("\uD83D\uDC4B Welcome to " + event.getEventName() + "! \uD83D\uDE80");
                    eventDetail.setText(event.getDescription());
                    eventDetail.setMovementMethod(new ScrollingMovementMethod());
                    eventLocation.setText(event.getLocation());
                    displayImage(event.getEventPosterID(), eventPoster);
                }
            } else {
                // TODO: Handle the case where the event doesn't exist in the database
            }
        }).addOnFailureListener(e -> {
            // TODO: handle errors
        });
    }

    /**
     * given an image id display an image from firebase
     * @param imageID id of image
     * @param imgView imageview to place image on
     */
    private void displayImage(String imageID, ImageView imgView)
    {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference photoReference= storageReference.child("images/" + imageID);

        final long ONE_MEGABYTE = 1024 * 1024;
        photoReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                imgView.setImageBitmap(bmp);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(EventDetailsActivity.this, "No Such file or Path found!!", Toast.LENGTH_LONG).show();
            }
        });
    }
}
