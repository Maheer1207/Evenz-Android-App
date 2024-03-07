package com.example.evenz;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class AttendeesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AttendeeAdapter adapter;
    private List<Attendee> attendeesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_attendees_at_event); // Make sure you have a layout file named activity_attendees.xml

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize mock data
        attendeesList = new ArrayList<>();
        attendeesList.add(new Attendee("John Doe", "1234567890", "780-123-4567", "jhondoe@gmail.com"));
        attendeesList.add(new Attendee("Jane Doe", "1234567890", "780-123-4567", "jane@gmail.com"));
        attendeesList.add(new Attendee("John Smith", "1234567890", "780-123-4567", "smith@gmail.com"));

        adapter = new AttendeeAdapter(attendeesList);
        recyclerView.setAdapter(adapter);
    }
}

