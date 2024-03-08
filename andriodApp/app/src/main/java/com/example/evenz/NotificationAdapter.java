package com.example.evenz;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.Locale;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {
    private ArrayList<String> notificationList;
    private Context context;

    public NotificationAdapter(Context context, ArrayList<String> notificationList) {
        this.context = context;
        this.notificationList = notificationList;
    }

    public static class NotificationViewHolder extends RecyclerView.ViewHolder {
        String notification;
        public NotificationViewHolder(View notificationView) {
            super(notificationView);
//            notification = notificationView.findViewById(R.id.)
        }

    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View notificationView = LayoutInflater.from(parent.getContext()).inflate(R.layout., parent, false);
//        return new NotificationAdapter(notificationView);
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        String notification = notificationList.get(position);
//        holder

    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }
}