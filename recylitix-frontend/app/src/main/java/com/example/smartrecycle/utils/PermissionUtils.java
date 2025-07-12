package com.example.smartrecycle.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

public class PermissionUtils {

    /**
     * Check if camera permission is granted
     */
    public static boolean isCameraPermissionGranted(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
               == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Check if location permission is granted
     */
    public static boolean isLocationPermissionGranted(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
               == PackageManager.PERMISSION_GRANTED ||
               ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
               == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Check if storage permission is granted
     */
    public static boolean isStoragePermissionGranted(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
               == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Request camera permission
     */
    public static void requestCameraPermission(Activity activity) {
        ActivityCompat.requestPermissions(activity,
                new String[]{Manifest.permission.CAMERA},
                Constants.REQUEST_CAMERA_PERMISSION);
    }

    /**
     * Request camera permission from fragment
     */
    public static void requestCameraPermission(Fragment fragment) {
        fragment.requestPermissions(
                new String[]{Manifest.permission.CAMERA},
                Constants.REQUEST_CAMERA_PERMISSION);
    }

    /**
     * Request location permission
     */
    public static void requestLocationPermission(Activity activity) {
        ActivityCompat.requestPermissions(activity,
                new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                },
                Constants.REQUEST_LOCATION_PERMISSION);
    }

    /**
     * Request location permission from fragment
     */
    public static void requestLocationPermission(Fragment fragment) {
        fragment.requestPermissions(
                new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                },
                Constants.REQUEST_LOCATION_PERMISSION);
    }

    /**
     * Request storage permission
     */
    public static void requestStoragePermission(Activity activity) {
        ActivityCompat.requestPermissions(activity,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                Constants.REQUEST_PICK_IMAGE);
    }

    /**
     * Check if permission should show rationale
     */
    public static boolean shouldShowRequestPermissionRationale(Activity activity, String permission) {
        return ActivityCompat.shouldShowRequestPermissionRationale(activity, permission);
    }

    /**
     * Check if all required permissions are granted
     */
    public static boolean areAllPermissionsGranted(Context context) {
        return isCameraPermissionGranted(context) &&
               isLocationPermissionGranted(context) &&
               isStoragePermissionGranted(context);
    }

    /**
     * Get missing permissions
     */
    public static String[] getMissingPermissions(Context context) {
        java.util.List<String> missingPermissions = new java.util.ArrayList<>();

        if (!isCameraPermissionGranted(context)) {
            missingPermissions.add(Manifest.permission.CAMERA);
        }

        if (!isLocationPermissionGranted(context)) {
            missingPermissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        if (!isStoragePermissionGranted(context)) {
            missingPermissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }

        return missingPermissions.toArray(new String[0]);
    }
}