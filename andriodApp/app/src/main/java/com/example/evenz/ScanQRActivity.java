
package com.example.evenz;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class ScanQRActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    TextView txv;
    Button y;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);

        //testing my method
        txv = findViewById(R.id.textView);
        y = findViewById(R.id.scner);
        y.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator intent =  new IntentIntegrator(ScanQRActivity.this);
                intent.setOrientationLocked(true);
                intent.setPrompt("Scan a QR code");
                intent.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
                intent.initiateScan();
            }
        });

        QRGenerator test = new QRGenerator();
        Bitmap finalx = test.generate("testing", 400, 400);
        ImageView qrimg = findViewById(R.id.QRcode);
        qrimg.setImageBitmap(finalx);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if(intentResult != null) {

            String contents = intentResult.getContents();
            if (contents != null) {
                txv.setText(intentResult.getContents());
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}