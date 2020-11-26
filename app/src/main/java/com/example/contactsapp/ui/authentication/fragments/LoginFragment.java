package com.example.contactsapp.ui.authentication.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.contactsapp.R;
import com.example.contactsapp.ui.authentication.Resource;
import com.example.contactsapp.tools.AuthenticationTools;
import com.example.contactsapp.ui.authentication.viewmodel.AuthenticationViewModel;
import com.example.contactsapp.ui.dashboard.DashboardActivity;
import com.example.contactsapp.util.SpannableStringUtil;
import com.google.android.material.textfield.TextInputLayout;

public class LoginFragment extends Fragment {

    // widgets
    private TextInputLayout tilUsername;
    private TextInputLayout tilPassword;
    private Button btnLogin;

    // viewmodel
    private AuthenticationViewModel viewModel;

    public LoginFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        // instantiate viewmodel
        viewModel = new ViewModelProvider(this).get(AuthenticationViewModel.class);

        // set spannable string
        SpannableStringUtil.setSpannableStringLoginRegister(
                getFragmentManager(),
                getString(R.string.no_account_register),
                23,
                31,
                new RegistrationFragment(),
                (TextView) view.findViewById(R.id.tvNoAccount)
        );

        // instantiate widget references
        tilUsername = view.findViewById(R.id.tilUsername);
        tilPassword = view.findViewById(R.id.tilPassword);
        btnLogin = view.findViewById(R.id.btnLogin);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get username and password
                String username = tilUsername.getEditText().getText().toString();
                String password = tilPassword.getEditText().getText().toString();

                // check for correctness
                boolean isUsernameCorrect = AuthenticationTools.checkUsername(tilUsername);
                boolean isPasswordCorrect = AuthenticationTools.checkPassword(tilPassword);

                // animate progress bar observer
                viewModel.getIsAuthenticating().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
                    @Override
                    public void onChanged(Boolean isAuthenticating) {

                        if (getActivity() != null) {
                            ProgressBar pbLoading = getActivity().findViewById(R.id.pbLoading);

                            if (isAuthenticating) {
                                pbLoading.setVisibility(View.VISIBLE);
                            } else {
                                pbLoading.setVisibility(View.GONE);
                            }
                        } else {
                            Toast.makeText(getContext(), "ACTIVITY IS NULL", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

                // log in response observer
                viewModel.getResponse().observe(getViewLifecycleOwner(), new Observer<Resource<String>>() {
                    @Override
                    public void onChanged(Resource<String> response) {
                        if (response instanceof Resource.Unsuccessful) {
                            Toast.makeText(getContext(), ((Resource.Unsuccessful<String>) response).getData(), Toast.LENGTH_SHORT).show();
                        } else if (response instanceof Resource.Successful) {
                            Intent intent = new Intent(getActivity(), DashboardActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            getActivity().startActivity(intent);
                        }
                    }
                });

                if (isUsernameCorrect && isPasswordCorrect) {
                    viewModel.logIn(username, password);
                }

            }
        });
    }
}