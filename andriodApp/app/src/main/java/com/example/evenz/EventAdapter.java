//package com.example.evenz;
//
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import android.content.Context;
//import android.widget.Toast;
//
//
//import com.google.android.gms.tasks.OnFailureListener;
//import com.google.android.gms.tasks.OnSuccessListener;
//import com.google.firebase.storage.FirebaseStorage;
//import com.google.firebase.storage.StorageReference;
//
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Locale;
//
////TODO: make sure Event ID is set up correctly, when storing new events
//
//public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {
//
//    private ArrayList<Event> eventDataList;
//
//    //change1
//    private OnClickListener onClickListener;
//
//    private Context context;
//
//    public EventAdapter(Context context, ArrayList<Event> eventDataList) {
//        this.context = context;
//        this.eventDataList = eventDataList;
//    }
//
//    public static class EventViewHolder extends RecyclerView.ViewHolder {
//        TextView textCategories, textDate, textLocation, textDescription;
//        ImageView imageBanner; // Assuming this is for eventPosterID as a URL or drawable resource.
//
//        public EventViewHolder(View itemView) {
//            super(itemView);
//            textCategories = itemView.findViewById(R.id.text_categories);
//            textDate = itemView.findViewById(R.id.text_date);
//            textLocation = itemView.findViewById(R.id.text_location);
//            textDescription = itemView.findViewById(R.id.text_lorem_ipsum);
//            imageBanner = itemView.findViewById(R.id.image_banner);
//        }
//    }
//
//    @NonNull
//    @Override
//    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event, parent, false);
//        return new EventViewHolder(itemView);
//    }
//
//    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
//        Event event = eventDataList.get(position);
//
//        holder.textCategories.setText(event.getEventName());
//        holder.textDescription.setText(event.getDescription());
//
//        // Set click listener to notify the interface implementer
//        holder.itemView.setOnClickListener(v -> {
//            if (onClickListener != null) {
//                onClickListener.onClick(position, event);
//            }
//        });
//
//        this.displayImage(event.getEventPosterID(), holder.imageBanner);
//        // Set click listener to notify the interface implementer
//        holder.itemView.setOnClickListener(v -> {
//            if (onClickListener != null) {
//                onClickListener.onClick(position, event);
//            }
//        });
//
//        this.displayImage(event.getEventPosterID(), holder.imageBanner);
//
//        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
//        holder.textDate.setText(dateFormat.format(event.getEventDate()));
//        holder.textLocation.setText(event.getLocation()); // Adjust based on your data
//    }
//
//    //change 3
//    public void setOnClickListener(OnClickListener onClickListener) {
//        this.onClickListener = onClickListener;
//    }
//
//    //change 4
//
//    public interface OnClickListener {
//        void onClick(int position, Event model);
//        holder.textLocation.setText(event.getLocation()); // Adjust based on your data
//    }
//
//    //change 3
//    public void setOnClickListener(OnClickListener onClickListener) {
//        this.onClickListener = onClickListener;
//    }
//
//    //change 4
//
//    public interface OnClickListener {
//        void onClick(int position, Event model);
//    }
//
//    @Override
//    public int getItemCount() {
//        return eventDataList.size();
//    }
//
//    private void displayImage(String imageID, ImageView imgView)
//    {
//        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
//        StorageReference photoReference= storageReference.child("images/" + imageID);
//
//        final long ONE_MEGABYTE = 1024 * 1024;
//        photoReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
//            @Override
//            public void onSuccess(byte[] bytes) {
//                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
//                imgView.setImageBitmap(bmp);
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception exception) {
//                Toast.makeText(context.getApplicationContext(), "No Such file or Path found!!", Toast.LENGTH_LONG).show();
//            }
//        });
//    }
//}
//
//    private void displayImage(String imageID, ImageView imgView)
//    {
//        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
//        StorageReference photoReference= storageReference.child("images/" + imageID);
//
//        final long ONE_MEGABYTE = 1024 * 1024;
//        photoReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
//            @Override
//            public void onSuccess(byte[] bytes) {
//                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
//                imgView.setImageBitmap(bmp);
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception exception) {
//                Toast.makeText(context.getApplicationContext(), "No Such file or Path found!!", Toast.LENGTH_LONG).show();
//            }
//        });
//    }
//}

package com.example.evenz;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import java.util.Locale;


/**
 * Adapter class used to integrate arrayList of Event objects with RecyclerView to display it
 * as well as provide interaction fucntionality with the UI such as onclick
 * has field EvenDataList, stores arrayList of Event Objects
 * has OnclickListener, used to implement OnclickListener on the RecylerView.
 */
public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private ArrayList<Event> eventDataList;

    //change1
    private OnClickListener onClickListener;
    private Context context;


    /**
     * Constructor for EventAdapter, initalized with EventList and context
     * @param context context
     * @param eventDataList List of Event Objects.
     */
    public EventAdapter(Context context, ArrayList<Event> eventDataList) {
        this.context = context;
        this.eventDataList = eventDataList;
    }

    /**
     * Class initalizes and links TextView and Image View of the individual cells of RecyclerView to hold
     * Event data,
     */
    public static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView textCategories, textDate, textLocation, textDescription;
        ImageView imageBanner; //this is for image poster

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

    /**
     * Passes Event-object data into the Views of the RecyclerView
     * @param holder The ViewHolder which should be updated to represent the contents of the
     *        item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = eventDataList.get(position);

        holder.textCategories.setText(event.getEventName());
        holder.textDescription.setText(event.getDescription());

        // Set click listener to notify the interface implementer
        holder.itemView.setOnClickListener(v -> {
            if (onClickListener != null) {
                onClickListener.onClick(position, event);
            }
        });

        this.displayImage(event.getEventPosterID(), holder.imageBanner);

        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        holder.textDate.setText(dateFormat.format(event.getEventDate()));
        holder.textLocation.setText(event.getLocation()); //TODO: change this to location
    }


    /**
     * initalizes the OnclickerLsitener
     * @param onClickListener
     */
    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    /**
     * provides interface for establishing the OnclickListener.
     */
    public interface OnClickListener {
        void onClick(int position, Event model);
    }

    @Override
    public int getItemCount() {
        return eventDataList.size();
    }


    /**
     * Fetches image from firebase and displays it in an ImageView
     * @param imageID firebase image ID for fetching
     * @param imgView Inma
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