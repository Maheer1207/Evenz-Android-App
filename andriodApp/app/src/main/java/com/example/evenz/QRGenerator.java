package com.example.evenz;

import android.graphics.Bitmap;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
/**
 * QRGenerator is a class responsible for generating QR codes from the given input string.
 * It utilizes the ZXing library to create a QR code encoded with the provided data.
 */
public class QRGenerator {
    /**
     * Generates a QR code as a Bitmap image based on the input string, width, and height.
     *
     * @param basestring The string to be encoded into the QR code.
     * @param width      The width (in pixels) of the resulting QR code image.
     * @param height     The height (in pixels) of the resulting QR code image.
     * @return A Bitmap representing the generated QR code or a fallback Bitmap with minimal size
     *         in case of a WriterException during QR code generation.
     * @see MultiFormatWriter
     * @see BitMatrix
     * @see BarcodeEncoder
     */
    public Bitmap generate(String basestring, int width, int height) {
        
        MultiFormatWriter wrter = new MultiFormatWriter();

        try {
            // Encode the input string into a QR code matrix
            BitMatrix matrix = wrter.encode(basestring, BarcodeFormat.QR_CODE, width, height);
            // Convert the matrix into a Bitmap representation of the QR code
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