package com.example.ArtShare.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ArtShare.R;

public class SettingsFragment extends Fragment {

    public SettingsFragment() {
    }

    public static SettingsFragment newInstance() {

        Bundle args = new Bundle();

        SettingsFragment fragment = new SettingsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button changeEmailButton = requireActivity().findViewById(R.id.change_email_button);
        changeEmailButton.setOnClickListener(v -> {
            ChangeEmailFragment changeEmailFragment = new ChangeEmailFragment();
            changeEmailFragment.show(getParentFragmentManager(), "ChangeEmail");
        });

        Button changeUsernameButton = requireActivity().findViewById(R.id.change_username_button);
        changeUsernameButton.setOnClickListener(v -> {
            ChangeUsernameFragment changeUsernameFragment = new ChangeUsernameFragment();
            changeUsernameFragment.show(getParentFragmentManager(), "ChangeUsername");
        });

        Button changePasswordButton = requireActivity().findViewById(R.id.change_password_button);
        changePasswordButton.setOnClickListener(v -> {
            ChangePasswordFragment changePasswordFragment = new ChangePasswordFragment();
            changePasswordFragment.show(getParentFragmentManager(), "ChangePassword");
        });

        Button changeLanguageButton = requireActivity().findViewById(R.id.change_language_button);
        changeLanguageButton.setOnClickListener(v -> {
            ChangeLanguageFragment changeLanguageFragment = new ChangeLanguageFragment();
            changeLanguageFragment.show(getParentFragmentManager(), "ChangeLanguage");
        });

        Button aboutButton = requireActivity().findViewById(R.id.show_about_button);
        aboutButton.setOnClickListener(v -> {
            AboutFragment aboutFragment = new AboutFragment();
            aboutFragment.show(getParentFragmentManager(), "About");
        });
    }
}
