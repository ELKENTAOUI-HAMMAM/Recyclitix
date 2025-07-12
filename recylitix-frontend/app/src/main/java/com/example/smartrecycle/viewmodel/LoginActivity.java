package com.example.smartrecycle.viewmodel;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smartrecycle.R;
import com.example.smartrecycle.api.ApiService;
import com.example.smartrecycle.api.RetrofitClient;
import com.example.smartrecycle.model.SignInResponse;
import com.example.smartrecycle.model.User;
import com.example.smartrecycle.utils.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private ApiService apiService;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Get API service from RetrofitClient
        apiService = RetrofitClient.getApiService();
        sessionManager = new SessionManager(this);

        EditText emailEditText = findViewById(R.id.emailEditText);
        EditText passwordEditText = findViewById(R.id.passwordEditText);
        TextView registerLink = findViewById(R.id.registerLink);

        findViewById(R.id.loginButton).setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
                return;
            }

            Log.d(TAG, "Tentative de connexion pour: " + email);

            User user = new User(email, password);

            apiService.signIn(user).enqueue(new Callback<SignInResponse>() {
                @Override
                public void onResponse(Call<SignInResponse> call, Response<SignInResponse> response) {
                    Log.d(TAG, "Réponse reçue - Code: " + response.code());
                    
                    if (response.isSuccessful()) {
                        SignInResponse signInResponse = response.body();
                        if (signInResponse != null) {
                            Log.d(TAG, "Connexion réussie pour: " + signInResponse.getEmail());
                            
                            // Save token in SessionManager if available in response
                            if (signInResponse.getToken() != null) {
                                sessionManager.saveToken(signInResponse.getToken());
                                Log.d(TAG, "Token sauvegardé");
                            }

                            // Save user information
                            String firstName = signInResponse.getFirstName();
                            String lastName = signInResponse.getLastName();
                            String email = signInResponse.getEmail();
                            sessionManager.saveUserInfo(firstName, lastName, email);

                            Toast.makeText(LoginActivity.this, "Connexion réussie, Bienvenue " + firstName, Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this, MainContainerActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Log.e(TAG, "Réponse vide");
                            Toast.makeText(LoginActivity.this, "Réponse vide ou invalide", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e(TAG, "Échec de connexion - Code: " + response.code());
                        try {
                            String errorBody = response.errorBody() != null ? response.errorBody().string() : "Erreur inconnue";
                            Log.e(TAG, "Erreur body: " + errorBody);
                            Toast.makeText(LoginActivity.this, "Échec de connexion: " + response.code(), Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            Toast.makeText(LoginActivity.this, "Échec de connexion", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<SignInResponse> call, Throwable t) {
                    Log.e(TAG, "Erreur réseau", t);
                    String errorMessage = "Erreur réseau : " + t.getMessage();
                    if (t.getMessage() != null && t.getMessage().contains("CLEARTEXT")) {
                        errorMessage = "Erreur de sécurité réseau. Vérifiez la configuration.";
                    }
                    Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                }
            });
        });

        registerLink.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }
}