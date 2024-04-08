package com.example.evenz;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

/**
 * an activity that allows a admin to delete an image
 */
public class ImageDeleteActivity extends AppCompatActivity {

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_delete_image);

        ImageView imageView =  findViewById(R.id.browser_image);

        String imagePath = getIntent().getStringExtra("imagePath");
        String[] parts = imagePath.split("/");

        String imageID = parts[parts.length - 1];

        // Use ImageUtility to display the image in the ImageView
        ImageUtility.displayImage(imageID, imageView);

        RelativeLayout delete_img = findViewById(R.id.delete_image_button);
        delete_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageUtility.deleteImage(imageID);
                startActivity(new Intent(ImageDeleteActivity.this, ImageBrowseActivity.class));
            }
        });

        findViewById(R.id.back_admin_delete_image).setOnClickListener(v -> finish());
    }
}