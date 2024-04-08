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

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class AdminAttendeeAdapter extends RecyclerView.Adapter<AdminAttendeeAdapter.ViewHolder> {

    private List<User> userList;
    private OnClickListener onClickListener;

    public AdminAttendeeAdapter(List<User> userList) {
        this.userList = userList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, contact;
        ImageView imageBanner; //this is for image poster

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.text_categories);
            contact = itemView.findViewById(R.id.text_contact_info);
            imageBanner = itemView.findViewById(R.id.image_banner);
        }
    }

    @NonNull
    @Override
    public AdminAttendeeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_attendee_admin, parent, false);
        return new AdminAttendeeAdapter.ViewHolder(itemView);
    }

    public void onBindViewHolder(@NonNull AdminAttendeeAdapter.ViewHolder holder, int position) {
        User user = userList.get(position);

        holder.name.setText(user.getName());
        holder.contact.setText(user.getPhone()+"\n"+user.getEmail());

        // Set click listener to notify the interface implementer
        holder.itemView.setOnClickListener(v -> {
            if (onClickListener != null) {
                onClickListener.onClick(position, user);
            }
        });

        this.displayImage(user.getProfilePicID(), holder.imageBanner);
    }

    //change 3
    public void setOnClickListener(AdminAttendeeAdapter.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    //change 4

    public interface OnClickListener {
        void onClick(int position, User model);
    }

    @Override
    public int getItemCount() {
        return userList.size();
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
        });
    }
}