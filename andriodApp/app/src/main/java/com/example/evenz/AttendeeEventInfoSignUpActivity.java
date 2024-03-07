package com.example.evenz;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class AttendeeEventInfoSignUpActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendee_event_info_sign_up);
        findViewById(R.id.back_attendee_event_info_signup).setOnClickListener(v -> finish());
    }
}