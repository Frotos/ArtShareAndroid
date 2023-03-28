package com.example.ArtShare.Fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ArtShare.Components.AudioPlayer;
import com.example.ArtShare.Enums.UploadTypes;
import com.example.ArtShare.Adapters.GalleryAdapter;
import com.example.ArtShare.ContentGallery;
import com.example.ArtShare.ImagesGallery;
import com.example.ArtShare.R;

import java.util.List;

public class SelectFromGalleryFragment extends Fragment {
    Context context;

    RecyclerView recyclerView;
    GalleryAdapter galleryAdapter;
    List<String> content;
    ImageView imagePreview;
    VideoView videoPreview;
    AudioPlayer audioPreview;
    ConstraintLayout previews_container;

    Button selectContentButton;
    Button selectAvatarButton;

    UploadTypes type;

    private static final int READ_PERMISSION_CODE = 141;

    public SelectFromGalleryFragment() {}

    public static SelectFromGalleryFragment newInstance(UploadTypes type) {
        Bundle args = new Bundle();
        args.putSerializable("type", type);
        SelectFromGalleryFragment fragment = new SelectFromGalleryFragment();
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
        return inflater.inflate(R.layout.fragment_select_from_gallery, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Activity activity = getActivity();
        context = getContext();

        Bundle args = getArguments();
        if (args != null) {
            type = (UploadTypes) args.getSerializable("type");
        }

        if (activity != null) {
            recyclerView = activity.findViewById(R.id.recyclerview_gallery_items);
            imagePreview = activity.findViewById(R.id.gallery_image_preview);
            videoPreview = activity.findViewById(R.id.gallery_video_preview);
            audioPreview = activity.findViewById(R.id.gallery_audio_preview);
            previews_container = activity.findViewById(R.id.gallery_previews_container);
            selectContentButton = activity.findViewById(R.id.select_content_button);
            selectAvatarButton = activity.findViewById(R.id.select_avatar_button);

            if (context != null) {
                if (ContextCompat.checkSelfPermission(context,
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(activity,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_PERMISSION_CODE);
                } else {
                    switch (type) {
                        case Avatar:
                            loadImages();
                            break;
                        case Content:
                            loadAllMedia();
                            break;
                    }
                }
            }
        }
    }

    private void loadImages() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(context, 4));
        content = ImagesGallery.getImages(context);
        galleryAdapter = new GalleryAdapter(this, content, path -> {
            previews_container.setVisibility(View.VISIBLE);
            imagePreview.setVisibility(View.VISIBLE);
            Glide.with(context).load(path).into(imagePreview);
            showAvatarUploadButton();
        });

        recyclerView.setAdapter(galleryAdapter);
    }

    private void loadAllMedia() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 4));
        content = ContentGallery.getMedia(context);
        galleryAdapter = new GalleryAdapter(this, content, path -> {
            previews_container.setVisibility(View.VISIBLE);
            if (path.endsWith("mp4")) {
                Uri uri = Uri.parse("file://" + path);
                imagePreview.setVisibility(View.GONE);
                videoPreview.setVisibility(View.VISIBLE);
                audioPreview.setVisibility(View.GONE);
                videoPreview.setVideoURI(uri);
                videoPreview.setTag(uri.getPath());
                videoPreview.start();
            } else if (path.endsWith("mp3")) {
                Uri uri = Uri.parse("file://" + path);
                imagePreview.setVisibility(View.GONE);
                videoPreview.setVisibility(View.GONE);
                audioPreview.setVisibility(View.VISIBLE);
                audioPreview.setAudioPath(uri.toString());
            } else {
                imagePreview.setVisibility(View.VISIBLE);
                videoPreview.setVisibility(View.GONE);
                audioPreview.setVisibility(View.GONE);
                videoPreview.stopPlayback();
                Glide.with(context).load(path).into(imagePreview);
            }
//            Glide.with(context).load(path).into(imagePreview);
            showContentUploadButton();
        });

        recyclerView.setAdapter(galleryAdapter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == READ_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadAllMedia();
            }
        }
    }

    public void showContentUploadButton() {
        if (selectContentButton.getVisibility() == View.INVISIBLE) {
            selectContentButton.setVisibility(View.VISIBLE);
        }
    }

    public void showAvatarUploadButton() {
        if (selectAvatarButton.getVisibility() == View.INVISIBLE) {
            selectAvatarButton.setVisibility(View.VISIBLE);
        }
    }
}
