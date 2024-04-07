package com.example.evenz;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.List;

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
        Task<Integer> userAttendCount = EventUtility.userAttendCount(eventID, user.getUserId());
        userAttendCount.addOnSuccessListener(new OnSuccessListener<Integer>() {
            @Override
            public void onSuccess(Integer integer) {
                holder.attendCount.setText(integer.toString());
            }
        });
        this.displayImage(user.getProfilePicID(), holder.profileImageView);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView profileImageView;
        TextView nameTextView;
        TextView attendCount;
        TextView contactInfoTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            profileImageView = itemView.findViewById(R.id.image_banner);
            nameTextView = itemView.findViewById(R.id.text_categories);
            contactInfoTextView = itemView.findViewById(R.id.text_contact_info);
            attendCount = itemView.findViewById(R.id.attendCount);
        }
    }

    public void displayImage(String id, ImageView view)
    {
        ImageUtility.displayImage(id, view);
    }
}