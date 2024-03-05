package com.example.evenz;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //testing my method
        /*


        QRCodeGenerator1N test = new QRCodeGenerator1N();
        Bitmap finalx = test.generate("testing", 400, 400);
        ImageView qrimg = findViewById(R.id.QRcode);
        qrimg.setImageBitmap(finalx);

         */
    }
}