package com.example.ArtShare.Fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

public class FavouritesFragment extends Fragment {

    private final Fragment fragment;

    private Context context;

    private RecyclerView recyclerView;
    private TextView emptyView;
    private ContentAdapter contentAdapter;

    private ArrayList<Content> content = new ArrayList<>();

    protected Handler handler;

    private final ContentRepository contentRepository;

    public FavouritesFragment() {
        fragment = this;
        contentRepository = new ContentRepository();
    }

    public static FavouritesFragment newInstance() {
        return new FavouritesFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favourites, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        context = getContext();
        Activity activity = getActivity();

        if (activity != null) {
            recyclerView = activity.findViewById(R.id.favourites_recyclerview);
            LinearLayoutManager layoutManager = new LinearLayoutManager(activity);
            recyclerView.setLayoutManager(layoutManager);
            emptyView = activity.findViewById(R.id.favourites_empty_view);

            handler = new Handler();

            LoadImages();
        }
    }

    private void LoadImages() {
        contentRepository.loadLikedContent(context, response -> {
            Gson gson = new GsonBuilder().serializeNulls().create();
            content = new ArrayList<>(Arrays.asList(gson.fromJson(response, Content[].class)));

            if (content.size() == 0) {
                emptyView.setVisibility(View.VISIBLE);
            } else {
                emptyView.setVisibility(View.GONE);
            }

            contentAdapter = new ContentAdapter(fragment, content, recyclerView);
            recyclerView.setAdapter(contentAdapter);
            contentAdapter.setOnLoadMoreListener(() -> handler.postDelayed(() -> LoadMoreImages(content.size()), 2000));
        }, error -> emptyView.setVisibility(View.VISIBLE));
    }

    private void LoadMoreImages(int imagesToSkip) {
        int pageSize = 10;
        int page = imagesToSkip / pageSize + 1;

        contentRepository.loadMoreLikedContent(context, page, response -> {
            Gson gson = new GsonBuilder().serializeNulls().create();
            Content[] images = gson.fromJson(response, Content[].class);
            int itemsAdded = 0;
            for (Content image : images) {
                if (content.contains(image)) {
                    break;
                }

                content.add(image);
                itemsAdded++;
            }

            int finalItemsAdded = itemsAdded;
            recyclerView.post(() -> {
                if (finalItemsAdded > 0) {
                    contentAdapter.notifyItemRangeInserted(content.size() - finalItemsAdded, finalItemsAdded);
                }
            });
            contentAdapter.setLoaded();
        });
    }
}