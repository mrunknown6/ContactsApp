package com.example.contactsapp.ui.splash;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.contactsapp.R;
import com.example.contactsapp.ui.authentication.AuthenticationActivity;
import com.example.contactsapp.ui.dashboard.DashboardActivity;
import com.google.firebase.auth.FirebaseAuth;

public class SplashActivity extends AppCompatActivity {

    private Handler handler = new Handler();
    private Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        init();
    }

    private void init() {
        runnable = this::isLoggedIn;
    }

    private void isLoggedIn() {
        Intent intent;
        if (FirebaseAuth.getInstance().getCurrentUser() == null)
            intent = new Intent(this, AuthenticationActivity.class);
        else
            intent = new Intent(this, DashboardActivity.class);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();

        handler.postDelayed(runnable, 1000);
    }

    @Override
    protected void onStop() {
        super.onStop();

        handler.removeCallbacks(runnable);
    }
}