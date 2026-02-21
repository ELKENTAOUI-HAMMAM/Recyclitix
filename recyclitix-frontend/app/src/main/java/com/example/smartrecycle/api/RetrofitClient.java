package com.example.smartrecycle.api;

import android.content.Context;
import android.util.Log;

import com.example.smartrecycle.utils.SessionManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static final String TAG = "RetrofitClient";
    
    private static final String[] POSSIBLE_BASE_URLS = {
        "http://192.168.0.141:8080/",
        "http://10.0.2.2:8080/",  
        "http://localhost:8080/",
        "http://127.0.0.1:8080/"
    };
    
    private static String BASE_URL = POSSIBLE_BASE_URLS[0]; 
    private static Retrofit retrofit = null;
    private static Retrofit authenticatedRetrofit = null;
    private static SessionManager sessionManager;

    
    public static void setBaseUrl(String baseUrl) {
        BASE_URL = baseUrl;
        
        retrofit = null;
        authenticatedRetrofit = null;
        Log.d(TAG, "Base URL changée vers: " + BASE_URL);
    }

    
    public static String getBaseUrl() {
        return BASE_URL;
    }

    
    public static Retrofit getClient() {
        if (retrofit == null) {
            
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            
            OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .addInterceptor(loggingInterceptor);

            OkHttpClient client = clientBuilder.build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            
            Log.d(TAG, "Retrofit client created with base URL: " + BASE_URL);
        }
        return retrofit;
    }

    
    public static Retrofit getAuthenticatedClient(Context context) {
        if (sessionManager == null) {
            sessionManager = new SessionManager(context);
        }

        if (authenticatedRetrofit == null) {
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();

            
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .addInterceptor(loggingInterceptor);

            clientBuilder.addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request original = chain.request();

                    String token = sessionManager.getToken();
                    Log.d(TAG, "Adding token to request: " + (token != null ? "Bearer " + token.substring(0, Math.min(20, token.length())) + "..." : "null"));

                    Request request = original.newBuilder()
                            .header("Authorization", "Bearer " + token)
                            .method(original.method(), original.body())
                            .build();

                    return chain.proceed(request);
                }
            });

            OkHttpClient client = clientBuilder.build();

            authenticatedRetrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
            
            Log.d(TAG, "Authenticated Retrofit client created");
        }

        return authenticatedRetrofit;
    }

    
    public static ApiService getApiService() {
        return getClient().create(ApiService.class);
    }

    
    public static ApiService getAuthenticatedApiService(Context context) {
        return getAuthenticatedClient(context).create(ApiService.class);
    }
}