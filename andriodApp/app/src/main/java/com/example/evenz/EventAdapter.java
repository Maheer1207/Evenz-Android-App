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

/**
 * The {@code EventAdapter} class is a RecyclerView adapter responsible for binding event data to
 * corresponding views in the UI. It facilitates the display of a list of events in a RecyclerView.
 *
 * <p>This adapter is designed to work with the {@code EventViewHolder} class, which defines the
 * structure of individual item views within the RecyclerView.
 *
 * <p>The class also includes methods for handling the creation of view holders, binding data to
 * views, and determining the total number of items in the dataset.
 *
 * <p>Additionally, it provides a method to asynchronously load event poster images from Firebase
 * Storage based on the event's poster ID.
 *
 * @author hrithick
 * @version 1.0
 * @see RecyclerView
 * @see EventViewHolder
 */
//TODO: make sure Event ID is set up correctly, when storing new events

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private ArrayList<Event> eventDataList;
    private Context context;
    /**
     * Constructs an {@code EventAdapter} with the provided context and event data list.
     *
     * @param context       The context of the calling component.
     * @param eventDataList The list of events to be displayed.
     */
    public EventAdapter(Context context, ArrayList<Event> eventDataList) {
        this.context = context;
        this.eventDataList = eventDataList;
    }
    /**
     * The {@code EventViewHolder} class defines the structure of individual item views within the
     * RecyclerView. It holds references to the layout elements for each event item.
     */
    public static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView textCategories, textDate, textLocation, textDescription;
        ImageView imageBanner; // Assuming this is for eventPosterID as a URL or drawable resource.
        /**
         * Constructs an {@code EventViewHolder} with the specified item view.
         *
         * @param itemView The view representing an individual item in the RecyclerView.
         */
        public EventViewHolder(View itemView) {
            super(itemView);
            textCategories = itemView.findViewById(R.id.text_categories);
            textDate = itemView.findViewById(R.id.text_date);
            textLocation = itemView.findViewById(R.id.text_location);
            textDescription = itemView.findViewById(R.id.text_lorem_ipsum);
            imageBanner = itemView.findViewById(R.id.image_banner);
        }
    }
    /**
     * Called when RecyclerView needs a new {@code EventViewHolder} of the given type to represent
     * an item.
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The type of the new View.
     * @return A new {@code EventViewHolder} that holds a View of the given view type.
     */
    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event, parent, false);
        return new EventViewHolder(itemView);
    }
    /**
     * Called by RecyclerView to display the data at the specified position. This method should
     * update the contents of the {@code EventViewHolder} to reflect the item at the given
     * position.
     *
     * @param holder   The {@code EventViewHolder} that represents the item at the given position.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = eventDataList.get(position);

        holder.textCategories.setText(event.getEventName());
        holder.textDescription.setText(event.getDescription());
        // Display event poster image asynchronously
        this.displayImage(event.getEventPosterID(), holder.imageBanner);

        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        holder.textDate.setText(dateFormat.format(event.getEventDate()));

        // Assuming you have a method to get a displayable location string from Geolocation
        holder.textLocation.setText(event.getLocation()); // You need to adjust this based on your Geolocation data.

    }
    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in the data set.
     */
    @Override
    public int getItemCount() {
        return eventDataList.size();
    }
    /**
     * Asynchronously loads and displays an event poster image from Firebase Storage based on the
     * provided image ID.
     *
     * @param imageID The ID of the event poster image.
     * @param imgView The ImageView in which to display the loaded image.
     */
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
