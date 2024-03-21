package com.example.evenz;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;

import java.util.ArrayList;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.ViewHolder> {

    private Context context;
    private ArrayList<String> notifications;

    public NotificationsAdapter(Context context, ArrayList<String> notifications) {
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
        String notification = notifications.get(position);


        String[] notificationParts = notification.split(",",2); // will be matched 1 times.
        if (notificationParts.length == 1) {
            holder.txtNotificationDetails.setText(notification);
        } else {
            holder.txtNotificationTitle.setText(notificationParts[0]);
            holder.txtNotificationDetails.setText(notificationParts[1]);
        }
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtNotificationDetails, txtNotificationTitle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNotificationTitle = itemView.findViewById(R.id.txt_notification_title);
            txtNotificationDetails = itemView.findViewById(R.id.txt_notification_details);
        }
    }
}