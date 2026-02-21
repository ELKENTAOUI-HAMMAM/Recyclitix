package com.example.smartrecycle.viewmodel;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartrecycle.R;
import com.example.smartrecycle.utils.SessionManager;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DURATION = 3000; 

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_simple);

        initializeViews();
        startAnimations();
        navigateToNextScreen();
    }

    private void initializeViews() {
        
        
    }

    private void startAnimations() {
        
        
    }



    private void navigateToNextScreen() {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            
            SessionManager sessionManager = new SessionManager(this);
            
            Intent intent;
            if (sessionManager.isLoggedIn()) {
                
                intent = new Intent(SplashActivity.this, MainContainerActivity.class);
            } else {
                
                intent = new Intent(SplashActivity.this, LoginActivity.class);
            }
            
            
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            startActivity(intent);
            finish();
        }, SPLASH_DURATION);
    }

    @Override
    public void onBackPressed() {
        
        
    }
} 