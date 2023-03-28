package com.example.ArtShare.Fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.ArtShare.R;

public class AboutFragment extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Activity activity = getActivity();
        Context context = getContext();

        if (context != null && activity != null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View changeLanguageLayout = inflater.inflate(R.layout.fragment_about, null);

            return new AlertDialog.Builder(context)
                    .setView(changeLanguageLayout)
                    .setNeutralButton("Ok", (dialog, which) -> dialog.cancel())
                    .create();
        }

        return null;
    }
}
