package com.example.evenz;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

class AttendeeAdapter extends RecyclerView.Adapter<AttendeeAdapter.ViewHolder> {

    private final List<User> userList;

    public AttendeeAdapter(List<User> userList) {
        this.userList = userList;
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
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView profileImageView;
        TextView nameTextView;
        TextView contactInfoTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            profileImageView = itemView.findViewById(R.id.image_banner);
            nameTextView = itemView.findViewById(R.id.text_categories);
            contactInfoTextView = itemView.findViewById(R.id.text_contact_info);
        }
    }
}