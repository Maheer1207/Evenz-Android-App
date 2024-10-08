package com.example.evenz;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;


import java.util.ArrayList;

import android.widget.ArrayAdapter;
import android.widget.Spinner;

/**
 * an activity to allow an organizer to send a notification to their event
 */
public class OrgSendNotificationActivity extends AppCompatActivity {
    RelativeLayout post;
    ImageView back;
    String eventID;
    Spinner spinnerNotificationType;
    EditText editTextNotificationInfo;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle b = getIntent().getExtras();
        assert b != null;
        eventID = b.getString("eventID");

        setContentView(R.layout.org_send_event_notification);

        spinnerNotificationType = findViewById(R.id.spinnerNotificationType);
        editTextNotificationInfo = findViewById(R.id.editTextNotificationInfo);

        // Populate the spinner
        ArrayList<String> notificationTypes = new ArrayList<>();
        notificationTypes.add("Food");
        notificationTypes.add("Recreation");
        notificationTypes.add("Announcement");
        notificationTypes.add("Milestones");
        notificationTypes.add("Urgent");
        notificationTypes.add("Presentation");
        notificationTypes.add("Networking & Socials");
        notificationTypes.add("Workshop");
        notificationTypes.add("Mystery");
        notificationTypes.add("Other");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, notificationTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerNotificationType.setAdapter(adapter);

        post = findViewById(R.id.post_notification_button);
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initUI();
                postNotification();
                Intent intent = new Intent(new Intent(OrgSendNotificationActivity.this, HomeScreenActivity.class));
                Bundle b = new Bundle();
                b.putString("role", "organizer");
                b.putString("eventID", eventID);
                intent.putExtras(b);
                startActivity(intent);
            }
        });

        back = findViewById(R.id.back_send_notification);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(new Intent(OrgSendNotificationActivity.this, HomeScreenActivity.class));
                Bundle b = new Bundle();
                b.putString("role", "organizer");
                b.putString("eventID", eventID);
                intent.putExtras(b);
                startActivity(intent);
            }
        });
    }

    private void initUI() {
        // Removed editTextNotificationType as we're using a spinner now
        editTextNotificationInfo = findViewById(R.id.editTextNotificationInfo);
    }

    /**
     * post the notification that has been created
     */
    private void postNotification () {
        String notificationType = spinnerNotificationType.getSelectedItem().toString();
        if (notificationType.equals("Select:")) {
            // Prompt the user to select a notification type
            Toast.makeText(this, "Please select a notification type.", Toast.LENGTH_SHORT).show();
            return; // Do not proceed further
        }
        String notificationInfo = editTextNotificationInfo.getText().toString().trim();

        fetchEventDetailsAndNotifications(eventID, notificationType, notificationInfo);
    }

    private void fetchEventDetailsAndNotifications(String eventID, String notificationType, String notificationInfo) {
        EventUtility.notificationOps(notificationType, notificationInfo, eventID, 1);
    }
}