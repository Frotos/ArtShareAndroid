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

public class ChangeUsernameFragment extends DialogFragment {
    Context context;

    EditText newUsername;
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
            View changeUsernameLayout = inflater.inflate(R.layout.fragment_change_username, null);
            newUsername = changeUsernameLayout.findViewById(R.id.new_username);
            password = changeUsernameLayout.findViewById(R.id.change_username_password);

            return new AlertDialog.Builder(context)
                    .setTitle("Change username")
                    .setView(changeUsernameLayout)
                    .setPositiveButton("Change", (dialog, which) -> new Thread(this::changeUsername).start())
                    .setNegativeButton("Cancel", (dialog, which) -> dialog.cancel())
                    .create();
        }

        return null;
    }

    private void changeUsername() {
        if (newUsername != null && password != null) {
            if (!newUsername.getText().toString().equals("") && !password.getText().toString().equals("")) {
                repository.getUser(context, user -> {
                    user.username = newUsername.getText().toString();
                    repository.updateUser(context, user, repository -> Toast.makeText(context, "Username changed", Toast.LENGTH_LONG).show());
                });
            }
        }
    }
}
