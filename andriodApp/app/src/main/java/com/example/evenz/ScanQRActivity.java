package com.example.evenz;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;


/**
 * activity for scanning QR code.
 */
public class ScanQRActivity extends AppCompatActivity {

    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;

    private static final int REQUEST_CAMERA_PERMISSION = 100;
    private Camera camera;
    private boolean isProcessing = false;//so we can have only one

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
        flashlightButton.setOnClickListener(v -> toggleFlashlight());
    }

    private void toggleFlashlight() {
        isFlashlightOn = !isFlashlightOn;
        camera.getCameraControl().enableTorch(isFlashlightOn);
    }

    private void scanBarcodes(InputImage image, ImageProxy imageProxy) {
        if (isProcessing) {
            imageProxy.close();
            return;
        }
        BarcodeScannerOptions options = new BarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                .build();
        BarcodeScanner scanner = BarcodeScanning.getClient(options);

        scanner.process(image)
                .addOnSuccessListener(barcodes -> {
                    for (Barcode barcode : barcodes) {
                        String rawValue = barcode.getRawValue();
                        isProcessing = true;
                        runOnUiThread(() -> handleQRCode(rawValue));
                        break; //process first barcode only
                    }
                    imageProxy.close(); // Ensure to close the ImageProxy
                })
                .addOnFailureListener(e -> imageProxy.close());// Ensure to close the ImageProxy on failure as well
    }

    /**
     * Takes the QR code string, determines if the qr code is
     * to check in or to view the poster of an event
     * does actions accordingly
     * @param qrCode qrcode gotten
     */
    private void handleQRCode(String qrCode) {
        if (qrCode != null) {
            String[] parts = qrCode.split("/");
            String lastPart = parts[parts.length -1];
            if (lastPart.equals("check_in")) {
                // Check in the user to the event
                Intent intent = new Intent(ScanQRActivity.this, HomeScreenActivity.class);
                Bundle b =new Bundle();
                b.putString("role", "attendee");
                b.putString("eventID", parts[parts.length -2]);
                intent.putExtras(b);

                FirebaseUserManager firebaseUserManager = new FirebaseUserManager();
                String deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

                // Get the user's current location
                LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                Log.d("LocationDebug", "Before checking permission");
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    Log.d("LocationDebug", "Permission granted");
                    Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (location != null) {
                        Log.d("LocationDebug", "Location obtained");
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();

                        // Add the locations to the event
                        EventUtility.addLocationsToEvent(parts[parts.length -2], latitude,longitude );
                    } else {
                        Log.d("LocationDebug", "Location is null");
                    }
                } else {
                    Log.d("LocationDebug", "Permission not granted");
                }
                firebaseUserManager.addEventToUser(deviceId, parts[parts.length -2])//this is for adding user to the events signed up for
                        .addOnSuccessListener(aVoid -> Log.d("checkInUser", "User successfully checked in!"))
                        .addOnFailureListener(e -> Log.w("checkInUser", "Error checking user in", e));

                firebaseUserManager.checkInUser(deviceId, parts[parts.length -2]) // this is for putting the event in checked in
                        .addOnSuccessListener(aVoid -> Log.d("checkInUser", "User successfully checked in!"))
                        .addOnFailureListener(e -> Log.w("checkInUser", "Error checking user in", e));


                EventUtility.userCheckIn(deviceId, parts[parts.length -2]);

                startActivity(intent);
                isProcessing = false;
            }
         else if (lastPart.equals("sign_up")) {
                // Navigate to the event details for the attendee to sign up
                Intent intent = new Intent(ScanQRActivity.this, EventDetailsActivity.class);
                intent.putExtra("eventID", parts[parts.length -2]);
                intent.putExtra("source", "browse");
                intent.putExtra("role", "attendee");
                startActivity(intent);
            }
        }
    }
}