package com.example.ArtShare.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
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
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ArtShare.Models.Comment;
import com.example.ArtShare.Models.Requests.CreateCommentReportRequest;
import com.example.ArtShare.OnLoadMoreListener;
import com.example.ArtShare.R;
import com.example.ArtShare.Repositories.CommentsRepository;

import java.util.ArrayList;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ViewHolder> {

    private final Context context;
    private final Activity activity;
    private final ArrayList<Comment> comments;

    private final int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;
    private OnLoadMoreListener onLoadMoreListener;
    private boolean loading;

    private final CommentsRepository commentsRepository;

    public CommentsAdapter(Fragment fragment, ArrayList<Comment> comments, RecyclerView recyclerView) {
        this.context = fragment.getContext();
        this.activity = fragment.getActivity();
        this.comments = comments;

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

        commentsRepository = new CommentsRepository();
    }

    @NonNull
    @Override
    public CommentsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(this.context);
        ConstraintLayout layout = (ConstraintLayout) inflater.inflate(R.layout.comment_item, parent, false);
        return new ViewHolder(layout);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onBindViewHolder(@NonNull CommentsAdapter.ViewHolder holder, int position) {
        Comment comment = comments.get(position);

        Bitmap avatarBitmapImage;
        if (comment.user.avatar.image != null) {
            byte[] avatarBytes = Base64.decode(comment.user.avatar.image, Base64.DEFAULT);
            avatarBitmapImage = BitmapFactory.decodeByteArray(avatarBytes, 0, avatarBytes.length);
            holder.profileImage.setImageBitmap(avatarBitmapImage);
        }

        holder.profileName.setText(comment.user.username);
        holder.commentText.setText(comment.text);
        holder.commentText.setVisibility(View.VISIBLE);

        if (comment.isLiked) {
            holder.likeButton.setBackgroundResource(R.drawable.ic_like);
        } else {
            holder.likeButton.setBackgroundResource(R.drawable.ic_like_unliked);
        }
        holder.likeButton.setOnClickListener(v -> commentsRepository.likeComment(context, comment.isLiked, comment.id, response -> {
            comment.isLiked = !comment.isLiked;
            activity.runOnUiThread(() -> {
                int value = comment.isLiked ? 1 : -1;
                activity.runOnUiThread(() -> updateLikesCount(position, value));
            });
        }));

        holder.likesCount.setText(String.valueOf(comment.likesCount));

        if (comment.isDisliked) {
            holder.dislikeButton.setBackgroundResource(R.drawable.ic_dislike);
        } else {
            holder.dislikeButton.setBackgroundResource(R.drawable.ic_dislike_undisliked);
        }
        holder.dislikeButton.setOnClickListener(v -> commentsRepository.dislikeComment(context, comment.isDisliked, comment.id, response -> {
            comment.isDisliked = !comment.isDisliked;
            activity.runOnUiThread(() -> {
                int value = comment.isDisliked ? 1 : -1;
                activity.runOnUiThread(() -> updateDislikesCount(position, value));
            });
        }));
        holder.dislikesCount.setText(String.valueOf(comment.dislikesCount));

        holder.optionsButton.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(context, holder.optionsButton);
            popupMenu.inflate(R.menu.comment_options_menu);

            MenuItem deleteItem = popupMenu.getMenu().findItem(R.id.comment_options_delete);
            deleteItem.setVisible(false);
            if (comment.uploadedByUser) {
                deleteItem.setVisible(true);
            }

            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.comment_options_report:
                        CreateCommentReportRequest reportRequest = new CreateCommentReportRequest(comment.id);
                        commentsRepository.reportComment(context, reportRequest, response -> Toast.makeText(context, "Comment reported", Toast.LENGTH_SHORT).show());

                        return true;
                    case R.id.comment_options_delete:
                        commentsRepository.deleteComment(context, comment.id, response -> activity.runOnUiThread(() -> {
                            int commentIndex = comments.indexOf(comment);
                            comments.remove(commentIndex);
                            notifyItemRemoved(commentIndex);
                        }));

                        return true;
                    default:
                        return false;
                }
            });

            popupMenu.show();
        });
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public void setLoaded() {
        loading = false;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    public void updateLikesCount(int position, int value) {
        Comment comment = comments.get(position);
        comment.likesCount += value;

        comment.isLiked = value > 0;

        notifyItemChanged(position);
    }

    public void updateDislikesCount(int position, int value) {
        Comment comment = comments.get(position);
        comment.dislikesCount += value;

        comment.isDisliked = value > 0;

        notifyItemChanged(position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView profileImage;
        private final TextView profileName;
        private final TextView commentText;
        private final Button likeButton;
        private final TextView likesCount;
        private final Button dislikeButton;
        private final TextView dislikesCount;
        private final Button optionsButton;

        public ViewHolder(View view) {
            super(view);
            profileImage = view.findViewById(R.id.profile_image);
            profileName = view.findViewById(R.id.profile_name);
            commentText = view.findViewById(R.id.comment);
            likeButton = view.findViewById(R.id.like_button);
            likesCount = view.findViewById(R.id.likes_count);
            dislikeButton = view.findViewById(R.id.dislike_button);
            dislikesCount = view.findViewById(R.id.dislikes_count);
            optionsButton = view.findViewById(R.id.options_button);
        }
    }
}
