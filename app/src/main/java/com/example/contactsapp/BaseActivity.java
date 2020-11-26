package com.example.contactsapp;

import android.content.Intent;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.contactsapp.ui.authentication.AuthenticationActivity;
import com.example.contactsapp.ui.splash.SplashActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.rpc.context.AttributeContext;

public class BaseActivity extends AppCompatActivity implements SessionListener {

    public void setupSession() {
        ((App) getApplication()).registerSessionListener(this);
        ((App) getApplication()).startUserSession();
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();

        ((App) getApplication()).onUserInteracted();
    }

    @Override
    public void onSessionLogOut() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, AuthenticationActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        runOnUiThread(() -> {
            Toast.makeText(getApplicationContext(), "Logged out for inactivity", Toast.LENGTH_LONG).show();
        });


        startActivity(intent);
    }
}
