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

public class RegistrationFragment extends Fragment {

    // widgets
    private TextInputLayout tilFirstName;
    private TextInputLayout tilLastName;
    private TextInputLayout tilPhoneNumber;
    private TextInputLayout tilUsername;
    private TextInputLayout tilPassword;
    private Button btnRegister;

    // viewmodel
    private AuthenticationViewModel viewModel;

    public RegistrationFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_registration, container, false);

        // instantiate viewmodel
        viewModel = new ViewModelProvider(this).get(AuthenticationViewModel.class);

        // set spannable string
        SpannableStringUtil.setSpannableStringLoginRegister(
                getFragmentManager(),
                getString(R.string.already_account_log_in),
                25,
                31,
                new LoginFragment(),
                (TextView) view.findViewById(R.id.tvAlreadyAccount)
        );

        // instantiate widget references
        tilFirstName = view.findViewById(R.id.tilFirstName);
        tilLastName = view.findViewById(R.id.tilLastName);
        tilPhoneNumber = view.findViewById(R.id.tilPhoneNumber);
        tilUsername = view.findViewById(R.id.tilUsername);
        tilPassword = view.findViewById(R.id.tilPassword);
        btnRegister = view.findViewById(R.id.btnRegister);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get user data
                String firstName = tilFirstName.getEditText().getText().toString();
                String lastName = tilLastName.getEditText().getText().toString();
                String phoneNumber = tilPhoneNumber.getEditText().getText().toString();
                String username = tilUsername.getEditText().getText().toString();
                String password = tilPassword.getEditText().getText().toString();

                // check for correctness
                boolean isFirstNameCorrect = AuthenticationTools.checkFirstName(tilFirstName);
                boolean isLastNameCorrect = AuthenticationTools.checkLastName(tilLastName);
                boolean isPhoneNumberCorrect = AuthenticationTools.checkPhoneNumber(tilPhoneNumber);
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

                // registration response observer
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

                if (isFirstNameCorrect && isLastNameCorrect &&
                        isPhoneNumberCorrect && isUsernameCorrect &&
                        isPasswordCorrect) {
                    viewModel.register(username, password, firstName, lastName, phoneNumber);
                }

            }
        });
    }
}