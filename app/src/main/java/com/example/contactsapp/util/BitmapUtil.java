package com.example.contactsapp.util;

import android.graphics.Bitmap;
import android.graphics.Matrix;

public class BitmapUtil {

    public static float WIDTH_480 = 480f;
    public static float HEIGHT_640 = 640f;

    public static Bitmap getResizedBitmap(Bitmap bm) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = WIDTH_480 / width;
        float scaleHeight = WIDTH_480 / height;

        Matrix matrix = new Matrix();

        matrix.postScale(scaleWidth, scaleHeight);


        return Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
    }
}
