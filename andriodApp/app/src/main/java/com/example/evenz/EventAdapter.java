package com.example.evenz;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

//TODO: make sure Event ID is set up correctly, when storing new events

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private ArrayList<Event> eventDataList;
    private Context context;

    public EventAdapter(Context context, ArrayList<Event> eventDataList) {
        this.context = context;
        this.eventDataList = eventDataList;
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView textCategories, textDate, textLocation, textDescription;
        ImageView imageBanner; // Assuming this is for eventPosterID as a URL or drawable resource.

        public EventViewHolder(View itemView) {
            super(itemView);
            textCategories = itemView.findViewById(R.id.text_categories);
            textDate = itemView.findViewById(R.id.text_date);
            textLocation = itemView.findViewById(R.id.text_location);
            textDescription = itemView.findViewById(R.id.text_lorem_ipsum);
            imageBanner = itemView.findViewById(R.id.image_banner);
        }
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event, parent, false);
        return new EventViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = eventDataList.get(position);
        holder.textCategories.setText(event.getEventName());
        holder.textDescription.setText(event.getDescription());

        // Format the date
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        holder.textDate.setText(dateFormat.format(event.getEventDate()));

        // Assuming you have a method to get a displayable location string from Geolocation
        holder.textLocation.setText("Location details"); // You need to adjust this based on your Geolocation data.

        // For image loading from an ID or URL, you'll use a library like Glide or Picasso. Example:
        // Glide.with(context).load(event.getEventPosterID()).into(holder.imageBanner);
    }

    @Override
    public int getItemCount() {
        return eventDataList.size();
    }
}
