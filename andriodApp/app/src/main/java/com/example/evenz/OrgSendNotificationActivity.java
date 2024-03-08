package com.example.evenz;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class OrgSendNotificationActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.org_send_event_notification);
        findViewById(R.id.back_send_notification).setOnClickListener(v -> finish());
    }
}