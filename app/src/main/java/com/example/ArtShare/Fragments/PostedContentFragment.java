package com.example.ArtShare.Fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ArtShare.Adapters.ContentAdapter;
import com.example.ArtShare.Models.Content;
import com.example.ArtShare.R;
import com.example.ArtShare.Repositories.ContentRepository;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Arrays;

public class PostedContentFragment extends Fragment {
    private final Fragment fragment;

    private Context context;

    private RecyclerView recyclerView;
    private ContentAdapter contentAdapter;

    private final ArrayList<Content> content = new ArrayList<>();

    protected Handler handler;

    private final ContentRepository contentRepository;

    public PostedContentFragment() {
        fragment = this;
        contentRepository = new ContentRepository();
    }

    public static PostedContentFragment newInstance() {
        return new PostedContentFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_posted_content, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        context = getContext();
        Activity activity = getActivity();

        if (activity != null) {
            recyclerView = activity.findViewById(R.id.posted_content_recyclerview);
            LinearLayoutManager layoutManager = new LinearLayoutManager(activity);
            recyclerView.setLayoutManager(layoutManager);

            handler = new Handler();

            loadPostedContent();
        }
    }

    private void loadPostedContent() {
        contentRepository.loadPostedContent(context, response -> {
            Gson gson = new GsonBuilder().serializeNulls().create();
            content.addAll(Arrays.asList(gson.fromJson(response, Content[].class)));
            contentAdapter = new ContentAdapter(fragment, content, recyclerView);
            recyclerView.setAdapter(contentAdapter);
            contentAdapter.setOnLoadMoreListener(() -> handler.postDelayed(() -> loadMorePostedContent(content.size()), 2000));
        });
    }

    private void loadMorePostedContent(int contentToSkip) {
        int pageSize = 10;
        int page = contentToSkip / pageSize + 1;

        contentRepository.loadMorePostedContent(context, page, response -> {
            Gson gson = new GsonBuilder().serializeNulls().create();
            Content[] content = gson.fromJson(response, Content[].class);
            int itemsAdded = 0;
            for (Content cont : content) {
                if (this.content.contains(cont)){
                    break;
                }

                this.content.add(cont);
                itemsAdded++;
            }

            int finalItemsAdded = itemsAdded;
            recyclerView.post(() -> {
                if (finalItemsAdded > 0) {
                    contentAdapter.notifyItemRangeInserted(this.content.size() - finalItemsAdded, finalItemsAdded);
                }
            });
            contentAdapter.setLoaded();
        });
    }
}
