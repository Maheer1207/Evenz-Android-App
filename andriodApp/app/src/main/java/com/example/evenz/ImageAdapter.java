package com.example.evenz;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {

    private ArrayList<String> imgPathList; // List of image paths
    private ImageUtility imageUtility; // ImageUtility instance for image loading
    private Context context; // Context for inflating views

    // Constructor
    public ImageAdapter(Context context, ArrayList<String> imgPathList) {
        this.context = context;
        this.imgPathList = imgPathList;
        this.imageUtility = new ImageUtility(); // Initialize ImageUtility
    }

    // ViewHolder class for recycling views
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.browser_image); // Assuming imageView ID in your layout
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item layout
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_photo, parent, false); // Assuming image_item.xml as your item layout
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Get the image path for the current position
        String imagePath = imgPathList.get(position);
        String[] parts = imagePath.split("/");

        String imageID = parts[parts.length - 1];

        // Use ImageUtility to display the image in the ViewHolder's ImageView
        imageUtility.displayImage(imageID, holder.imageView);
    }

    @Override
    public int getItemCount() {
        // Return the size of imgPathList
        return imgPathList.size();
    }
}
