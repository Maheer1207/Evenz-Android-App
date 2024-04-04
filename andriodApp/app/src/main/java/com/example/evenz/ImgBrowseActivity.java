package com.example.evenz;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ImgBrowseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_browse_images);

        RecyclerView imgRecyclerView = findViewById(R.id.imgsRecyclerView);

        // Specify the number of columns in the grid
        int numberOfColumns = 3; // For example, 3 columns

        // Set GridLayoutManager as the layout manager for imgRecyclerView
        imgRecyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));

//        imgRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Fetch all images asynchronously and then set up the RecyclerView
        ImageUtility.fetchAllImg(new ImageFetchListener() {
            @Override
            public void onImagePathsFetched(ArrayList<String> imgPathList) {
                // This will be called once the image paths are fetched
                runOnUiThread(() -> {
                    ImageAdapter imgAdapter = new ImageAdapter(ImgBrowseActivity.this, imgPathList);
                    imgRecyclerView.setAdapter(imgAdapter);
                });
            }
        });

        findViewById(R.id.back_admin_browse_images).setOnClickListener(v -> finish());
    }
}


