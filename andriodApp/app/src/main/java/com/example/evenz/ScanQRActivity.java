package com.example.evenz;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;

import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class ScanQRActivity extends AppCompatActivity {

    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;

    private static final int REQUEST_CAMERA_PERMISSION = 100; //TODO: fix later
    private Camera camera;

    private boolean isFlashlightOn = false;//For flashlight QR scaning


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qr_code_scan); // Ensure this layout has a PreviewView with the ID `viewFinder`

        // Request camera permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        }

        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindPreview(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                // Handle any errors (including cancellation)
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    @OptIn(markerClass = ExperimentalGetImage.class)
    private void bindPreview(@NonNull ProcessCameraProvider cameraProvider) {
        Preview preview = new Preview.Builder().build();
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();
        PreviewView viewFinder = findViewById(R.id.viewFinder);
        preview.setSurfaceProvider(viewFinder.getSurfaceProvider());
        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build();

        imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(this), imageProxy -> {
            @NonNull InputImage image = InputImage.fromMediaImage(Objects.requireNonNull(imageProxy.getImage()), imageProxy.getImageInfo().getRotationDegrees());
            scanBarcodes(image, imageProxy);
        });

        cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis);
        camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis);

        // Get the ImageView and set an OnClickListener on it
        ImageView flashlightButton = findViewById(R.id.img_rectangle);
        flashlightButton.setOnClickListener(v -> toggleFlashlight()); //TODO: check if flashlight is working
    }

    private void toggleFlashlight() {
        isFlashlightOn = !isFlashlightOn;
        camera.getCameraControl().enableTorch(isFlashlightOn);
    }

    private void scanBarcodes(InputImage image, ImageProxy imageProxy) {
        BarcodeScannerOptions options = new BarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                .build();
        BarcodeScanner scanner = BarcodeScanning.getClient(options);

        scanner.process(image)
                .addOnSuccessListener(barcodes -> {
                    for (Barcode barcode : barcodes) {
                        String rawValue = barcode.getRawValue();
                        runOnUiThread(() -> handleQRCode(rawValue));
                        break; //process first barcode only
                    }
                    imageProxy.close(); // Ensure to close the ImageProxy
                })
                .addOnFailureListener(e -> imageProxy.close());// Ensure to close the ImageProxy on failure as well
    }

    private void handleQRCode(String qrCode) {
        if (qrCode != null) {
            String[] parts = qrCode.split("/");
            String lastPart = parts[parts.length -1];

            if (lastPart.equals("check_in")) {
                // Check in the user to the event
                Intent intent = new Intent(ScanQRActivity.this, HomeScreenActivity.class);
                Bundle b =new Bundle();
                b.putString("role", "attendee");
                b.putString("eventID", qrCode);
                intent.putExtras(b);

                FirebaseUserManager firebaseUserManager = new FirebaseUserManager();
                String deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

                firebaseUserManager.checkInUser(deviceId, qrCode)
                        .addOnSuccessListener(aVoid -> Log.d("checkInUser", "User successfully checked in!"))
                        .addOnFailureListener(e -> Log.w("checkInUser", "Error checking user in", e));
                EventUtility.userCheckIn(deviceId, qrCode);

                startActivity(intent);
            } else if (lastPart.equals("sign_up")) {
                // Navigate to the event details for the attendee to sign up
                Intent intent = new Intent(ScanQRActivity.this, EventDetailsActivity.class);
                intent.putExtra("eventID", qrCode);
                intent.putExtra("source", "browse");
                intent.putExtra("role", "attendee");
                startActivity(intent);
            }
        }
    }
}