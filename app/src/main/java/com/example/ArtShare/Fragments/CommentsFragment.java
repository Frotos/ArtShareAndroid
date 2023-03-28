package com.example.ArtShare.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ArtShare.Adapters.CommentsAdapter;
import com.example.ArtShare.Models.Comment;
import com.example.ArtShare.Models.Content;
import com.example.ArtShare.Models.Requests.UploadCommentRequest;
import com.example.ArtShare.R;
import com.example.ArtShare.Repositories.CommentsRepository;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Arrays;

public class CommentsFragment extends Fragment {

    private final Fragment fragment;

    private Context context;

    private RecyclerView recyclerView;
    private CommentsAdapter commentsAdapter;

    private final ArrayList<Comment> comments = new ArrayList<>();

    protected Handler handler;

    private Content content;

    private final CommentsRepository commentsRepository;

    public CommentsFragment() {
        fragment = this;
        commentsRepository = new CommentsRepository();
    }

    public static CommentsFragment newInstance(Content imageContent) {
        Bundle args = new Bundle();
        args.putSerializable("image", imageContent);
        CommentsFragment fragment = new CommentsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_comments, container, false);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        context = getContext();
        FragmentActivity activity = getActivity();

        if (activity != null) {
            recyclerView = activity.findViewById(R.id.comments_recyclerview);
            LinearLayoutManager layoutManager = new LinearLayoutManager(activity);
            recyclerView.setLayoutManager(layoutManager);

            ImageButton closeButton = view.findViewById(R.id.close_comment_btn);

            closeButton.setOnClickListener(v -> {
                BottomNavigationView bottomNavigationView = activity.findViewById(R.id.main_bottom_navigation);
                switch (bottomNavigationView.getSelectedItemId()) {
                    case R.id.nav_home:
                        activity.getSupportFragmentManager().beginTransaction().replace(R.id.content_container, new HomeFragment()).commit();
                        break;
                    case R.id.nav_search:
                        activity.getSupportFragmentManager().beginTransaction().replace(R.id.content_container, new SearchFragment()).commit();
                        break;
                }
            });

            ImageButton sendComment = view.findViewById(R.id.send_comment_button);

            sendComment.setOnClickListener(v -> {
                EditText commentText = view.findViewById(R.id.comment_edit_text);

                if (commentText.length() == 0) {
                    return;
                }

                commentsRepository.sendComment(context, new UploadCommentRequest(content.id, commentText.toString()), response -> {
                    LoadMoreComments(comments.size());
                    commentText.setText("");
                });
            });

            handler = new Handler();
            content = (Content) (getArguments() != null ? getArguments().getSerializable("image") : null);

            LoadComments();
        }
    }

    private void LoadComments() {
        commentsRepository.loadComments(context, content.id, response -> {
            Gson gson = new GsonBuilder().serializeNulls().create();
            comments.addAll(Arrays.asList(gson.fromJson(response, Comment[].class)));
            commentsAdapter = new CommentsAdapter(fragment, comments, recyclerView);
            recyclerView.setAdapter(commentsAdapter);
            commentsAdapter.setOnLoadMoreListener(() -> handler.postDelayed(() -> LoadMoreComments(comments.size()), 2000));
        });
    }

    private void LoadMoreComments(int commentsToSkip) {
        int pageSize = 10;
        int page = commentsToSkip / pageSize + 1;
        commentsRepository.loadMoreComments(context, content.id, page, response -> {
            Gson gson = new GsonBuilder().serializeNulls().create();
            Comment[] newComments = gson.fromJson(response, Comment[].class);
            int itemsAdded = 0;
            for (Comment comment : newComments) {
                if (!comments.contains(comment)) {
                    comments.add(comment);
                    itemsAdded++;
                }
            }

            int finalItemsAdded = itemsAdded;
            recyclerView.post(() -> {
                if (finalItemsAdded > 0) {
                    commentsAdapter.notifyItemRangeChanged(comments.size() - finalItemsAdded, finalItemsAdded);
                }
            });
            commentsAdapter.setLoaded();
        });
    }
}
