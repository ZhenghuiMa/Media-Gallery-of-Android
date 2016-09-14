package com.derekma.videogallery;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;

/**
 * Created by derekma on 16/1/31.
 * A class is to convert byte[] to bitmap and bitmap to byte[].
 */
public class DbBitmapUtility {

    /**
     * A public static method.
     * Convert from bitmap to byte array.
     */
    public static byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }

    /**
     * A public static method.
     * Convert from byte array to bitmap.
     */
    public static Bitmap getImage(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }
}