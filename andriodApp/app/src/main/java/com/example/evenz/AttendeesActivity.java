package com.example.evenz;

import android.os.Bundle;

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
        setContentView(R.layout.view_attendees_at_event); // Make sure you have a layout file named activity_attendees.xml

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize mock data
//        attendeesList = new ArrayList<>();
//        attendeesList.add(new Attendee("Saske", "1234567890", "780-123-4567", "jhondoe@gmail.com"));
//        attendeesList.add(new Attendee("Jane Doe", "1234567890", "780-123-4567", "jane@gmail.com"));
//        attendeesList.add(new Attendee("John Smith", "1234567890", "780-123-4567", "smith@gmail.com"));

        // Mofiy each attendee to all be at the same event
//        for (Attendee attendee : attendeesList) {
//            attendee.addEvent("Drake Concert");
//        }

        // send one of these attendees to the database
        FirebaseAttendeeManager attendeeManager = new FirebaseAttendeeManager();
        // clear the database
//        attendeeManager.clearAttendees();

//        attendeeManager.submitAttendee(attendeesList.get(0));

        // Fetch the attendees from the database and print all of them
//        attendeeManager.getAllAttendeesAsynchronously().addOnCompleteListener(task -> {
//            if (task.isSuccessful()) {
//                List<Attendee> attendees = task.getResult();
//                // Handle the list of attendees
//            } else {
//                // Handle the error
//            }
//        });

        // Fetch the attendees from the database that are at a specific event and print all of them
        attendeeManager.getEventAttendees("Drake Concert").addOnCompleteListener(task -> {
              if (task.isSuccessful()) {
                attendeesList = task.getResult();
                // Print all of the attendees names to the console
                for (Attendee attendee : attendeesList) {
                    System.out.println(attendee.getName());
                }
                adapter = new AttendeeAdapter(attendeesList);
                recyclerView.setAdapter(adapter);
            } else {
                // Handle the error print a message to console saying that the task was not successful
                System.out.println("Task was not successful");
            }
        });

    }
}

