
package com.example.evenz;


import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Hashtable;

/**
 * AdminDelete Event, extends AppcompactActivity produces the activity allowing the administrator
 * to view in-detail, and delete a previously selected Event from the Firestore Database.
 * has no constructors nor methods as it is an activity class, aside from oncreate().
 * Associated with XML file "admin_delete_event.xml" to provide the UI.
 */
public class AdminDeleteEvent extends AppCompatActivity{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_delete_event);


        Bundle rb = getIntent().getExtras();
        String etitle = rb.getString("eventName");
        String edesc = rb.getString("eventDesc");

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference dev = db.collection("events");

        TextView eventname = findViewById(R.id.event_Name2);
        TextView location = findViewById(R.id.location234);
        TextView date = findViewById(R.id.delete_event_admin_date);
        TextView orgname = findViewById(R.id.orgdata);
        TextView desc = findViewById(R.id.continer_event_summary_date_icon1);
        ImageView imgV = findViewById(R.id.image_career_fairs_web_banner);

        eventname.setText(etitle);
        location.setText(rb.getString("location"));
        date.setText(rb.getString("eventDate"));
        orgname.setText(rb.getString("organizationame"));
        desc.setText(edesc);

        ImageUtility.displayImage(rb.getString("imgID"), imgV);



        final String[] docID = new String[1];
        dev.whereEqualTo("eventName", etitle).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
              @Override
              public void onComplete(@NonNull Task<QuerySnapshot> task) {
                  if (task.isSuccessful()) {
                      for (QueryDocumentSnapshot document : task.getResult()) {
                          Log.d("Ben8888", "Retrieved copy");
                          Log.d("Ben8888", document.getId());

                          docID[0] = document.getId();
                      }
                  }
                  else {
                      Log.d("Ben8888", "Failure");
                  }

              }
        });

        findViewById(R.id.deletebutton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dev.document(docID[0]).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        finish();
                    }

                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
                startActivity(new Intent(AdminDeleteEvent.this, AdminBrowseEventActivity.class));
            }
        });
        findViewById(R.id.photo_browse).setOnClickListener(v -> startActivity(new Intent(AdminDeleteEvent.this, ImageBrowseActivity.class)));
        findViewById(R.id.attendees_admin_browse_event).setOnClickListener(v -> startActivity(new Intent(AdminDeleteEvent.this, AdminBrowseProfilesActivity.class)));
        findViewById(R.id.back_attendee_browse_events).setOnClickListener(v -> finish());
    }
}
