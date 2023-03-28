package com.example.ArtShare.Fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.ArtShare.Adapters.ContentAdapter;
import com.example.ArtShare.Models.Content;
import com.example.ArtShare.Models.Responses.ErrorModel;
import com.example.ArtShare.R;
import com.example.ArtShare.Utils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class SearchFragment extends Fragment {

    private final Fragment fragment;

    private Context context;

    private RecyclerView recyclerView;
    private TextView emptyView;
    private ContentAdapter contentAdapter;

    private EditText searchLine;

    private ArrayList<Content> content = new ArrayList<>();

    protected Handler handler;

    public SearchFragment() {fragment = this;}

    public static SearchFragment newInstance() {
        return new SearchFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        context = getContext();
        Activity activity = getActivity();

        if (activity != null) {
            recyclerView = activity.findViewById(R.id.search_recyclerview);
            searchLine = activity.findViewById(R.id.search_line);
            ImageButton searchButton = activity.findViewById(R.id.search_btn);
            LinearLayoutManager layoutManager = new LinearLayoutManager(activity);
            recyclerView.setLayoutManager(layoutManager);
            emptyView = activity.findViewById(R.id.search_empty_view);

            searchButton.setOnClickListener(v -> loadContent());

            handler = new Handler();

            loadContent();
        }
    }

    private void loadContent() {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = Utils.generateUrlWithFilters(getString(R.string.first_content_page), searchLine.getText().toString().split(" "), false, 0, false);
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.GET, url, response -> {
            Gson gson = new GsonBuilder().serializeNulls().create();
            content = new ArrayList<>(Arrays.asList(gson.fromJson(response, Content[].class)));

            if (content.size() == 0) {
                emptyView.setVisibility(View.VISIBLE);
            } else {
                emptyView.setVisibility(View.GONE);
            }

            contentAdapter = new ContentAdapter(fragment, content, recyclerView);
            recyclerView.setAdapter(contentAdapter);
            contentAdapter.setOnLoadMoreListener(() -> handler.postDelayed(() -> loadMoreContent(content.size()), 2000));
        }, error -> {
            Log.e("!", error.toString());
            //TODO: Create view for case, if search wasn't succeed
            if (error.networkResponse != null) {
                if (error.networkResponse.statusCode == 404) {
                    emptyView.setVisibility(View.VISIBLE);
                } else {
                    Gson gson = new GsonBuilder().serializeNulls().create();
                    ErrorModel errorModel = gson.fromJson(new String(error.networkResponse.data), ErrorModel.class);
                    Toast.makeText(context, errorModel.Message, Toast.LENGTH_LONG).show();
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", String.valueOf(Utils.loadSessionId(context)));
                return params;
            }
        };

        queue.add(stringRequest);
    }

    private void loadMoreContent(int imagesToSkip) {
        int pageSize = 10;
        int page = imagesToSkip / pageSize + 1;
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = Utils.generateUrlWithFilters(context.getString(R.string.base_content_url) + "page/" + page, searchLine.getText().toString().split(" "), false, 0, false);
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.GET, url, response -> {
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
        }, error -> {
            if (error.networkResponse != null) {
                Gson gson = new GsonBuilder().serializeNulls().create();
                ErrorModel errorModel = gson.fromJson(new String(error.networkResponse.data), ErrorModel.class);
                Toast.makeText(context, errorModel.Message, Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", String.valueOf(Utils.loadSessionId(context)));
                return params;
            }
        };

        queue.add(stringRequest);
    }
}
