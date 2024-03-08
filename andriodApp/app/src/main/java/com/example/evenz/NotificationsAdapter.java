package com.example.evenz;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.ViewHolder> {

    private Context context;
    private String[] notifications;

    public NotificationsAdapter(Context context, String[] notifications) {
        this.context = context;
        this.notifications = notifications;
    }

    @NonNull
    @Override
    public NotificationsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationsAdapter.ViewHolder holder, int position) {
        String notification = notifications[position];
        holder.txtNotificationDetails.setText(notification);
        // Since we are using just a String, we won't have a title or timestamp.
        // If those are needed, you'd have to parse the String or change the data structure.
    }

    @Override
    public int getItemCount() {
        return notifications.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtNotificationDetails;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNotificationDetails = itemView.findViewById(R.id.txt_notification_details);
            // txtNotificationTitle and txtNotificationTime would be removed if not used.
        }
    }
}