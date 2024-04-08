package com.example.evenz;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * an adapter to present a list of notifications for an event
 */
public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.ViewHolder> {

    private final Context context;
    private final ArrayList<String> notifications;

    public NotificationsAdapter(Context context, ArrayList<String> notifications) {
        this.context = context;
        this.notifications = notifications;
    }

    // Other methods remain unchanged

    // Use a static inner class or a separate class for AsyncTask to avoid memory leaks
    private static class FetchImageTask extends AsyncTask<Void, Void, Bitmap> {
        private WeakReference<ImageView> imageViewReference;
        private String url;

        public FetchImageTask(ImageView imageView, String url) {
            this.imageViewReference = new WeakReference<>(imageView);
            this.url = url;
        }

        @Override
        protected Bitmap doInBackground(Void... voids) {
            try {
                URL url = new URL(this.url);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                return BitmapFactory.decodeStream(input);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            ImageView imageView = imageViewReference.get();
            if (imageView != null && bitmap != null) {
                imageView.setImageBitmap(bitmap);
            }
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationsAdapter.ViewHolder holder, int position) {
        String notification = notifications.get(position);

        // Splitting notification into type and details
        String[] notificationParts = notification.split(",", 2);
        if (notificationParts.length == 2) {
            // Set text for title and details
            holder.txtNotificationTitle.setText(notificationParts[0].trim());
            holder.txtNotificationDetails.setText(notificationParts[1].trim());

            // Set image based on the notification type
            int imageResId = getImageResourceId(notificationParts[0].trim());
            holder.notificationImageView.setImageResource(imageResId);
        } else {
            //If the notification format doesn't match expected, set entire string as details
            holder.txtNotificationDetails.setText(notification); // other if default
            holder.notificationImageView.setImageResource(R.drawable.other_noti);
        }
    }


    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtNotificationTitle, txtNotificationDetails;
        ImageView notificationImageView; // Add this line

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNotificationTitle = itemView.findViewById(R.id.txt_notification_title);
            txtNotificationDetails = itemView.findViewById(R.id.txt_notification_details);
            notificationImageView = itemView.findViewById(R.id.img_notification_icon); // Add this line
        }
    }
    private int getImageResourceId(String notificationType) {
        switch (notificationType) {
            case "Food":
                return R.drawable.food_noti;
            case "Recreation":
                return R.drawable.recreation_noti;
            case "Announcement":
                return R.drawable.annoucement_noti;
            case "Milestones":
                return R.drawable.milestones_noti;
            case "Mystery":
                return R.drawable.myster_noti;
            case "Networking & Socials":
                return R.drawable.network_noti;
            case "Presentation":
                return R.drawable.presentation_noti;
            case "Urgent":
                return R.drawable.urgent_noti;
            case "Workshop":
                return R.drawable.workshop_noti;
            default:
                return R.drawable.other_noti; //ensure other notification is set
        }
    }

}