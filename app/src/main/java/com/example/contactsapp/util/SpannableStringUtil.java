package com.example.contactsapp.util;

import android.graphics.Color;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.contactsapp.R;

public class SpannableStringUtil {

    public static void setSpannableStringLoginRegister(
            final FragmentManager fragmentManager,
            String text,
            int startIndex,
            int endIndex,
            final Fragment fragment,
            TextView textView) {
        SpannableString spannableString = new SpannableString(text);

        // initialize spans
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                if (fragmentManager != null) {
                    fragmentManager.beginTransaction().replace(R.id.flAuthenticationContainer, fragment, null).commit();
                }
            }
        };
        ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(Color.parseColor("#ff0099cc"));
        StyleSpan styleSpan = new StyleSpan(Typeface.BOLD);

        // set spans
        spannableString.setSpan(clickableSpan, startIndex, endIndex, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(foregroundColorSpan, startIndex, endIndex, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(styleSpan, startIndex, endIndex, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);

        // set text
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setText(spannableString);
    }
}
