package com.example.ArtShare.Repositories;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.ArtShare.Models.Responses.ErrorModel;
import com.example.ArtShare.R;
import com.example.ArtShare.Utils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;
import java.util.Map;

public class FollowingRepository {
    public void createFollowing(Context context, int followUserId, Response.Listener<String> responseCallback) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = context.getString(R.string.base_followings_url) + followUserId;
        StringRequest jsonObjectRequest = new StringRequest(Request.Method.POST, url, responseCallback, error -> {
            if (error.networkResponse != null) {
                Gson gson = new GsonBuilder().serializeNulls().create();
                ErrorModel errorModel = gson.fromJson(new String(error.networkResponse.data), ErrorModel.class);
                Toast.makeText(context, errorModel.Message, Toast.LENGTH_LONG).show();
            }
        })
        {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", String.valueOf(Utils.loadSessionId(context)));
                return params;
            }
        };

        queue.add(jsonObjectRequest);
    }

    public void deleteFollowing(Context context, int unfollowUserId, Response.Listener<String> responseCallback) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = context.getString(R.string.base_followings_url) + unfollowUserId;
        StringRequest jsonObjectRequest = new StringRequest(Request.Method.DELETE, url, responseCallback, error -> {
            if (error.networkResponse != null) {
                Gson gson = new GsonBuilder().serializeNulls().create();
                ErrorModel errorModel = gson.fromJson(new String(error.networkResponse.data), ErrorModel.class);
                Toast.makeText(context, errorModel.Message, Toast.LENGTH_LONG).show();
            }
        })
        {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", String.valueOf(Utils.loadSessionId(context)));
                return params;
            }
        };

        queue.add(jsonObjectRequest);
    }
}
