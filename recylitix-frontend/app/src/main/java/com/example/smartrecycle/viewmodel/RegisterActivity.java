package com.example.smartrecycle.viewmodel;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smartrecycle.R;
import com.example.smartrecycle.api.ApiService;
import com.example.smartrecycle.api.RetrofitClient;
import com.example.smartrecycle.model.SignUpRequest;
import com.example.smartrecycle.model.SignUpResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Get API service from RetrofitClient
        apiService = RetrofitClient.getApiService();

        EditText firstNameEditText = findViewById(R.id.firstNameEditText);
        EditText lastNameEditText = findViewById(R.id.lastNameEditText);
        EditText emailEditText = findViewById(R.id.emailEditText);
        EditText passwordEditText = findViewById(R.id.passwordEditText);
        Button registerButton = findViewById(R.id.registerButton);
        TextView loginLink = findViewById(R.id.loginLink);

        registerButton.setOnClickListener(v -> {
            String firstName = firstNameEditText.getText().toString().trim();
            String lastName = lastNameEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(RegisterActivity.this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
                return;
            }

            Log.d(TAG, "Tentative d'inscription pour: " + email);

            SignUpRequest signUpRequest = new SignUpRequest(firstName, lastName, email, password);

            apiService.signUp(signUpRequest).enqueue(new Callback<SignUpResponse>() {
                @Override
                public void onResponse(Call<SignUpResponse> call, Response<SignUpResponse> response) {
                    Log.d(TAG, "Réponse reçue - Code: " + response.code());
                    
                    if (response.isSuccessful() && response.body() != null) {
                        Log.d(TAG, "Inscription réussie pour: " + email);
                        Toast.makeText(RegisterActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Log.e(TAG, "Échec d'inscription - Code: " + response.code());
                        try {
                            String errorBody = response.errorBody() != null ? response.errorBody().string() : "Erreur inconnue";
                            Log.e(TAG, "Erreur body: " + errorBody);
                            Toast.makeText(RegisterActivity.this, "Échec de l'inscription: " + response.code(), Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            Toast.makeText(RegisterActivity.this, "Échec de l'inscription", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<SignUpResponse> call, Throwable t) {
                    Log.e(TAG, "Erreur réseau", t);
                    String errorMessage = "Erreur réseau : " + t.getMessage();
                    if (t.getMessage() != null && t.getMessage().contains("CLEARTEXT")) {
                        errorMessage = "Erreur de sécurité réseau. Vérifiez la configuration.";
                    }
                    Toast.makeText(RegisterActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                }
            });

        });

        loginLink.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
        });
    }
}
