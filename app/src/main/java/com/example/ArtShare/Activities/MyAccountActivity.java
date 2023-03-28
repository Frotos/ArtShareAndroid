package com.example.ArtShare.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.ArtShare.Components.AudioPlayer;
import com.example.ArtShare.Enums.UploadOperationsEnum;
import com.example.ArtShare.Enums.UploadTypes;
import com.example.ArtShare.Fragments.MyAccountFragment;
import com.example.ArtShare.Fragments.SelectFromGalleryFragment;
import com.example.ArtShare.Fragments.SettingsFragment;
import com.example.ArtShare.Fragments.UploadAvatarFragment;
import com.example.ArtShare.Fragments.UploadContentFragment;
import com.example.ArtShare.Models.Avatar;
import com.example.ArtShare.Models.ContentTypesEnum;
import com.example.ArtShare.Models.UploadContentModel;
import com.example.ArtShare.R;
import com.example.ArtShare.Repositories.AuthRepository;
import com.example.ArtShare.Repositories.UserRepository;
import com.example.ArtShare.Utils;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.ByteArrayOutputStream;

public class MyAccountActivity extends AppCompatActivity {

    private final String TARGET = "target";

    private UserRepository userRepository;
    private AuthRepository authRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);

        BottomNavigationView bottomNavigationView = findViewById(R.id.my_account_bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);
        bottomNavigationView.setSelectedItemId(R.id.my_account);

        userRepository = new UserRepository();
        authRepository = new AuthRepository();

        getSupportFragmentManager().beginTransaction().replace(R.id.my_account_container, new MyAccountFragment()).commit();
    }

    @SuppressLint("NonConstantResourceId")
    private final BottomNavigationView.OnNavigationItemSelectedListener navListener =
            item -> {
                Fragment selectedFragment = null;

                switch (item.getItemId()) {
                    case R.id.home_from_account:
                        startActivity(new Intent(MyAccountActivity.this, MainActivity.class));
                        break;
                    case R.id.search_from_account:
                        Intent intent = new Intent(MyAccountActivity.this, MainActivity.class);
                        intent.putExtra(TARGET, "search");
                        startActivity(intent);
                        break;
                    case R.id.my_account:
                        selectedFragment = new MyAccountFragment();
                        break;
                    case R.id.upload:
                        selectedFragment = SelectFromGalleryFragment.newInstance(UploadTypes.Content);
                        break;
                    case R.id.account_settings:
                        selectedFragment = new SettingsFragment();
                }

                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.my_account_container, selectedFragment).commit();
                }

                return true;
            };

    public void selectContent(View view) {
        ImageView imagePreview = findViewById(R.id.gallery_image_preview);
        VideoView videoPreview = findViewById(R.id.gallery_video_preview);
        AudioPlayer audioPreview = findViewById(R.id.gallery_audio_preview);

        if (imagePreview.getVisibility() == View.VISIBLE) {
            Bitmap selectedContent = ((BitmapDrawable)imagePreview.getDrawable()).getBitmap();
            getSupportFragmentManager().beginTransaction().replace(R.id.my_account_container, UploadContentFragment.newInstance(selectedContent, new UploadContentModel(ContentTypesEnum.Image, UploadOperationsEnum.Upload))).commit();
        } else if (videoPreview.getVisibility() == View.VISIBLE) {
            String videoUri = videoPreview.getTag().toString();
            getSupportFragmentManager().beginTransaction().replace(R.id.my_account_container, UploadContentFragment.newInstance(videoUri, new UploadContentModel(ContentTypesEnum.Video, UploadOperationsEnum.Upload))).commit();
        } else if (audioPreview.getVisibility() == View.VISIBLE) {
            String audioUri = audioPreview.getAudioPath();
            getSupportFragmentManager().beginTransaction().replace(R.id.my_account_container, UploadContentFragment.newInstance(audioUri, new UploadContentModel(ContentTypesEnum.Audio, UploadOperationsEnum.Upload))).commit();
        }
    }

    public void selectAvatar(View view) {
        Bitmap selectedContent = ((BitmapDrawable)((ImageView)findViewById(R.id.gallery_image_preview)).getDrawable()).getBitmap();
        getSupportFragmentManager().beginTransaction().replace(R.id.my_account_container, UploadAvatarFragment.newInstance(selectedContent)).commit();
    }

    public void uploadAvatar(View view) {
        ImageView contentPreview = findViewById(R.id.send_avatar_preview);
        Avatar avatar = new Avatar();

        Bitmap bitmapImage = ((BitmapDrawable)contentPreview.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmapImage.compress(Bitmap.CompressFormat.JPEG, 60, stream);

        avatar.image = Base64.encodeToString(stream.toByteArray(), Base64.DEFAULT);

        userRepository.getUser(this, user -> {
            user.avatar = avatar;
            userRepository.updateUser(this, user, response -> Toast.makeText(this, "Avatar updated", Toast.LENGTH_SHORT).show());
        });
    }

    public void logOut(View view) {
        authRepository.logout(this, response -> {
            Utils.saveSessionId(this, "");
            this.runOnUiThread(() -> {
                Intent intent = new Intent(this, LoginActivity.class);
                this.startActivity(intent);
                this.finish();
            });
        });
    }
}