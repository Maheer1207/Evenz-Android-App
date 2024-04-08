package com.example.evenz;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class AttendeesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ImageView shareQR;
    private AttendeeAdapter adapter;
    private List<User> attendeesList;
    private TextView limit;
    private String limitString;
    private String eventID;
    private FirebaseUserManager firebaseUserManager;
    private RelativeLayout event_loc;
    private FirebaseAttendeeManager firebaseAttendeeManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_attendees_at_event);

        initializeComponents();

        String deviceID = getDeviceID();
        //String deviceID = "1";
        fetchEventID(deviceID);
    }

    private void initializeComponents() {
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        limit = findViewById(R.id.header_frame_text);
        limitString = "Attendees ";

        firebaseUserManager = new FirebaseUserManager();

        findViewById(R.id.home_attendee_limit).setOnClickListener(v-> startActivity(new Intent(AttendeesActivity.this, HomeScreenActivity.class)));

        event_loc = findViewById(R.id.event_location);
        event_loc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Task<String> getEventLocation = EventUtility.getEventLocation(eventID);
                getEventLocation.addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String addr) {
                        Intent intent = new Intent(AttendeesActivity.this, MapsActivity.class);

                        intent.putExtra("eventID", eventID);
                        intent.putExtra("role", "organizer");
                        intent.putExtra("addressString", addr);
                        intent.putExtra("from", "attendee_list");
                        startActivity(intent);
                    }
                });
            }
        });

        //updated share QR option to have promotional and check-in QR code options
        shareQR = findViewById(R.id.shareQR_attendee_limit);
        shareQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an AlertDialog.Builder
                AlertDialog.Builder builder = new AlertDialog.Builder(AttendeesActivity.this);
                builder.setTitle("Choose QR Code Type")
                        .setItems(new String[]{"Promotional QR code", "Check-in QR code"}, (dialog, which) -> {
                            String qrCodeType =(which == 0) ? "/sign_up" : "/check_in"; // 0 for promotional, 1 for check-in

                            QRGenerator test = new QRGenerator();
                            Bitmap bitmap = test.generate(eventID, qrCodeType, 400, 400);//generate with a string that we can parse
                            Uri bitmapUri = saveBitmapToCache(bitmap);

                            Intent intent = new Intent(AttendeesActivity.this, ShareQRActivity.class);
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
    }

    // This method saves the bitmap to cache and returns the Uri
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

    private void fetchEventID(String deviceID) {
        Task<String> getEventID = firebaseUserManager.getEventName(deviceID);
        // add an on success listener print the event id as a toast
        getEventID.addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String eventID) {
                fetchAttendees(eventID);
                // log the event id
                Log.d(TAG, "onSuccess: Event ID: " + eventID);

            }
        });

        // add an on failure listener
        getEventID.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AttendeesActivity.this, "Error fetching event ID: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void fetchAttendees(String eventID) {
        this.eventID = eventID;
        Task<List<User>> getAttendeesTask = firebaseUserManager.getAttendeesForEvent(eventID);
        // add an on success listener to update the UI and display the attendees in toast
        getAttendeesTask.addOnSuccessListener(new OnSuccessListener<List<User>>() {
            @Override
            public void onSuccess(List<User> attendees) {
                updateUI(attendees);
                setHeaderString(eventID);
                // log the attendees
//                Log.d(TAG, "onSuccess: Attendees: " + attendees);
            }
        });
        // on failure listener to handle the error
        getAttendeesTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                handleAttendeesFailure(e);
            }
        });
    }

    private void updateUI(List<User> attendees) {
        attendeesList = attendees;
        adapter = new AttendeeAdapter(attendeesList, eventID);
        recyclerView.setAdapter(adapter);
    }

    // Please create a method that will set the value of the header string
    // to the number of attendees at the event. The header string should be
    // in the format "Attendees: X/Y" where X is the number of attendees. and Y is the limit.
    private void setHeaderString(String eventID) {
        int attendees = attendeesList.size();
        // Get the max attendees for the event from the getattendlimitfromeventname method in the eventutility class
        EventUtility.getAttendLimitFromEventName(eventID);
        Task<Integer> getAttendLimit = EventUtility.getAttendLimitFromEventName(eventID);
        getAttendLimit.addOnSuccessListener(new OnSuccessListener<Integer>() {
            @Override
            public void onSuccess(Integer maxAttendees) {
                // Convert the max attendees to a string
                limitString = "Attendees: " + attendees + "/" + maxAttendees;
                limit.setText(limitString);
                if (attendees == maxAttendees) {
                    Toast.makeText(AttendeesActivity.this, "MileStones: Event is HouseFull!!", Toast.LENGTH_LONG).show();
                } else if ((double) attendees /maxAttendees >= 0.75 && (double) attendees /maxAttendees < 0.8) {
                    Toast.makeText(AttendeesActivity.this, "MileStones: Event reached its 75% capacity!!", Toast.LENGTH_LONG).show();
                } else if ((double) attendees /maxAttendees >= 0.5 && (double) attendees /maxAttendees < 0.6) {
                    Toast.makeText(AttendeesActivity.this, "MileStones: Event reached its 50% capacity!!", Toast.LENGTH_LONG).show();
                } else if ((double) attendees /maxAttendees >= 0.25 && (double) attendees /maxAttendees < 0.3) {
                    Toast.makeText(AttendeesActivity.this, "MileStones: Event reached its 25% capacity!!", Toast.LENGTH_LONG).show();
                }
            }
        });
        // Handle the failure of the getAttendLimit task
        getAttendLimit.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "onFailure: Error fetching attendees limit", e);
            }
        });

    }

    private void handleAttendeesFailure(@NonNull Exception e) {
        // print the error message to log
        Log.e(TAG, "onFailure: Error fetching attendees", e);
    }

    public void onHeaderIconClicked(View view) {
        Intent intent = new Intent(this, HomeScreenActivity.class);
        startActivity(intent);
    }

    @SuppressLint("HardwareIds")
    private String getDeviceID() {
        return Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
    }
}