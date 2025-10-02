package com.example.smartrecycle.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ImageUtils {

    /**
     * Resize bitmap to specified dimensions
     */
    public static Bitmap resizeBitmap(Bitmap bitmap, int maxWidth, int maxHeight) {
        if (bitmap == null) return null;

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        float scaleWidth = ((float) maxWidth) / width;
        float scaleHeight = ((float) maxHeight) / height;
        float scale = Math.min(scaleWidth, scaleHeight);

        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);

        return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false);
    }

    /**
     * Get bitmap from URI
     */
    public static Bitmap getBitmapFromUri(Context context, Uri uri) {
        try {
            return MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Save bitmap to internal storage
     */
    public static String saveBitmapToInternalStorage(Context context, Bitmap bitmap, String directory) {
        try {
            File dir = new File(context.getFilesDir(), directory);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            String timeStamp = new SimpleDateFormat(Constants.DATE_FORMAT_FILE, Locale.getDefault()).format(new Date());
            String fileName = Constants.IMAGE_PREFIX + timeStamp + "." + Constants.IMAGE_FORMAT.toLowerCase();
            File file = new File(dir, fileName);

            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(
                    Constants.IMAGE_FORMAT.equals("JPEG") ? Bitmap.CompressFormat.JPEG : Bitmap.CompressFormat.PNG,
                    Constants.IMAGE_QUALITY,
                    fos
            );
            fos.close();

            return file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Load bitmap from file path
     */
    public static Bitmap loadBitmapFromPath(String path) {
        if (path == null || path.isEmpty()) return null;

        File file = new File(path);
        if (!file.exists()) return null;

        return BitmapFactory.decodeFile(path);
    }

    /**
     * Correct image orientation based on EXIF data
     */
    public static Bitmap correctImageOrientation(Bitmap bitmap, String imagePath) {
        try {
            ExifInterface exif = new ExifInterface(imagePath);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            Matrix matrix = new Matrix();
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    matrix.postRotate(90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    matrix.postRotate(180);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    matrix.postRotate(270);
                    break;
                case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                    matrix.postScale(-1, 1);
                    break;
                case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                    matrix.postScale(1, -1);
                    break;
                default:
                    return bitmap;
            }

            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        } catch (IOException e) {
            e.printStackTrace();
            return bitmap;
        }
    }

    /**
     * Compress bitmap to reduce file size
     */
    public static Bitmap compressBitmap(Bitmap bitmap, int quality) {
        if (bitmap == null) return null;

        try {
            File tempFile = File.createTempFile("temp", ".jpg");
            FileOutputStream fos = new FileOutputStream(tempFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, fos);
            fos.close();

            Bitmap compressedBitmap = BitmapFactory.decodeFile(tempFile.getAbsolutePath());
            tempFile.delete();

            return compressedBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return bitmap;
        }
    }

    /**
     * Create circular bitmap
     */
    public static Bitmap createCircularBitmap(Bitmap bitmap) {
        if (bitmap == null) return null;

        int size = Math.min(bitmap.getWidth(), bitmap.getHeight());
        Bitmap output = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);

        android.graphics.Canvas canvas = new android.graphics.Canvas(output);
        android.graphics.Paint paint = new android.graphics.Paint();
        android.graphics.Rect rect = new android.graphics.Rect(0, 0, size, size);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawCircle(size / 2f, size / 2f, size / 2f, paint);

        paint.setXfermode(new android.graphics.PorterDuffXfermode(android.graphics.PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    /**
     * Calculate sample size for efficient loading
     */
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    /**
     * Load bitmap efficiently with sample size
     */
    public static Bitmap loadBitmapEfficiently(String path, int reqWidth, int reqHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(path, options);
    }

    /**
     * Convert bitmap to byte array
     */
    public static byte[] bitmapToByteArray(Bitmap bitmap) {
        if (bitmap == null) return null;

        try {
            java.io.ByteArrayOutputStream stream = new java.io.ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, Constants.IMAGE_QUALITY, stream);
            return stream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Convert byte array to bitmap
     */
    public static Bitmap byteArrayToBitmap(byte[] byteArray) {
        if (byteArray == null || byteArray.length == 0) return null;

        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
    }

    /**
     * Delete image file
     */
    public static boolean deleteImageFile(String path) {
        if (path == null || path.isEmpty()) return false;

        File file = new File(path);
        return file.exists() && file.delete();
    }

    /**
     * Get file size in MB
     */
    public static double getFileSizeMB(String path) {
        if (path == null || path.isEmpty()) return 0;

        File file = new File(path);
        if (!file.exists()) return 0;

        return file.length() / (1024.0 * 1024.0);
    }
}

