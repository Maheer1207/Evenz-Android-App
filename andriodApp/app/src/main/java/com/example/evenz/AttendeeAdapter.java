package com.example.evenz;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

class AttendeeAdapter extends RecyclerView.Adapter<AttendeeAdapter.ViewHolder> {

    private final List<Attendee> attendeesList;

    public AttendeeAdapter(List<Attendee> attendeesList) {
        this.attendeesList = attendeesList;
        printAttendees();
    }
//    print the details of the attendees for debugging
    public void printAttendees() {
        for (Attendee attendee : attendeesList) {
            System.out.println(attendee.getName());
            System.out.println(attendee.getEmail());
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
        Attendee attendee = attendeesList.get(position);
        holder.nameTextView.setText(attendee.getName());
        holder.contactInfoTextView.setText(String.format("%s\\n%s", attendee.getPhone(), attendee.getEmail()));
    }

    @Override
    public int getItemCount() {
        return attendeesList.size();
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