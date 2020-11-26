package com.example.contactsapp.ui.authentication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.contactsapp.R;
import com.example.contactsapp.ui.authentication.fragments.LoginFragment;

public class AuthenticationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        getSupportFragmentManager().beginTransaction().replace(R.id.flAuthenticationContainer, new LoginFragment(), null).commit();
    }

}