package com.example.evenz;

import static android.app.Activity.RESULT_OK;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;

import java.util.ArrayList;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Utility class for Image-related helper functions.
 * constructor initalizes firebase to default
 */
public final class ImageUtility {

    FirebaseStorage storage;
    StorageReference storageReference;

    public ImageUtility(){
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
    }
    public interface UploadCallback {
        void onSuccess(String imageID, String imageURL) throws ParseException;
        void onFailure(Exception e);
    }

    /**
     * Given that filePath has already been defined within the activity, uploads an image previously selected by the user
     * This includes adding the image to the firebase storage with proper notifications for upload progress.
     * @param filePath Uri filepath of
     * @return returns generated id for image uploaded
     */


    /**
     * Helper function to upload image
     * @param filePath file path of the image.
     * @param callback callback.
     */
    public void upload(Uri filePath, UploadCallback callback) {
        if (filePath != null) {
            String id = UUID.randomUUID().toString();
            StorageReference ref = storageReference.child("images/" + id);
            ref.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String imageURL = uri.toString();
                            if (callback != null) {
                                try {
                                    callback.onSuccess(id, imageURL);
                                } catch (ParseException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    if (callback != null) {
                        callback.onFailure(e);
                    }
                }
            });
        } else {
            if (callback != null) {
                callback.onFailure(new Exception("File path is null"));
            }
        }
    }


    /**
     * Displays an image from the firebase storage given
     * @param imageID id of the image being displayed
     * @param imgView The image view to place the image on
     */
    public static void displayImage(String imageID, ImageView imgView)
    {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference photoReference = storageReference.child("images/" + imageID);

        final long ONE_MEGABYTE = 1024 * 1024;
        photoReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                imgView.setImageBitmap(bmp);
            }
        });
    }

    public static void fetchAllImg(ImageFetchListener listener) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference imagesRef = storageReference.child("images/");

        imagesRef.listAll()
                .addOnSuccessListener(listResult -> {
                    ArrayList<String> imgPathList = new ArrayList<>();
                    for (StorageReference item : listResult.getItems()) {
                        imgPathList.add(item.getPath());
                    }
                    // Invoke the callback with the fetched list
                    listener.onImagePathsFetched(imgPathList);
                })
                .addOnFailureListener(e -> {
                    // Handle any errors here
                    // For simplicity, we're just printing the stack trace
                    e.printStackTrace();
                });
    }

    public static void deleteImage(String imageID)
    {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference photoReference = storageReference.child("images/" + imageID);

        photoReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // Image deletion succeeded
                Log.d("Delete Image", "Image successfully deleted");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Image deletion failed
                Log.d("Delete Image", "Image deletion failed", exception);
            }
        });
    }

    public String decodeQRCode(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);

        RGBLuminanceSource source = new RGBLuminanceSource(width, height, pixels);
        BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(source));

        Map<DecodeHintType, Object> hints = new HashMap<>();
        hints.put(DecodeHintType.PURE_BARCODE, Boolean.TRUE);

        Reader reader = new QRCodeReader();
        try {
            Result result = reader.decode(binaryBitmap, hints);

            String qr = result.getText();
            String[] qrParts = qr.split("/");

            return qrParts[0];
        } catch (Exception e) {
            e.printStackTrace();
            return null; // Return null on failure
        }
    }

}