package com.example.smartrecycle.viewmodel;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartrecycle.R;
import com.example.smartrecycle.utils.SessionManager;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DURATION = 3000; // 3 secondes

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_simple);

        initializeViews();
        startAnimations();
        navigateToNextScreen();
    }

    private void initializeViews() {
        // Pour le layout simplifié, on n'a pas besoin d'initialiser tous les éléments
        // Les animations seront simplifiées
    }

    private void startAnimations() {
        // Pour le layout simplifié, on n'a pas besoin d'animations complexes
        // L'activité fonctionnera sans animations pour l'instant
    }



    private void navigateToNextScreen() {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            // Vérifier si l'utilisateur est connecté
            SessionManager sessionManager = new SessionManager(this);
            
            Intent intent;
            if (sessionManager.isLoggedIn()) {
                // Utilisateur connecté, aller à l'activité principale
                intent = new Intent(SplashActivity.this, MainContainerActivity.class);
            } else {
                // Utilisateur non connecté, aller à l'écran de connexion
                intent = new Intent(SplashActivity.this, LoginActivity.class);
            }
            
            // Animation de transition
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            startActivity(intent);
            finish();
        }, SPLASH_DURATION);
    }

    @Override
    public void onBackPressed() {
        // Désactiver le bouton retour pendant le splash
        // Ne pas appeler super.onBackPressed() pour empêcher le retour
    }
} 