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

public class ChangePasswordFragment extends DialogFragment {
    Context context;

    EditText newPassword;
    EditText newPasswordConfirm;
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
            View changePasswordLayout = inflater.inflate(R.layout.fragment_change_password, null);
            newPassword = changePasswordLayout.findViewById(R.id.new_password);
            newPasswordConfirm = changePasswordLayout.findViewById(R.id.new_password_confirm);
            password = changePasswordLayout.findViewById(R.id.change_password_password);

            return new AlertDialog.Builder(context)
                    .setTitle("Change password")
                    .setView(changePasswordLayout)
                    .setPositiveButton("Change", (dialog, which) -> new Thread(this::changePassword).start())
                    .setNegativeButton("Cancel", (dialog, which) -> dialog.cancel())
                    .create();
        }

        return null;
    }

    private void changePassword() {
        if (newPassword != null && password != null) {
            if (!newPassword.getText().toString().equals("") && !password.getText().toString().equals("")) {
                if (newPassword.getText().toString().equals(newPasswordConfirm.getText().toString())) {
                    repository.getUser(context, user -> {
                        user.password = newPassword.getText().toString();
                        repository.updateUser(context, user, response -> Toast.makeText(context, "Password changed", Toast.LENGTH_SHORT).show());
                    });
                }
            }
        }
    }
}
