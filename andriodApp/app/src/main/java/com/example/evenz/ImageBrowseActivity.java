package com.example.evenz;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

/**
 * Java class for admin_browse_images, allowing the administrator to browse the set of images
 * serving as the posters for all events or user profile pictures in the system in a RecyclerView.
 */
public class ImageBrowseActivity extends AppCompatActivity {

    private RecyclerView imgRecyclerView;
    private ImageAdapter imgAdapter;

    /**
     * links to XML file "admin_browse_images. and the recyclerview
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_browse_images);
        imgRecyclerView = findViewById(R.id.imgsRecyclerView);
        imgRecyclerView.setLayoutManager(new GridLayoutManager(this, 3)); // Assuming 3 columns in the grid
        setupRecyclerView();
        findViewById(R.id.back_admin_browse_images).setOnClickListener(v -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupRecyclerView(); // Refresh data when coming back to this activity
    }

    /**
     * Fetch all images asynchronously and then set up the RecyclerView
     */
    private void setupRecyclerView() {
        //
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