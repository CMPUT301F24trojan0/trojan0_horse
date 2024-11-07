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

// code from https://stackoverflow.com/questions/26460997/how-to-strip-exif-data-from-android-camera-image

public class HandleEXIF {
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
