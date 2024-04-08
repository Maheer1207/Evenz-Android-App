package com.example.evenz;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.List;

/**
 * Adapter class utilized to adapt arraylists of Attendee-objects to be displayed in
 * recyclerView in the android UI.
 * extends RecyclerView.Adapter.
 * contains field storing array of Attendee-objects
 * Has constructors that can either initalize it with the to-be-adapted List of User-objects,
 * or the list, and an EventID.
 */
class AttendeeAdapter extends RecyclerView.Adapter<AttendeeAdapter.ViewHolder> {

    private final List<User> userList;

    private AttendeeAdapter.OnClickListener onClickListener;
    private String eventID;

    public AttendeeAdapter(List<User> userList) {
        this.userList = userList;
        logUsers();
    }

    public AttendeeAdapter(List<User> userList, String eventID) {
        this.userList = userList;
        this.eventID = eventID;
        logUsers();
    }

    public void logUsers() {
        for (User user : userList) {
            Log.d("UserAdapter", "Name: " + user.getName() + ", Email: " + user.getEmail());
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_attendee, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Updates the contents of the item in the adapter's dataset.
     * @param holder The ViewHolder which should be updated to represent the contents of the
     *        item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        User user = userList.get(position);
        holder.nameTextView.setText(user.getName());
        holder.itemView.setOnClickListener(v -> {
            if (onClickListener != null) {
                onClickListener.onClick(position, user);
            }
        });
        holder.contactInfoTextView.setText(String.format("%s\n%s", user.getPhone(), user.getEmail()));
        Task<Integer> userAttendCount = EventUtility.userAttendCount(eventID, user.getUserId());
        userAttendCount.addOnSuccessListener(new OnSuccessListener<Integer>() {
            @Override
            public void onSuccess(Integer integer) {
                holder.attendCount.setText(integer.toString());
            }
        });
        this.displayImage(user.getProfilePicID(), holder.profileImageView);
    }
    //change 3
    public void setOnClickListener(AttendeeAdapter.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    //change 4

    /**
     * provides interface for establishing the OnclickListener.
     */
    public interface OnClickListener {
        void onClick(int position, User model);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    /**
     * Viewholder class, extends RecyclerView.viewholder. Stores fields containing the Views within each
     * cell of the reclyerView,
     * that the data from the User Class will be displayed in. One constructor
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView profileImageView;
        TextView nameTextView;
        TextView attendCount;
        TextView contactInfoTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            profileImageView = itemView.findViewById(R.id.image_banner);
            nameTextView = itemView.findViewById(R.id.text_categories);
            contactInfoTextView = itemView.findViewById(R.id.text_contact_info);
            attendCount = itemView.findViewById(R.id.attendCount);
        }
    }

    /**
     * Function that calls ImageUtility class to display an image in a specified ImageView specified by parameters:
     * @param id the ID of the image in Firestore to be displayed.
     * @param view the ImageView in which the image is going to be displayed
     */

    public void displayImage(String id, ImageView view)
    {
        ImageUtility.displayImage(id, view);
    }
}