package com.example.evenz;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;

/**
 * class holds methods for generating images for user profile picture
 */
public class ImageGenerator {

    /**
     * Generates image based on profile name,
     * @param profileName user name
     * @param width width
     * @param height height
     * @return image Canvas object
     */
    public static Bitmap generateProfileImage(String profileName, int width, int height) {
        Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(image);

        // Generate a color based on the hash of the profile name
        int backgroundColor = generateColor(profileName);
        Paint backgroundPaint = new Paint();
        backgroundPaint.setColor(backgroundColor);
        canvas.drawRect(0, 0, width, height, backgroundPaint);

        // Draw the first letter of the profile name
        if (!profileName.isEmpty()) {
            String firstLetter = profileName.substring(0, 1).toUpperCase();
            Paint textPaint = new Paint();
            textPaint.setColor(Color.WHITE); // Set text color
            textPaint.setTextSize((float) width / 2); // Set text size based on the image width
            textPaint.setAntiAlias(true);
            textPaint.setTextAlign(Paint.Align.CENTER);
            textPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

            // Calculate the position of the text
            float xPos = (float) width / 2;
            float yPos = ((float) height / 2) - ((textPaint.descent() + textPaint.ascent()) / 2);

            canvas.drawText(firstLetter, xPos, yPos, textPaint);
        }

        return image;
    }

    private static int generateColor(String input) {
        int hash = input.hashCode();
        int r = (hash & 0xFF0000) >> 16;
        int g = (hash & 0x00FF00) >> 8;
        int b = (hash & 0x0000FF);
        return Color.rgb(r, g, b);
    }
}
