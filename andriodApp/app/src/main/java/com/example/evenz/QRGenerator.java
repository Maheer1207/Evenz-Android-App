package com.example.evenz;

import android.graphics.Bitmap;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

/**
 * Class with code for Generating a random QR code.
 */
public class QRGenerator {

    /**
     * Generates a bitmap with given parameters, returns as a bitmap. basestring is constructed
     * from event ID and user.
     * @param eventID
     * @param additionalInfo
     * @param width
     * @param height
     * @return bitmap of QR code
     */
    public Bitmap generate(String eventID, String additionalInfo, int width, int height) {
        String basestring = eventID + additionalInfo;

        MultiFormatWriter wrter = new MultiFormatWriter();

        try {
            BitMatrix matrix = wrter.encode(basestring, BarcodeFormat.QR_CODE, width, height);

            BarcodeEncoder encoder1 = new BarcodeEncoder();
            Bitmap finalbitmap;
            finalbitmap = encoder1.createBitmap(matrix);
            return finalbitmap;

        } catch (WriterException e) {
            e.printStackTrace();

            //throwaway Bitmap
            return Bitmap.createBitmap(4, 4, Bitmap.Config.ARGB_8888);
        }
    }
}