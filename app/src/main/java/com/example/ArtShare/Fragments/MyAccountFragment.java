package com.example.ArtShare.Fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ArtShare.Enums.UploadTypes;
import com.example.ArtShare.R;
import com.example.ArtShare.Repositories.UserRepository;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyAccountFragment extends Fragment {

    private UserRepository repository;

    public MyAccountFragment() {}

    public static MyAccountFragment newInstance() {
        return new MyAccountFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        repository = new UserRepository();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Context context = getContext();
        FragmentActivity activity = getActivity();

        if (activity != null) {
            final CircleImageView avatar = activity.findViewById(R.id.my_account_avatar);
            final TextView username = activity.findViewById(R.id.my_account_username);
            final TextView followersCount = activity.findViewById(R.id.my_account_followers_count);
            final TextView followingsCount = activity.findViewById(R.id.my_account_followings_count);
            final TextView postsCount = activity.findViewById(R.id.my_account_posts_count);

            ImageView postedImages = activity.findViewById(R.id.my_account_posted_images);
            postedImages.setOnClickListener(v -> activity.getSupportFragmentManager().beginTransaction().replace(R.id.my_account_container, new PostedContentFragment()).commit());

            avatar.setOnClickListener(v -> activity.getSupportFragmentManager().beginTransaction().replace(R.id.my_account_container, SelectFromGalleryFragment.newInstance(UploadTypes.Avatar)).commit());

            repository.getUser(context, user -> {
                if (user.avatar != null) {
                    byte[] bytes = Base64.decode(user.avatar.image, Base64.DEFAULT);
                    Bitmap bitmapImage = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    if (bitmapImage != null) {
                        avatar.setImageBitmap(bitmapImage);
                    }
                }

                username.setText(user.username);
                followersCount.setText(String.valueOf(user.followersCount));
                followingsCount.setText(String.valueOf(user.followingsCount));
                postsCount.setText(String.valueOf(user.postsCount));
            });
        }
    }
}