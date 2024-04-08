package com.example.evenz;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

/**
 * activity for the admin to browse and delete images
 */
public class ImageBrowseActivity extends AppCompatActivity {

    private RecyclerView imgRecyclerView;
    private ImageAdapter imgAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_browse_images);
        imgRecyclerView = findViewById(R.id.imgsRecyclerView);
        imgRecyclerView.setLayoutManager(new GridLayoutManager(this, 3)); // Assuming 3 columns in the grid
        setupRecyclerView();
        findViewById(R.id.admin_event_list).setOnClickListener( v -> startActivity(new Intent(ImageBrowseActivity.this, AdminBrowseEventActivity.class)));
        findViewById(R.id.profile_attendee).setOnClickListener( v -> startActivity(new Intent(ImageBrowseActivity.this, AdminBrowseProfilesActivity.class)));
        findViewById(R.id.back_admin_browse_images).setOnClickListener(v -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupRecyclerView(); // Refresh data when coming back to this activity
    }

    private void setupRecyclerView() {
        // Fetch all images asynchronously and then set up the RecyclerView
        ImageUtility.fetchAllImg(new ImageFetchListener() {
            @Override
            public void onImagePathsFetched(ArrayList<String> imgPathList) {
                runOnUiThread(() -> {
                    if (imgAdapter == null) {
                        imgAdapter = new ImageAdapter(ImageBrowseActivity.this, imgPathList);
                        imgRecyclerView.setAdapter(imgAdapter);
                    } else {
                        imgAdapter.updateData(imgPathList); // Update existing adapter data
                    }
                    imgAdapter.setOnClickListener(new ImageAdapter.OnClickListener() {
                        @Override
                        public void onClick(int position) {
                            String imagePath = imgPathList.get(position);

                            Intent intent = new Intent(ImageBrowseActivity.this, ImageDeleteActivity.class);
                            intent.putExtra("imagePath", imagePath);
                            startActivity(intent);
                        }
                    });
                });
            }
        });
    }
}