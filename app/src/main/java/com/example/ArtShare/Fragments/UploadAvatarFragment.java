package com.example.ArtShare.Fragments;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ArtShare.R;

import java.io.ByteArrayOutputStream;

public class UploadAvatarFragment extends Fragment {

    private static final String ARG_PREVIEW = "preview";

    public UploadAvatarFragment() {}

    public static UploadAvatarFragment newInstance(Bitmap preview) {
        UploadAvatarFragment fragment = new UploadAvatarFragment();
        Bundle args = new Bundle();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        preview.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        args.putString(ARG_PREVIEW, Base64.encodeToString(stream.toByteArray(), Base64.DEFAULT));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_upload_avatar, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Activity activity = getActivity();

        if (activity != null) {
            ImageView avatarPreview = activity.findViewById(R.id.send_avatar_preview);
            Bundle args = getArguments();
            if (args != null) {
                byte[] bytes = Base64.decode(args.getString(ARG_PREVIEW), Base64.DEFAULT);
                avatarPreview.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
            }
        }
    }
}
