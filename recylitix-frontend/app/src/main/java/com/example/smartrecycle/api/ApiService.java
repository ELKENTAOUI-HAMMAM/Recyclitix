package com.example.smartrecycle.api;

import com.example.smartrecycle.model.ImageRequest;
import com.example.smartrecycle.model.SignInResponse;
import com.example.smartrecycle.model.SignUpRequest;
import com.example.smartrecycle.model.SignUpResponse;
import com.example.smartrecycle.model.User;
import com.example.smartrecycle.model.WasteResult;
import com.example.smartrecycle.model.RecyclingPoint;
import com.example.smartrecycle.model.ImageUploadResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import okhttp3.MultipartBody;
import retrofit2.http.Multipart;
import retrofit2.http.Part;

public interface ApiService {

    @POST("/api/auth/signin")
    Call<SignInResponse> signIn(@Body User user);

    @POST("/api/auth/signup")
    Call<SignUpResponse> signUp(@Body SignUpRequest request);
    
    @POST("/api/waste/scan")
    Call<WasteResult> scanWaste(@Body ImageRequest request);
    
    @POST("/api/waste/save")
    Call<WasteResult> saveWasteResult(@Body WasteResult wasteResult);
    
    @GET("/api/waste/history")
    Call<List<WasteResult>> getWasteHistory();

    @GET("/api/recycling-points")
    Call<List<RecyclingPoint>> getRecyclingPoints(
            @Query("lat") double lat,
            @Query("lng") double lng,
            @Query("radius") int radius
    );

    @GET("/api/user/profile")
    Call<User> getUserProfile();

    @Multipart
    @POST("/api/waste/upload-image")
    Call<ImageUploadResponse> uploadImage(@Part MultipartBody.Part file);
}