package com.example.evenz;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.widget.Toast;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

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

        this.displayImage(event.getEventPosterID(), holder.imageBanner);

        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        holder.textDate.setText(dateFormat.format(event.getEventDate()));

        // Assuming you have a method to get a displayable location string from Geolocation
        holder.textLocation.setText(event.getLocation());

    }

    @Override
    public int getItemCount() {
        return eventDataList.size();
    }

    private void displayImage(String imageID, ImageView imgView)
    {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference photoReference= storageReference.child("images/" + imageID);

        final long ONE_MEGABYTE = 1024 * 1024;
        photoReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                imgView.setImageBitmap(bmp);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(context.getApplicationContext(), "No Such file or Path found!!", Toast.LENGTH_LONG).show();
            }
        });
    }
}
