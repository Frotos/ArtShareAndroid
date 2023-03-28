package com.example.ArtShare.Repositories;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.ArtShare.Models.Requests.CreateCommentReportRequest;
import com.example.ArtShare.Models.Requests.UploadCommentRequest;
import com.example.ArtShare.Models.Responses.ErrorModel;
import com.example.ArtShare.R;
import com.example.ArtShare.Utils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class CommentsRepository {
    public void sendComment(Context context, UploadCommentRequest uploadCommentRequest, Response.Listener<String> responseCallback) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = context.getString(R.string.base_comments_url);

        StringRequest request = new StringRequest(com.android.volley.Request.Method.POST, url, responseCallback, error -> {
            if (error.networkResponse != null) {
                Gson gson = new GsonBuilder().serializeNulls().create();
                ErrorModel errorModel = gson.fromJson(new String(error.networkResponse.data), ErrorModel.class);
                Toast.makeText(context, errorModel.Message, Toast.LENGTH_LONG).show();
            }
        })
        {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() {
                String body = Utils.convertToJSON(uploadCommentRequest);
                return body.getBytes(StandardCharsets.UTF_8);
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", String.valueOf(Utils.loadSessionId(context)));
                return params;
            }
        };

        queue.add(request);
    }

    public void likeComment(Context context, boolean isLiked, int commentId, Response.Listener<String> responseCallback) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = String.format(context.getString(R.string.base_comments_url) + "%s/%d",
                isLiked ? "unlike" : "like",
                commentId);

        StringRequest request = new StringRequest(com.android.volley.Request.Method.POST, url, responseCallback, error -> {
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

        queue.add(request);
    }

    public void dislikeComment(Context context, boolean isDisliked, int commentId, Response.Listener<String> responseCallback) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = String.format(context.getString(R.string.base_comments_url) + "%s/%d",
                isDisliked ? "undislike" : "dislike",
                commentId);

        StringRequest request = new StringRequest(com.android.volley.Request.Method.POST, url, responseCallback, error -> {
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

        queue.add(request);
    }

    public void reportComment(Context context, CreateCommentReportRequest reportRequest, Response.Listener<JSONObject> responseCallback) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String urlToReport = context.getString(R.string.report_comment_url);

        JSONObject jsonObject = null;

        try {
            jsonObject = new JSONObject(Utils.convertToJSON(reportRequest));
        } catch (JSONException exception) {
            Log.e(this.getClass().getName(), exception.getMessage());
        }

        if (jsonObject != null) {
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(com.android.volley.Request.Method.POST, urlToReport, jsonObject, responseCallback, error -> {
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

    public void deleteComment(Context context, int commentId, Response.Listener<String> responseCallback) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String urlToDelete = context.getString(R.string.base_comments_url) + commentId;

        StringRequest request = new StringRequest(com.android.volley.Request.Method.DELETE, urlToDelete, responseCallback, error -> {
            // TODO: Check in all requests if response data isn't null
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

        queue.add(request);
    }

    public void loadComments(Context context, int contentId, Response.Listener<String> responseCallback) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = context.getString(R.string.base_comments_url) + contentId + "/page/1";

        StringRequest request = new StringRequest(com.android.volley.Request.Method.GET, url, responseCallback, error -> {
            // TODO: Check in all requests if response data isn't null
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

        queue.add(request);
    }

    public void loadMoreComments(Context context, int contentId, int page, Response.Listener<String> responseCallback) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = context.getString(R.string.base_comments_url) + contentId + "/page/" + page;

        StringRequest request = new StringRequest(com.android.volley.Request.Method.GET, url, responseCallback, error -> {
            // TODO: Check in all requests if response data isn't null
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

        queue.add(request);
    }
}
