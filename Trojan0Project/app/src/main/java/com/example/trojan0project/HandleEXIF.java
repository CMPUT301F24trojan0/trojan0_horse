package com.example.trojan0project;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Purpose:
 * ensures images are displayed properly on different devices.
 *
 * Design Rationale:
 * decodes the image and reads its EXIF metadata. Based on this data, the image is rotated properly.
 *
 * Outstanding Issues:
 * - No Issues.
 */

// code from https://stackoverflow.com/questions/26460997/how-to-strip-exif-data-from-android-camera-image

public class HandleEXIF {
    /**
     * Adjusts the orientation of an image based on EXIF data.
     *
     * @param context The context of the calling component.
     * @param uri The URI of the image to process.
     * @return A Bitmap with corrected orientation, or null if unable to decode.
     * @throws FileNotFoundException if the image file is not found.
     * @throws IOException if an I/O error occurs.
     */
    public static Bitmap handleEXIF(Context context, Uri uri) throws FileNotFoundException, IOException {
        InputStream input = context.getContentResolver().openInputStream(uri);

        // Decode image bounds only
        BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
        onlyBoundsOptions.inJustDecodeBounds = true;
        onlyBoundsOptions.inDither = true;
        onlyBoundsOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;
        BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
        input.close();

        if (onlyBoundsOptions.outWidth == -1 || onlyBoundsOptions.outHeight == -1) {
            return null;
        }

        // Decode the actual image
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inJustDecodeBounds = false;
        bitmapOptions.inDither = true;
        bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;
        input = context.getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
        input.close();

        // Use ExifInterface to check orientation
        input = context.getContentResolver().openInputStream(uri);
        ExifInterface ei = new ExifInterface(input);
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                bitmap = rotateImage(bitmap, 90);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                bitmap = rotateImage(bitmap, 180);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                bitmap = rotateImage(bitmap, 270);
                break;
        }

        input.close();
        return bitmap;
    }
    /**
     * Rotates an image by a specified angle.
     *
     * @param source The original bitmap to rotate.
     * @param angle The angle of rotation in degrees.
     * @return A rotated bitmap.
     */
    private static Bitmap rotateImage(Bitmap source, float angle) {
        Bitmap bitmap = null;
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        try {
            bitmap = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
        return bitmap;
    }
}

