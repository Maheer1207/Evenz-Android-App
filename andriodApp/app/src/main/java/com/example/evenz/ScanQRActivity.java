
package com.example.evenz;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.List;

public class ScanQRActivity extends AppCompatActivity {

    private List<Attendee> attendeesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        IntentIntegrator intent =  new IntentIntegrator(ScanQRActivity.this);
        intent.setOrientationLocked(true);
        intent.setPrompt("Scan a QR code");
        intent.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        intent.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if(intentResult != null) {
            String contents = intentResult.getContents();

            if (contents != null) {
                Intent intent = new Intent(ScanQRActivity.this, HomeScreenActivity.class); //TODO: replace with ORG homepage

                FirebaseAttendeeManager firebaseAttendeeManager = new FirebaseAttendeeManager();

                Task<List<Attendee>> getAttendeesTask = firebaseAttendeeManager.getEventAttendees(contents);

                // Add a listener to handle the result when the task completes
                getAttendeesTask.addOnSuccessListener(new OnSuccessListener<List<Attendee>>() {
                    @Override
                    public void onSuccess(List<Attendee> attendees) {
                        attendeesList = attendees;
                        Task<Integer> getAttendLimit = EventUtility.getAttendLimit(contents);
                        getAttendLimit.addOnSuccessListener(new OnSuccessListener<Integer>() {
                            @Override
                            public void onSuccess(Integer attendLimit) {
                                if (attendLimit != -1) {
                                    startActivity(intent);
                                }
                                //else if (attendLimit == contents) {

                                //}
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle the error case
                        //Toast.makeText(AttendeesActivity.this, "Error fetching attendeeLimit: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}