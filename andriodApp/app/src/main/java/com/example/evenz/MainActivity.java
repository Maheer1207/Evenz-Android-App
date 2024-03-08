package com.example.evenz;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button edit_user_profile = findViewById(R.id.button_create_new_event);
        edit_user_profile.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, edit_profile_activity.class);
                startActivity(intent);
            }
        });

        final Button admin_event_browse = findViewById(R.id.button_admin_event_browse);
        admin_event_browse.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, admin_browse_profile_activity.class);
                startActivity(intent);
            }
        });
    }
}