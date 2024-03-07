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
        attendeesList.add(new Attendee("Sara Dean", "(780) 143-8312", "saradean12@gmail.com", R.drawable.container_parent_image_p1));
        attendeesList.add(new Attendee("John Doe", "(780) 555-8312", "johndoe@example.com", R.drawable.container_parent_image_p2));
        attendeesList.add(new Attendee("Jane Smith", "(780) 123-4567", "jane@example.com", R.drawable.container_parent_image_p3));
        attendeesList.add(new Attendee("Mario Ninja", "(780) 123-4567", "mario@ualberta.ca", R.drawable.container_parent_image_p1));
        attendeesList.add(new Attendee("Luigi Ninja", "(780) 123-4567", "luigiNinja@ualberta.ca", R.drawable.container_parent_image_p2));
        attendeesList.add(new Attendee("Princess Peach", "(780) 123-4567", "Princess@ualberta.ca", R.drawable.container_parent_image_p3));
        attendeesList.add(new Attendee("Bowser", "(780) 123-4567", "BowsarTyrone@Ninnga.ca", R.drawable.container_parent_image_p1));
        attendeesList.add(new Attendee("Toad", "(780) 123-4567", "toat@hotmail.ca", R.drawable.container_parent_image_p2));
        // Add more mock attendees as needed

        adapter = new AttendeeAdapter(attendeesList);
        recyclerView.setAdapter(adapter);
    }
}

