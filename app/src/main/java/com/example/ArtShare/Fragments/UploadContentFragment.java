package com.example.ArtShare.Fragments;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ArtShare.Components.AudioPlayer;
import com.example.ArtShare.Enums.UploadOperationsEnum;
import com.example.ArtShare.Models.Content;
import com.example.ArtShare.Models.ContentTypesEnum;
import com.example.ArtShare.Models.Requests.UpdateContentRequest;
import com.example.ArtShare.Models.Tag;
import com.example.ArtShare.Models.UploadContentModel;
import com.example.ArtShare.Models.User;
import com.example.ArtShare.R;
import com.example.ArtShare.Repositories.ContentRepository;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipDrawable;
import com.google.android.material.chip.ChipGroup;

import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class UploadContentFragment extends Fragment {

    private final ContentRepository contentRepository;

    public UploadContentFragment() {
        contentRepository = new ContentRepository();
    }

    public static UploadContentFragment newInstance(String preview, UploadContentModel uploadContentModel) {
        UploadContentFragment fragment = new UploadContentFragment();
        Bundle args = new Bundle();
        args.putString(uploadContentModel.type.toString(), preview);
        args.putSerializable("uploadModel", uploadContentModel);
        fragment.setArguments(args);
        return fragment;
    }

    public static UploadContentFragment newInstance(Bitmap preview, UploadContentModel uploadContentModel) {
        UploadContentFragment fragment = new UploadContentFragment();
        Bundle args = new Bundle();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        preview.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        args.putString(uploadContentModel.type.toString(), Base64.encodeToString(stream.toByteArray(), Base64.DEFAULT));
        args.putSerializable("uploadModel", uploadContentModel);
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
        return inflater.inflate(R.layout.fragment_upload_content, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Context context = getContext();
        Activity activity = getActivity();

        if (activity != null && context != null) {
            Button uploadButton = activity.findViewById(R.id.upload_content_button);
            ImageButton addTagButton = activity.findViewById(R.id.add_tag);
            ImageView imagePreview = activity.findViewById(R.id.send_content_preview);
            VideoView videoPreview = activity.findViewById(R.id.send_content_video_preview);
            AudioPlayer audioPreview = activity.findViewById(R.id.send_content_audio_preview);
            EditText title = activity.findViewById(R.id.send_content_title);
            EditText description = activity.findViewById(R.id.send_content_description);
            ChipGroup tags = activity.findViewById(R.id.tags_group);

            Bundle args = getArguments();
            if (args != null) {
                UploadContentModel uploadContentModel = (UploadContentModel) args.getSerializable("uploadModel");
                title.setText(uploadContentModel.title == null ? "" : uploadContentModel.title);
                description.setText(uploadContentModel.description == null ? "" : uploadContentModel.description);
                if (uploadContentModel.tags != null) {
                    for (int i = 0; i < uploadContentModel.tags.size(); i++) {
                        Chip tag = new Chip(context);
                        ChipDrawable drawable = ChipDrawable.createFromAttributes(context, null, 0, R.style.Widget_MaterialComponents_Chip_Entry);
                        tag.setChipDrawable(drawable);

                        tag.setCheckable(false);
                        tag.setClickable(false);
                        tag.setChipIconResource(R.drawable.ic_tag);
                        tag.setIconStartPadding(3f);
                        tag.setPadding(60, 10, 60, 10);
                        tag.setText(uploadContentModel.tags.get(i).name);
                        tag.setOnCloseIconClickListener(v -> tags.removeView(tag));

                        tags.addView(tag);
                    }
                }

                if (args.getString(ContentTypesEnum.Image.toString()) != null) {
                    imagePreview.setVisibility(View.VISIBLE);
                    byte[] bytes = Base64.decode(args.getString(ContentTypesEnum.Image.toString()), Base64.DEFAULT);
                    imagePreview.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
                } else if (args.get(ContentTypesEnum.Video.toString()) != null) {
                    Uri videoUri = Uri.parse("file://" + args.getString(ContentTypesEnum.Video.toString()));
                    videoPreview.setVisibility(View.VISIBLE);
                    videoPreview.setTag(videoUri);
                    videoPreview.setVideoURI(videoUri);
                    videoPreview.start();
                } else if (args.getString(ContentTypesEnum.Audio.toString()) != null) {
                    String audioPath = args.getString(ContentTypesEnum.Audio.toString());
                    audioPreview.setVisibility(View.VISIBLE);
                    audioPreview.setAudioPath(audioPath);
                    audioPreview.start();
                }

                uploadButton.setOnClickListener(v -> uploadContent(uploadContentModel));
                addTagButton.setOnClickListener(this::addTag);
            }
        }
    }

    private void addTag(View view) {
        Activity activity = getActivity();
        Context context = getContext();

        EditText tagInput;
        if (activity != null && context != null) {
            tagInput = activity.findViewById(R.id.tag_input);
            ChipGroup tagsGroup = activity.findViewById(R.id.tags_group);

            if (!tagInput.getText().toString().equals("")) {
                Chip tag = new Chip(context);
                ChipDrawable drawable = ChipDrawable.createFromAttributes(context, null, 0, R.style.Widget_MaterialComponents_Chip_Entry);
                tag.setChipDrawable(drawable);

                tag.setCheckable(false);
                tag.setClickable(false);
                tag.setChipIconResource(R.drawable.ic_tag);
                tag.setIconStartPadding(3f);
                tag.setPadding(60, 10, 60, 10);
                tag.setText(tagInput.getText().toString());
                tag.setOnCloseIconClickListener(v -> tagsGroup.removeView(tag));

                tagsGroup.addView(tag);
                tagInput.setText("");
            }
        }
    }

    private Content getContent() {
        Activity activity = getActivity();

        String title = null;
        if (activity != null) {
            title = ((EditText)activity.findViewById(R.id.send_content_title)).getText().toString();
            String description = ((EditText)activity.findViewById(R.id.send_content_description)).getText().toString();

            ChipGroup tagsGroup = activity.findViewById(R.id.tags_group);

            ImageView imagePreview = activity.findViewById(R.id.send_content_preview);
            VideoView videoPreview = activity.findViewById(R.id.send_content_video_preview);
            AudioPlayer audioPreview = activity.findViewById(R.id.send_content_audio_preview);

            Content content = new Content();
            content.user = new User();

            if (!title.equals("")) {
                content.title = title;
            }

            if (!description.equals("")) {
                content.description = description;
            }

            if (tagsGroup.getChildCount() > 0) {
                for (int i = 0; i < tagsGroup.getChildCount(); i++) {
                    content.tags.add(new Tag(((Chip)tagsGroup.getChildAt(i)).getText().toString().toLowerCase()));
                }
            }

            if (imagePreview.getVisibility() == View.VISIBLE) {
                content.type = ContentTypesEnum.Image;
                Bitmap bitmapImage = ((BitmapDrawable)imagePreview.getDrawable()).getBitmap();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmapImage.compress(Bitmap.CompressFormat.JPEG, 60, stream);

                content.contentBase64 = Base64.encodeToString(stream.toByteArray(), Base64.DEFAULT);
            } else if (videoPreview.getVisibility() == View.VISIBLE) {
                content.type = ContentTypesEnum.Video;
                Uri videoUri = (Uri)videoPreview.getTag();
                InputStream inputStream = null;
                try {
                    inputStream = activity.getContentResolver().openInputStream(videoUri);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                int bufferSize = 1024;
                byte[] buffer = new byte[bufferSize];
                ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
                int len = 0;
                try {
                    while ((len = inputStream.read(buffer)) != -1) {
                        byteBuffer.write(buffer, 0, len);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                content.contentBase64 = Base64.encodeToString(byteBuffer.toByteArray(), Base64.DEFAULT);
            } else if (audioPreview.getVisibility() == View.VISIBLE) {
                content.type = ContentTypesEnum.Audio;
                Uri audioUri = Uri.parse(audioPreview.getAudioPath());
                InputStream inputStream = null;
                try {
                    inputStream = activity.getContentResolver().openInputStream(audioUri);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                int bufferSize = 1024;
                byte[] buffer = new byte[bufferSize];
                ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
                int len = 0;
                try {
                    while ((len = inputStream.read(buffer)) != -1) {
                        byteBuffer.write(buffer, 0, len);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                content.contentBase64 = Base64.encodeToString(byteBuffer.toByteArray(), Base64.DEFAULT);
            }

            return content;
        }

        return null;
    }

    private UpdateContentRequest getUpdateContentRequest(int id) {
        Activity activity = getActivity();

        UpdateContentRequest request = new UpdateContentRequest();

        request.id = id;
        if (activity != null) {
            request.title = ((EditText)activity.findViewById(R.id.send_content_title)).getText().toString();
            request.description = ((EditText)activity.findViewById(R.id.send_content_description)).getText().toString();

            ChipGroup tagsGroup = activity.findViewById(R.id.tags_group);

            if (tagsGroup.getChildCount() > 0) {
                for (int i = 0; i < tagsGroup.getChildCount(); i++) {
                    request.tags.add(new Tag(((Chip)tagsGroup.getChildAt(i)).getText().toString().toLowerCase()));
                }
            }

            return request;
        }

        return null;
    }

    private void uploadContent(UploadContentModel uploadContentModel) {
        Context context = getContext();
        Activity activity = getActivity();

        if (uploadContentModel.operation == UploadOperationsEnum.Upload) {
            contentRepository.createContent(context, activity, getContent(), response -> {
                try {
                    Toast.makeText(context, response.getString("message"), Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            });
        } else if (uploadContentModel.operation == UploadOperationsEnum.Update) {
            contentRepository.updateContent(context, activity, getUpdateContentRequest(uploadContentModel.id), response -> {
                try {
                    Toast.makeText(context, response.getString("message"), Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        Activity activity = getActivity();
        if (activity != null) {
            VideoView videoPreview = activity.findViewById(R.id.send_content_video_preview);
            AudioPlayer audioPreview = activity.findViewById(R.id.send_content_audio_preview);

            if (videoPreview != null && videoPreview.isPlaying()) {
                videoPreview.stopPlayback();
            }

            if (audioPreview != null) {
                audioPreview.stop();
            }
        }
    }
}
