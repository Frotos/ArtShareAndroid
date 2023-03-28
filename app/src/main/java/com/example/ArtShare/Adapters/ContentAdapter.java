package com.example.ArtShare.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ArtShare.Components.AudioPlayer;
import com.example.ArtShare.Components.VideoPlayer;
import com.example.ArtShare.Enums.UploadOperationsEnum;
import com.example.ArtShare.Fragments.CommentsFragment;
import com.example.ArtShare.Fragments.UploadContentFragment;
import com.example.ArtShare.Models.Content;
import com.example.ArtShare.Models.ContentTypesEnum;
import com.example.ArtShare.Models.Requests.CreateContentReportRequest;
import com.example.ArtShare.Models.UploadContentModel;
import com.example.ArtShare.OnLoadMoreListener;
import com.example.ArtShare.R;
import com.example.ArtShare.Repositories.ContentRepository;
import com.example.ArtShare.Repositories.FollowingRepository;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class ContentAdapter extends RecyclerView.Adapter<ContentAdapter.ViewHolder> {

    private final Context context;
    private final Activity activity;
    private final ArrayList<Content> images;

    private final int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;
    private OnLoadMoreListener onLoadMoreListener;
    private boolean loading;

    private final ContentRepository contentRepository;
    private final FollowingRepository followingRepository;

    public ContentAdapter(Fragment fragment, ArrayList<Content> imageContents, RecyclerView recyclerView) {
        this.context = fragment.getContext();
        this.activity = fragment.getActivity();
        this.images = imageContents;

        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                totalItemCount = linearLayoutManager != null ? linearLayoutManager.getItemCount() : 0;
                lastVisibleItem = linearLayoutManager != null ? linearLayoutManager
                        .findLastVisibleItemPosition() : 0;
                if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                    if (onLoadMoreListener != null) {
                        onLoadMoreListener.onLoadMore();
                    }
                    loading = true;
                }
            }
        });

        contentRepository = new ContentRepository();
        followingRepository = new FollowingRepository();
    }

    @NonNull
    @Override
    public ContentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(this.context);
        ConstraintLayout layout = (ConstraintLayout) inflater.inflate(R.layout.content_item, parent, false);
        return new ViewHolder(layout);
    }

    @Override
    public void onBindViewHolder(@NonNull ContentAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Content content = images.get(holder.getAdapterPosition());

        Bitmap avatarBitmapImage;
        if (content.user != null) {
            if (content.user.avatar.image != null) {
                byte[] avatarBytes = Base64.decode(content.user.avatar.image, Base64.DEFAULT);
                avatarBitmapImage = BitmapFactory.decodeByteArray(avatarBytes, 0, avatarBytes.length);
                holder.profileImage.setImageBitmap(avatarBitmapImage);
            }

            holder.profileName.setText(content.user.username);

            holder.follow.setVisibility(View.GONE);
            if (content.user.isFollowed) {
                holder.follow.setText(R.string.unfollow);
            } else {
                holder.follow.setText(R.string.follow);
            }
            if (!content.uploadedByUser) {
                holder.follow.setVisibility(View.VISIBLE);
                holder.follow.setOnClickListener(v -> {
                    if (content.user.isFollowed) {
                        unfollowUser(content.user.id);
                    } else {
                        followUser(content.user.id);
                    }
                });
            }
        }

        if (content.type == ContentTypesEnum.Image) {
            Bitmap contentBitmapImage = null;
            try {
                byte[] contentBytes = Base64.decode(content.contentBase64, Base64.DEFAULT);
                contentBitmapImage = BitmapFactory.decodeByteArray(contentBytes, 0, contentBytes.length);
            } catch (Exception e){
                Log.d("LoadImageContent", e.getMessage());
            }

            if (contentBitmapImage != null){
                holder.imageContentContainer.setVisibility(View.VISIBLE);
                holder.imageContent.setImageBitmap(contentBitmapImage);
            }
        } else if (content.type == ContentTypesEnum.Video) {
            File contentCacheDir = new File(activity.getExternalCacheDir().getAbsoluteFile() + "/content");
            if (!contentCacheDir.exists()) {
                contentCacheDir.mkdir();
            }
            File cacheFile = new File(contentCacheDir.getAbsolutePath() + "/" + content.id);
            if (!cacheFile.exists()) {
                try {
                    cacheFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            try {
                FileOutputStream stream = new FileOutputStream(cacheFile);
                stream.write(Base64.decode(content.contentBase64, Base64.DEFAULT));
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            holder.videoContent.setVisibility(View.VISIBLE);
            holder.videoContent.setVideoPath("file://" + cacheFile.getAbsolutePath());
        } else if (content.type == ContentTypesEnum.Audio) {
            File contentCacheDir = new File(activity.getExternalCacheDir().getAbsoluteFile() + "/content");
            if (!contentCacheDir.exists()) {
                contentCacheDir.mkdir();
            }
            File cacheFile = new File(contentCacheDir.getAbsolutePath() + "/" + content.id);
            if (!cacheFile.exists()) {
                try {
                    cacheFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            try {
                FileOutputStream stream = new FileOutputStream(cacheFile);
                stream.write(Base64.decode(content.contentBase64, Base64.DEFAULT));
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            holder.audioContent.setVisibility(View.VISIBLE);
            holder.audioContent.setAudioPath("file://" + cacheFile.getAbsolutePath());
        }

        holder.postTitle.setText(content.title);
        holder.postTitle.setVisibility(View.VISIBLE);
        if (content.title == null) {
            holder.postTitle.setVisibility(View.GONE);
        }
        holder.postDescription.setText(content.description);
        holder.postDescription.setVisibility(View.VISIBLE);
        if (content.description == null) {
            holder.postDescription.setVisibility(View.GONE);
        }

        if (content.isLiked) {
            holder.likeButton.setBackgroundResource(R.drawable.ic_like);
        } else {
            holder.likeButton.setBackgroundResource(R.drawable.ic_like_unliked);
        }
        holder.likeButton.setOnClickListener(v -> contentRepository.likeContent(context, content.isLiked, content.id, response -> {
            content.isLiked = !content.isLiked;
            activity.runOnUiThread(() -> {
                int value = content.isLiked ? 1 : -1;
                activity.runOnUiThread(() -> updateLikesCount(position, value));
            });
        }));

        holder.likesCount.setText(String.valueOf(content.likesCount));

        if (content.isDisliked) {
            holder.dislikeButton.setBackgroundResource(R.drawable.ic_dislike);
        } else {
            holder.dislikeButton.setBackgroundResource(R.drawable.ic_dislike_undisliked);
        }
        holder.dislikeButton.setOnClickListener(v -> contentRepository.dislikeContent(context, content.isDisliked, content.id, response -> {
            content.isDisliked = !content.isDisliked;
            activity.runOnUiThread(() -> {
                int value = content.isDisliked ? 1 : -1;
                activity.runOnUiThread(() -> updateDislikesCount(position, value));
            });
        }));
        holder.dislikesCount.setText(String.valueOf(content.dislikesCount));

        holder.commentsButton.setOnClickListener(v -> {
            FragmentManager fragmentManager = ((FragmentActivity) activity).getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_container, CommentsFragment.newInstance(content)).commit();
        });

        holder.optionsButton.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(context, holder.optionsButton);
            popupMenu.inflate(R.menu.content_options_menu);

            MenuItem editItem = popupMenu.getMenu().findItem(R.id.content_options_edit);
            if (content.uploadedByUser) {
                editItem.setVisible(true);
            }

            MenuItem deleteItem = popupMenu.getMenu().findItem(R.id.content_options_delete);
            if (content.uploadedByUser) {
                deleteItem.setVisible(true);
            }

            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.content_options_edit:
                        UploadContentModel uploadContentModel = new UploadContentModel(
                                content.id,
                                content.type,
                                content.title,
                                content.description,
                                content.tags,
                                UploadOperationsEnum.Update
                        );
                        File contentCacheDir = new File(activity.getExternalCacheDir().getAbsoluteFile() + "/content");
                        File cacheFile = new File(contentCacheDir.getAbsolutePath() + "/" + content.id);

                        switch (content.type) {
                            case Audio:
                            case Video:
                                ((FragmentActivity) context).getSupportFragmentManager()
                                        .beginTransaction()
                                        .replace(R.id.content_container, UploadContentFragment.newInstance(cacheFile.getAbsolutePath(), uploadContentModel))
                                        .commit();
                                break;
                            case Image:
                                ((FragmentActivity) context).getSupportFragmentManager()
                                        .beginTransaction()
                                        .replace(R.id.content_container, UploadContentFragment.newInstance(content.contentBase64, uploadContentModel))
                                        .commit();
                                break;
                        }
                        return true;
                    case R.id.content_options_report:
                        contentRepository.reportContent(context, content.id, response -> {
                            Toast.makeText(context, "Content reported", Toast.LENGTH_SHORT).show();
                        });

                        return true;
                    case R.id.content_options_delete:
                        contentRepository.deleteContent(context, content.id, response -> {
                            activity.runOnUiThread(() -> {
                                int contentIndex = images.indexOf(content);
                                images.remove(contentIndex);
                                notifyItemRemoved(contentIndex);
                            });
                        });

                        return true;
                    default:
                        return false;
                }
            });

            popupMenu.show();
        });
    }

    private void followUser(int followUserId) {
        followingRepository.createFollowing(context, followUserId, response -> updateFollowing(followUserId, false));
    }

    public void unfollowUser(int unfollowUserId) {
        followingRepository.deleteFollowing(context, unfollowUserId, response -> updateFollowing(unfollowUserId, true));
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public void setLoaded() {
        loading = false;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    public void updateLikesCount(int position, int value) {
        Content image = images.get(position);
        image.likesCount += value;

        image.isLiked = value > 0;

        notifyItemChanged(position);
    }

    public void updateFollowing(int id, boolean value) {
        for (int i = 0; i < images.size(); i++) {
            Content content = images.get(i);
            if (content.user.id == id) {
                content.user.isFollowed = !value;

                notifyItemChanged(i);
            }
        }
    }

    public void updateDislikesCount(int position, int value) {
        Content image = images.get(position);
        image.dislikesCount += value;

        image.isDisliked = value > 0;

        notifyItemChanged(position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView profileImage;
        private final TextView profileName;
        private final TextView follow;
        private final TextView postTitle;
        private final TextView postDescription;
        private final ImageView imageContent;
        private final CardView imageContentContainer;
        private final VideoPlayer videoContent;
        private final AudioPlayer audioContent;
        private final Button likeButton;
        private final TextView likesCount;
        private final Button dislikeButton;
        private final TextView dislikesCount;
        private final Button commentsButton;
        private final Button optionsButton;

        public ViewHolder(View view) {
            super(view);
            profileImage = view.findViewById(R.id.profile_image);
            profileName = view.findViewById(R.id.profile_name);
            follow = view.findViewById(R.id.follow);
            postTitle = view.findViewById(R.id.content_title);
            postDescription = view.findViewById(R.id.content_description);
            imageContent = view.findViewById(R.id.image_content);
            imageContentContainer = view.findViewById(R.id.image_content_container);
            videoContent = view.findViewById(R.id.video_content_player);
            audioContent = view.findViewById(R.id.audio_content);
            likeButton = view.findViewById(R.id.like_button);
            likesCount = view.findViewById(R.id.likes_count);
            dislikeButton = view.findViewById(R.id.dislike_button);
            dislikesCount = view.findViewById(R.id.dislikes_count);
            commentsButton = view.findViewById(R.id.content_comments_button);
            optionsButton = view.findViewById(R.id.options_button);
        }
    }
}
