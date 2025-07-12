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
    // URLs possibles pour le backend
    private static final String[] POSSIBLE_BASE_URLS = {
        "http://192.168.100.136:8080/",
        "http://10.0.2.2:8080/",  // Pour l'émulateur Android
        "http://localhost:8080/",
        "http://127.0.0.1:8080/"
    };
    
    private static String BASE_URL = POSSIBLE_BASE_URLS[0]; // URL par défaut
    private static Retrofit retrofit = null;
    private static Retrofit authenticatedRetrofit = null;
    private static SessionManager sessionManager;

    // Méthode pour changer l'URL du backend
    public static void setBaseUrl(String baseUrl) {
        BASE_URL = baseUrl;
        // Réinitialiser les instances pour forcer la recréation
        retrofit = null;
        authenticatedRetrofit = null;
        Log.d(TAG, "Base URL changée vers: " + BASE_URL);
    }

    // Méthode pour obtenir l'URL actuelle
    public static String getBaseUrl() {
        return BASE_URL;
    }

    // Get basic Retrofit instance without authentication
    public static Retrofit getClient() {
        if (retrofit == null) {
            // Créer un interceptor pour les logs
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            // Configuration du client OkHttp
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

    // Get authenticated Retrofit instance with token
    public static Retrofit getAuthenticatedClient(Context context) {
        if (sessionManager == null) {
            sessionManager = new SessionManager(context);
        }

        if (authenticatedRetrofit == null) {
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();

            // Créer un interceptor pour les logs
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

    // Get API service without authentication (for login/register)
    public static ApiService getApiService() {
        return getClient().create(ApiService.class);
    }

    // Get authenticated API service (for protected endpoints)
    public static ApiService getAuthenticatedApiService(Context context) {
        return getAuthenticatedClient(context).create(ApiService.class);
    }
}