package com.example.smartrecycle.api;

import android.content.Context;
import android.util.Log;

import com.example.smartrecycle.model.SignInResponse;
import com.example.smartrecycle.model.User;
import com.example.smartrecycle.model.WasteResult;
import com.example.smartrecycle.utils.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ApiClient {
    private static final String TAG = "ApiClient";

    /**
     * Authenticate user and save token
     */
    public static void authenticateUser(Context context, String email, String password, 
                                       OnAuthListener listener) {
        User user = new User();
        user.setEmail(email);
        user.setPassword(password);

        ApiService apiService = RetrofitClient.getApiService();
        Call<SignInResponse> call = apiService.signIn(user);

        call.enqueue(new Callback<SignInResponse>() {
            @Override
            public void onResponse(Call<SignInResponse> call, Response<SignInResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    SignInResponse signInResponse = response.body();
                    
                    // Save token and user info
                    SessionManager sessionManager = new SessionManager(context);
                    sessionManager.saveToken(signInResponse.getToken());
                    sessionManager.saveUserInfo(
                        signInResponse.getFirstName(),
                        signInResponse.getLastName(),
                        signInResponse.getEmail()
                    );
                    
                    Log.d(TAG, "Authentication successful");
                    listener.onSuccess(signInResponse);
                } else {
                    Log.e(TAG, "Authentication failed: " + response.code());
                    listener.onError("Authentication failed: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<SignInResponse> call, Throwable t) {
                Log.e(TAG, "Authentication error", t);
                listener.onError("Network error: " + t.getMessage());
            }
        });
    }

    /**
     * Save waste result with authentication
     */
    public static void saveWasteResult(Context context, WasteResult wasteResult, 
                                      OnWasteResultListener listener) {
        ApiService apiService = RetrofitClient.getAuthenticatedApiService(context);
        Call<WasteResult> call = apiService.saveWasteResult(wasteResult);

        call.enqueue(new Callback<WasteResult>() {
            @Override
            public void onResponse(Call<WasteResult> call, Response<WasteResult> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Waste result saved successfully");
                    listener.onSuccess(response.body());
                } else {
                    Log.e(TAG, "Failed to save waste result: " + response.code());
                    listener.onError("Failed to save waste result: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<WasteResult> call, Throwable t) {
                Log.e(TAG, "Error saving waste result", t);
                listener.onError("Network error: " + t.getMessage());
            }
        });
    }

    public interface OnAuthListener {
        void onSuccess(SignInResponse response);
        void onError(String error);
    }

    public interface OnWasteResultListener {
        void onSuccess(WasteResult wasteResult);
        void onError(String error);
    }
}