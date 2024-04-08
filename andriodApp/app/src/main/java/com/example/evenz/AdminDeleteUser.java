
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
public class  AdminDeleteUser extends AppCompatActivity{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("Ben8888", "JOYJOYLALAALAWORLD");
        setContentView(R.layout.admin_delete_user);


        Bundle rb = getIntent().getExtras();
        String uname = rb.getString("x1");
        String uid = rb.getString("x6");
        String pfp = rb.getString("x4");

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference dev = db.collection("users");
        TextView eventname = findViewById(R.id.event_Name2);
        eventname.setText(uname);
        /*

        TextView location = findViewById(R.id.location234);
        TextView date = findViewById(R.id.delete_event_admin_date);
        TextView orgname = findViewById(R.id.orgdata);
        TextView desc = findViewById(R.id.continer_event_summary_date_icon1);
        ImageView imgV = findViewById(R.id.image_career_fairs_web_banner);
        TextView attendeeCnt = findViewById(R.id.attendeecntdata);


        eventname.setText(etitle);
        location.setText(rb.getString("location"));
        date.setText(rb.getString("eventDate"));
        orgname.setText(rb.getString("organizationame"));
        desc.setText(edesc);
        attendeeCnt.setText(rb.getString("AttendLimit"));


        ImageUtility x = new ImageUtility();
        x.displayImage(rb.getString("imgID"), imgV);

   */



        findViewById(R.id.deletebutton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Log.d("Ben8888", "trying to delete: " + uname);
                dev.document(uid).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("Ben8888", "successful delete");
                        finish();
                    }

                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Ben8888", "delete failed?");
                        Log.d("Ben8888",  e.toString());
                    }
                });
                startActivity(new Intent(AdminDeleteUser.this, AdminBrowseProfilesActivity.class));
            }
        });

    }
}
