package com.example.evenz;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;

public class ShareQRActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.container_qr_code_generated_organizer);

        Intent intent = getIntent();
        Bitmap bitmap = (Bitmap) intent.getParcelableExtra("BitmapImage");

        ImageView qr = findViewById(R.id.qrImage);
        qr.setImageBitmap(bitmap);

        ImageView back = findViewById(R.id.back_qr_share);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent (ShareQRActivity.this, HomeScreenActivity.class));
            }
        });
    }
}