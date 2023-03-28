package com.example.ArtShare.Fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.ArtShare.Activities.MyAccountActivity;
import com.example.ArtShare.R;
import com.example.ArtShare.Utils;

import java.util.Locale;

public class ChangeLanguageFragment extends DialogFragment {
    RadioGroup languages;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Activity activity = getActivity();
        Context context = getContext();

        if (context != null && activity != null) {
            LayoutInflater inflater = getLayoutInflater();
            View changeLanguageLayout = inflater.inflate(R.layout.fragment_change_language, null);
            languages = changeLanguageLayout.findViewById(R.id.languages_group);

            setCurrentLocale();

            return new AlertDialog.Builder(context)
                    .setTitle("Change language")
                    .setView(changeLanguageLayout)
                    .setPositiveButton("Change", (dialog, which) -> setLocale())
                    .setNegativeButton("Cancel", (dialog, which) -> dialog.cancel())
                    .create();
        }

        return null;
    }

    private void setCurrentLocale() {
        Locale currentLocale = getResources().getConfiguration().locale;
        switch (currentLocale.getLanguage()) {
            case "en":
                ((RadioButton)languages.getChildAt(0)).setChecked(true);
                break;
            case "pl":
                ((RadioButton)languages.getChildAt(1)).setChecked(true);
                break;
            case "uk":
                ((RadioButton)languages.getChildAt(2)).setChecked(true);
                break;
        }
    }

    private void setLocale() {
        Activity activity = getActivity();
        Context context = getContext();

        if (activity != null && context != null) {
            String selectedLanguage = "";
            if (((RadioButton)languages.findViewById(R.id.en)).isChecked()) {
                selectedLanguage = "en";
            } else if (((RadioButton)languages.findViewById(R.id.pl)).isChecked()) {
                selectedLanguage = "pl";
            } else if (((RadioButton)languages.findViewById(R.id.uk)).isChecked()) {
                selectedLanguage = "uk";
            }

            Locale newLocale = new Locale(selectedLanguage);
            Resources resources = getResources();
            DisplayMetrics displayMetrics = resources.getDisplayMetrics();
            Configuration configuration = resources.getConfiguration();
            configuration.locale = newLocale;
            resources.updateConfiguration(configuration, displayMetrics);
            Intent refresh = new Intent(context, MyAccountActivity.class);
            Utils.saveLocale(context, selectedLanguage);
            activity.finish();
            startActivity(refresh);
        }
    }
}
