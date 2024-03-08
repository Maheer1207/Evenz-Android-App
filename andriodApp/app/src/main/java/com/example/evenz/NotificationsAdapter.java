package com.example.evenz;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;

import java.util.ArrayList;
/**
 * Adapter class for managing the display of notification items in a RecyclerView.
 * This adapter is designed to work with a list of notification messages and bind them to
 * corresponding views in the RecyclerView.
 *
 * <p>The adapter extends RecyclerView.Adapter and is responsible for creating ViewHolder
 * instances, binding data to the views, and determining the total number of items in the list.
 *
 * @see android.support.v7.widget.RecyclerView.Adapter
 * @see android.support.v7.widget.RecyclerView.ViewHolder
 */
public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.ViewHolder> {

    private Context context;
    private ArrayList<String> notifications;
    /**
     * Constructs a new NotificationsAdapter with the given context and a list of notification messages.
     *
     * @param context      The context in which the adapter is created.
     * @param notifications The list of notification messages to be displayed.
     */
    public NotificationsAdapter(Context context, ArrayList<String> notifications) {
        this.context = context;
        this.notifications = notifications;
    }
    /**
     * Called when the RecyclerView needs a new ViewHolder for creating a notification item view.
     *
     * @param parent   The ViewGroup into which the new View will be added.
     * @param viewType The type of the new View, as defined by getItemViewType(int).
     * @return A new ViewHolder that holds a View of the given view type.
     */
    @NonNull
    @Override
    public NotificationsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_notification, parent, false);
        return new ViewHolder(view);
    }
    /**
     * Called to display the data at the specified position. This method updates the contents
     * of the ViewHolder to reflect the notification message at the given position.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull NotificationsAdapter.ViewHolder holder, int position) {
        String notification = notifications.get(position);
        holder.txtNotificationDetails.setText(notification);
        // Since we are using just a String, we won't have a title or timestamp.
        // If those are needed, you'd have to parse the String or change the data structure.
    }
    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in the data set.
     */
    @Override
    public int getItemCount() {
        return notifications.size();
    }
    /**
     * ViewHolder class representing each notification item view in the RecyclerView.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtNotificationDetails;
        /**
         * Constructs a new ViewHolder to hold a notification item view.
         *
         * @param itemView The view representing the notification item.
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNotificationDetails = itemView.findViewById(R.id.txt_notification_details);
            // txtNotificationTitle and txtNotificationTime would be removed if not used.
        }
    }
}