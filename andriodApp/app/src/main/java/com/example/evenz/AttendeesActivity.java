package com.example.evenz;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AttendeesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AttendeeAdapter adapter;
    private List<Attendee> attendeesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_attendees_at_event);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FirebaseAttendeeManager attendeeManager = new FirebaseAttendeeManager();

        attendeeManager.getEventAttendees("Drake Concert").addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                attendeesList = task.getResult();
                for (Attendee attendee : attendeesList) {
                    System.out.println(attendee.getName());
                }
                adapter = new AttendeeAdapter(attendeesList);
                recyclerView.setAdapter(adapter);
            } else {
                System.out.println("Task was not successful");
            }
        });
    }

    public void onHeaderIconClicked(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}