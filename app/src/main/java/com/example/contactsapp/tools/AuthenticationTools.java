package com.example.contactsapp.tools;

import com.google.android.material.textfield.TextInputLayout;

public class AuthenticationTools {

    public static boolean checkUsername(TextInputLayout tilUsername) {
        if (tilUsername.getEditText().getText().toString().length() < 6) {
            tilUsername.setError("Minimum size is 6");
            return false;
        } else {
            tilUsername.setError(null);
            return true;
        }
    }

    public static boolean checkPassword(TextInputLayout tilPassword) {
        if (tilPassword.getEditText().getText().toString().length() < 8) {
            tilPassword.setError("Minimum size is 8");
            return false;
        } else {
            tilPassword.setError(null);
            return true;
        }
    }

    public static boolean checkFirstName(TextInputLayout tilFirstName) {
        if (tilFirstName.getEditText().getText().toString().length() == 0) {
            tilFirstName.setError("Cannot be empty");
            return false;
        } else {
            tilFirstName.setError(null);
            return true;
        }
    }

    public static boolean checkLastName(TextInputLayout tilLastName) {
        if (tilLastName.getEditText().getText().toString().length() == 0) {
            tilLastName.setError("Cannot be empty");
            return false;
        } else {
            tilLastName.setError(null);
            return true;
        }
    }

    public static boolean checkPhoneNumber(TextInputLayout tilPhoneNumber) {
        if (tilPhoneNumber.getEditText().getText().toString().length() == 0) {
            tilPhoneNumber.setError("Cannot be empty");
            return false;
        } else {
            tilPhoneNumber.setError(null);
            return true;
        }
    }

}
