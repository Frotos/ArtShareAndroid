package com.example.ArtShare.Fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.ArtShare.R;
import com.example.ArtShare.Repositories.UserRepository;

public class ChangeEmailFragment extends DialogFragment {
    Context context;

    EditText newEmail;
    EditText password;

    private UserRepository repository;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        context = getContext();
        Activity activity = getActivity();
        repository = new UserRepository();

        if (context != null && activity != null) {
            LayoutInflater inflater = getLayoutInflater();
            View changeEmailLayout = inflater.inflate(R.layout.fragment_change_email, null);
            newEmail = changeEmailLayout.findViewById(R.id.new_email);
            password = changeEmailLayout.findViewById(R.id.change_email_password);

            return new AlertDialog.Builder(context)
                    .setTitle("Change e-mail")
                    .setView(changeEmailLayout)
                    .setPositiveButton("Change", (dialog, which) -> new Thread(this::changeEmail).start())
                    .setNegativeButton("Cancel", (dialog, which) -> dialog.cancel())
                    .create();
        }

        return null;
    }

    private void changeEmail() {
        if (newEmail != null && password != null) {
            if (!newEmail.getText().toString().equals("") && !password.getText().toString().equals("")) {
                repository.getUser(context, user -> {
                    user.email = newEmail.getText().toString();
                    repository.updateUser(context, user, response -> Toast.makeText(context, "Email changed", Toast.LENGTH_SHORT).show());
                });
            }
        }
    }


}
