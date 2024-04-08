package com.example.evenz;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter class utilized to adapt arraylists of Attendee-objects to be displayed in
 * recyclerView in the android UI.
 * extends RecyclerView.Adapter.
 * contains field storing array of Attendee-objects
 * Has constructors that can either initalize it with the to-be-adapted List of User-objects,
 * or the list, and an EventID.
 */
class AttendeeAdapter extends RecyclerView.Adapter<AttendeeAdapter.ViewHolder> {

    private final List<User> userList;
    private String eventID;

    public AttendeeAdapter(List<User> userList) {
        this.userList = userList;
        logUsers();
    }

    public AttendeeAdapter(List<User> userList, String eventID) {
        this.userList = userList;
        this.eventID = eventID;
        logUsers();
    }

    public void logUsers() {
        for (User user : userList) {
            Log.d("UserAdapter", "Name: " + user.getName() + ", Email: " + user.getEmail());
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_attendee, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        User user = userList.get(position);
        holder.nameTextView.setText(user.getName());
        holder.contactInfoTextView.setText(String.format("%s\n%s", user.getPhone(), user.getEmail()));
        Task<ArrayList<Integer>> userAttendCount = EventUtility.userAttendCount(eventID, user.getUserId());

        userAttendCount.addOnSuccessListener(new OnSuccessListener<ArrayList<Integer>>() {
            @Override
            public void onSuccess(ArrayList<Integer> integers) {
                if (integers != null && !integers.isEmpty()) {
                    holder.attendCount.setText(integers.get(0).toString()); // Set the count
                    if (integers.get(1) == 1) { // Check the status
                        holder.typeTextview.setText("Checked In");
                        holder.rootLayout.setBackgroundResource(R.drawable.light_green_button_setup);
                    }
                }
                notifyDataSetChanged(); // Notify the adapter that the data set has changed
            }
        });

        this.displayImage(user.getProfilePicID(), holder.profileImageView);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout rootLayout;

        ImageView profileImageView;
        TextView nameTextView;
        TextView attendCount;
        TextView contactInfoTextView;

        TextView typeTextview;

        public ViewHolder(View itemView) {
            super(itemView);
            profileImageView = itemView.findViewById(R.id.image_banner);
            nameTextView = itemView.findViewById(R.id.text_categories);
            typeTextview = itemView.findViewById(R.id.text_title);
            contactInfoTextView = itemView.findViewById(R.id.text_contact_info);
            attendCount = itemView.findViewById(R.id.attendCount);
            rootLayout = itemView.findViewById(R.id.root_layout);

        }
    }

    public void displayImage(String id, ImageView view)
    {
        ImageUtility.displayImage(id, view);
    }
}