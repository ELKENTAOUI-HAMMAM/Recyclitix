package com.example.smartrecycle.utils;

import android.content.Context;
import android.util.Log;

import com.example.smartrecycle.api.ApiClient;
import com.example.smartrecycle.model.WasteResult;


public class ExampleUsage {
    private static final String TAG = "ExampleUsage";

    
    public static void authenticateUser(Context context, String email, String password) {
        ApiClient.authenticateUser(context, email, password, new ApiClient.OnAuthListener() {
            @Override
            public void onSuccess(com.example.smartrecycle.model.SignInResponse response) {
                Log.d(TAG, "User authenticated successfully: " + response.getFirstName() + " " + response.getLastName());
                
                
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "Authentication failed: " + error);
                
            }
        });
    }

    
    public static void saveWasteResult(Context context) {
        
        WasteResult wasteResult = new WasteResult();
        wasteResult.setWasteIcon("plastic_bottle_icon.png");
        wasteResult.setWasteType("Plastic");
        wasteResult.setWasteCategory("Recyclable");
        wasteResult.setWastePoints(10);
        wasteResult.setTimeAgo("2 minutes ago");
        wasteResult.setObjectDescription("This is a plastic water bottle that can be recycled");
        wasteResult.setInstructions("Rinse the bottle, remove the cap, and place in recycling bin");

        
        ApiClient.saveWasteResult(context, wasteResult, new ApiClient.OnWasteResultListener() {
            @Override
            public void onSuccess(WasteResult savedResult) {
                Log.d(TAG, "Waste result saved with ID: " + savedResult.getId());
                
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "Failed to save waste result: " + error);
                
            }
        });
    }

    
    public static boolean isUserAuthenticated(Context context) {
        SessionManager sessionManager = new SessionManager(context);
        String token = sessionManager.getToken();
        return token != null && !token.isEmpty();
    }

    
    public static void logoutUser(Context context) {
        SessionManager sessionManager = new SessionManager(context);
        sessionManager.logout();
        Log.d(TAG, "User logged out successfully");
    }
} 