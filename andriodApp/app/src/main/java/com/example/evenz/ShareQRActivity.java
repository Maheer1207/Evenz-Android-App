package com.example.evenz;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;

public class ShareQRActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.container_qr_code_generated_organizer);

        ImageView qr = findViewById(R.id.qrImage);

        Uri bitmapUri = Uri.parse(getIntent().getStringExtra("BitmapImage"));
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), bitmapUri);
            qr.setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ImageView back = findViewById(R.id.back_qr_share);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent (ShareQRActivity.this, HomeScreenActivity.class));
            }
        });
    }
}