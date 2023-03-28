package com.example.ArtShare.Repositories;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.ArtShare.Models.Content;
import com.example.ArtShare.Models.Requests.CreateContentReportRequest;
import com.example.ArtShare.Models.Requests.UpdateContentRequest;
import com.example.ArtShare.Models.Responses.ErrorModel;
import com.example.ArtShare.R;
import com.example.ArtShare.Utils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ContentRepository {
    public void createContent(Context context, Activity activity, Content content, Response.Listener<JSONObject> responseCallback) {
        JSONObject jsonObject = null;

        try {
            jsonObject = new JSONObject(Utils.convertToJSON(content));
        } catch (JSONException exception) {
            Log.e(this.getClass().getName(), exception.getMessage());
        }

        RequestQueue queue = Volley.newRequestQueue(context);
        String url = activity.getString(R.string.base_content_url);
        if (jsonObject != null) {
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, responseCallback, error -> {
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

            queue.add(jsonObjectRequest);
        }
    }

    public void updateContent(Context context, Activity activity, UpdateContentRequest updateContentRequest, Response.Listener<JSONObject> responseCallback) {
        JSONObject jsonObject = null;

        try {
            jsonObject = new JSONObject(Utils.convertToJSON(updateContentRequest));
        } catch (JSONException exception) {
            Log.e(this.getClass().getName(), exception.getMessage());
        }

        RequestQueue queue = Volley.newRequestQueue(context);
        String url = activity.getString(R.string.base_content_url);
        if (jsonObject != null) {
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, url, jsonObject, responseCallback, error -> {
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

            queue.add(jsonObjectRequest);
        }
    }

    public void likeContent(Context context, boolean isLiked, int contentId, Response.Listener<String> responseCallback) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = String.format(context.getString(R.string.base_content_url) + "%s/%d",
                isLiked ? "unlike" : "like",
                contentId);

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

    public void dislikeContent(Context context, boolean isDisliked, int contentId, Response.Listener<String> responseCallback) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = String.format(context.getString(R.string.base_content_url) + "%s/%d",
                isDisliked ? "undislike" : "dislike",
                contentId);

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

    public void reportContent(Context context, int contentId, Response.Listener<String> responseCallback) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String urlToReport = context.getString(R.string.report_content_url) + contentId;

        StringRequest jsonObjectRequest = new StringRequest(com.android.volley.Request.Method.POST, urlToReport, responseCallback, error -> {
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

    public void deleteContent(Context context, int contentId, Response.Listener<String> responseCallback) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String urlToDelete = context.getString(R.string.base_content_url) + contentId;

        StringRequest request = new StringRequest(Request.Method.DELETE, urlToDelete, responseCallback, error -> {
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

    public void loadContent(Context context, Response.Listener<String> responseCallback, Response.ErrorListener errorCallback) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = context.getString(R.string.first_content_page);
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.GET, url, responseCallback, error -> {
            Log.e("!", error.toString());
            //TODO: Fix, can crush app
            if (error.networkResponse != null) {
                if (error.networkResponse.statusCode == 404) {
                    errorCallback.onErrorResponse(error);
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

    public void loadMoreContent(Context context, int page, Response.Listener<String> responseCallback) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = context.getString(R.string.base_content_url) + "page/" + page;
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.GET, url, responseCallback, error -> {
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

    public void loadPostedContent(Context context, Response.Listener<String> responseCallback) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = Utils.generateUrlWithFilters(context.getString(R.string.first_content_page), new String[]{}, false, 0, true);
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.GET, url, responseCallback, error -> {
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

    public void loadMorePostedContent(Context context, int page, Response.Listener<String> responseCallback) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = Utils.generateUrlWithFilters(context.getString(R.string.base_content_url) + "page/" + page, new String[]{}, false, 0, true);
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.GET, url, responseCallback, error -> {
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

    public void loadLikedContent(Context context, Response.Listener<String> responseCallback, Response.ErrorListener errorCallback) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = Utils.generateUrlWithFilters(context.getString(R.string.first_content_page), new String[]{}, true, 0, false);
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.GET, url, responseCallback, error -> {
            Log.e("!", error.toString());
            if (error.networkResponse != null) {
                if (error.networkResponse.statusCode == 404) {
                    errorCallback.onErrorResponse(error);
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

    public void loadMoreLikedContent(Context context, int page, Response.Listener<String> responseCallback) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = Utils.generateUrlWithFilters(context.getString(R.string.base_content_url) + "page/" + page, new String[]{}, true, 0, false);
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.GET, url, responseCallback, error -> {
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
