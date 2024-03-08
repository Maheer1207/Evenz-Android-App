// code for image upload based on https://www.geeksforgeeks.org/android-how-to-upload-an-image-on-firebase-storage/

package com.example.evenz;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private CollectionReference eventsRef;
    private CollectionReference usersRef;
    private ImageView imageView;
    private Uri filePath;
    FirebaseStorage storage;
    StorageReference storageReference;

    private ArrayList<Event> eventDataList;
    private ArrayList<User> userDataList;
    private Button select;
    private Button upload;

    private final int PICK_IMAGE_REQUEST = 22;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        eventDataList = new ArrayList<>();
        userDataList = new ArrayList<>();

        select = findViewById(R.id.select);
        upload = findViewById(R.id.upload);
        imageView = findViewById(R.id.image);

        db = FirebaseFirestore.getInstance();
        eventsRef = db.collection("events");
        usersRef = db.collection("users");

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                select();
            }
        });

        StorageReference profilePicRef = storageReference.child("images/asdf");
        //StorageReference profilePicImageRef = storageRef.child("images/profilePic.jpg");

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upload();
            }
        });

        // adds all events currently in database to a event list
        eventsRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshots,
                                @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e("Firestore", error.toString());
                    return;
                }
                if (querySnapshots != null) {
                    eventDataList.clear();
                    for (QueryDocumentSnapshot doc : querySnapshots) {
                        String eventID = doc.getId();
                        Event tempEvent = new Event(doc.getString("eventID"), doc.getString("eventName"), doc.getString("eventPosterID"),
                                doc.getString("description"), (Date) doc.get("date"), (Geolocation) doc.get("geolocation"),
                                (Bitmap) doc.get("qrCodeBrowse"), (Bitmap) doc.get("qrCodeCheckIn"), (Map<String, Integer>) doc.get("userList"));
                        Log.d("Firestore", String.format("Event(%s, %s) fetched", eventID, tempEvent.getEventName()));
                        eventDataList.add(tempEvent);
                    }
                }
            }
        });

        // adds all users currently in database to a user list
        usersRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshots,
                                @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e("Firestore", error.toString());
                    return;
                }
                if (querySnapshots != null) {
                    userDataList.clear();
                    for (QueryDocumentSnapshot doc : querySnapshots) {
                        String userID = doc.getId();
                        User tempUser = new User(doc.getString("userID"), doc.getString("userType"), doc.getString("name"), doc.getString("profilePicID"), doc.getString("phone"), doc.getString("email"));
                        userDataList.add(tempUser);
                    }
                }
            }
        });

        // displays an image from firebase storage
        displayImage("b647c5a6-aad8-47c9-a871-5be6ea32444c", findViewById(R.id.profPic));
    }

    // gets data for profile picture when user has selected from camera
    // FOR UPLOADING PROFILE PICTURES
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
     * This function adds a user to the database
     *
     * @param id   The id of the user
     * @param user The contents of the user (all variables from user class)
     */
    private void addUser(String id, User user) {
        HashMap<String, User> data = new HashMap<>();
        data.put(id, user);
        usersRef.document(id).set(data);
    }

    /**
     * This function deletes a user from the database
     *
     * @param id The id of the user to be deleted
     */
    private void deleteUser(String id) {
        usersRef.document(id).delete();
    }

    /**
     * This function adds an event to the database
     *
     * @param id    The id of the event to be added
     * @param event The contents of the event (all variables from event class)
     */
    private void addEvent(String id, Event event) {
        HashMap<String, Event> data = new HashMap<>();
        data.put(id, event);
        eventsRef.document(id).set(data);

        for (Map.Entry<String, Integer> entry : event.getAttendeeIDList().entrySet())
        {
            eventsRef.document(id).update("AttendeeList." + entry.getKey(), entry.getValue());
        }
    }

    /**
     * This function deletes an event from the database
     *
     * @param id The id of the event to be deleted
     */
    private void deleteEvent(String id) {
        usersRef.document(id).delete();
    }

    /**
     * Gets the user to enter their camera/gallery to select a photo, sets the activity filepath to the selected image
     */
    private void select() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image from here..."), PICK_IMAGE_REQUEST);
    }

    /**
     * Given that filePath has already been defined within the activity, uploads an image previously selected by the user
     * This includes adding the image to the firebase storage with proper notifications for upload progress.
     */
    private void upload() {
        if (filePath != null) {
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            // Defining the child of storageReference
            StorageReference ref = storageReference.child("images/" + UUID.randomUUID().toString());

            // adding listeners on upload
            // or failure of image
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            // Image uploaded successfully
                            // Dismiss dialog
                            progressDialog.dismiss();
                            Toast.makeText(MainActivity.this, "Image Uploaded!!", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            // Error, Image not uploaded
                            progressDialog.dismiss();
                            Toast.makeText(MainActivity.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
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

    /**
     * Displays an image from the firebase storage given
     * @param imageID id of the image being displayed
     * @param imgView The image view to place the image on
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
                Toast.makeText(getApplicationContext(), "No Such file or Path found!!", Toast.LENGTH_LONG).show();
            }
        });
    }
}