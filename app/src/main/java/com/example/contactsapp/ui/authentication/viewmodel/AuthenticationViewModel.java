package com.example.contactsapp.ui.authentication.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.contactsapp.ui.authentication.Resource;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AuthenticationViewModel extends ViewModel {

    private MutableLiveData<String> _username = new MutableLiveData<>();

    public void setUsername(String username) {
        _username.postValue(username);
    }

    public LiveData<String> getUsername() {
        return _username;
    }

    private MutableLiveData<String> _password = new MutableLiveData<>();

    public void setPassword(String password) {
        _password.postValue(password);
    }

    public LiveData<String> getPassword() {
        return _password;
    }

    private MutableLiveData<Boolean> _isAuthenticating = new MutableLiveData<>(false);

    public LiveData<Boolean> getIsAuthenticating() {
        return _isAuthenticating;
    }

    private MutableLiveData<Resource<String>> _response = new MutableLiveData<>();

    public LiveData<Resource<String>> getResponse() {
        return _response;
    }

    public void logIn(String username, String password) {
        _isAuthenticating.postValue(true);

        // firebase log in
        FirebaseAuth.getInstance().signInWithEmailAndPassword(username.concat("@contactsapp.com"), password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        _isAuthenticating.postValue(false);

                        if (task.isSuccessful()) {
                            _response.postValue(new Resource.Successful<String>("Log in successful"));
                        } else {
                            String errorMessage = task.getException().getMessage();
                            if (errorMessage != null)
                                _response.postValue(new Resource.Unsuccessful<String>(errorMessage));
                            else
                                _response.postValue(new Resource.Unsuccessful<String>("An unexpected error occurred"));
                        }
                    }
                });
    }

    public void register(final String username, String password, final String firstName, final String lastName, final String phoneNumber) {
        _isAuthenticating.postValue(true);

        // firebase registration
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(username.concat("@contactsapp.com"), password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        _isAuthenticating.postValue(false);

                        if (task.isSuccessful()) {
                            // add additional info to db
                            Map<String, String> userData = new HashMap<>();
                            userData.put("username", username);
                            userData.put("first_name", firstName);
                            userData.put("last_name", lastName);
                            userData.put("phone_number", phoneNumber);

                            FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).set(userData).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    _response.postValue(new Resource.Successful<String>("Registration successful"));
                                }
                            });

                        } else {
                            String errorMessage = task.getException().getMessage();
                            if (errorMessage != null)
                                _response.postValue(new Resource.Unsuccessful<String>(errorMessage));
                            else
                                _response.postValue(new Resource.Unsuccessful<String>("An unexpected error occurred"));
                        }

                    }
                });
    }
}
