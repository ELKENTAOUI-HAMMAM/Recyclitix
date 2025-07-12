package com.example.smartrecycle.utils;

import android.content.Context;
import android.util.Log;

import com.example.smartrecycle.api.ApiClient;
import com.example.smartrecycle.model.WasteResult;

/**
 * Example usage of the API client for authentication and waste result management
 * This is a utility class showing how to use the API
 */
public class ExampleUsage {
    private static final String TAG = "ExampleUsage";

    /**
     * Example of how to authenticate a user
     */
    public static void authenticateUser(Context context, String email, String password) {
        ApiClient.authenticateUser(context, email, password, new ApiClient.OnAuthListener() {
            @Override
            public void onSuccess(com.example.smartrecycle.model.SignInResponse response) {
                Log.d(TAG, "User authenticated successfully: " + response.getFirstName() + " " + response.getLastName());
                // User is now authenticated and token is saved
                // You can now make authenticated API calls
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "Authentication failed: " + error);
                // Handle authentication error
            }
        });
    }

    /**
     * Example of how to save a waste result with all required fields
     */
    public static void saveWasteResult(Context context) {
        // Create a new WasteResult with all the required fields
        WasteResult wasteResult = new WasteResult();
        wasteResult.setWasteIcon("plastic_bottle_icon.png");
        wasteResult.setWasteType("Plastic");
        wasteResult.setWasteCategory("Recyclable");
        wasteResult.setWastePoints(10);
        wasteResult.setTimeAgo("2 minutes ago");
        wasteResult.setObjectDescription("This is a plastic water bottle that can be recycled");
        wasteResult.setInstructions("Rinse the bottle, remove the cap, and place in recycling bin");

        // Save the waste result
        ApiClient.saveWasteResult(context, wasteResult, new ApiClient.OnWasteResultListener() {
            @Override
            public void onSuccess(WasteResult savedResult) {
                Log.d(TAG, "Waste result saved with ID: " + savedResult.getId());
                // Waste result has been successfully saved to the database
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "Failed to save waste result: " + error);
                // Handle save error
            }
        });
    }

    /**
     * Example of how to check if user is authenticated
     */
    public static boolean isUserAuthenticated(Context context) {
        SessionManager sessionManager = new SessionManager(context);
        String token = sessionManager.getToken();
        return token != null && !token.isEmpty();
    }

    /**
     * Example of how to logout user
     */
    public static void logoutUser(Context context) {
        SessionManager sessionManager = new SessionManager(context);
        sessionManager.logout();
        Log.d(TAG, "User logged out successfully");
    }
} 