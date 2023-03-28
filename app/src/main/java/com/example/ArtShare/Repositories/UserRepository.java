package com.example.ArtShare.Repositories;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.ArtShare.Models.Responses.ErrorModel;
import com.example.ArtShare.Models.User;
import com.example.ArtShare.R;
import com.example.ArtShare.Utils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class UserRepository {
    public void getUser(Context context, Response.Listener<User> responseCallback) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = context.getString(R.string.session_user_url);
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.GET, url, response -> {
            Gson gson = new GsonBuilder().serializeNulls().create();
            User user = gson.fromJson(response, User.class);

            responseCallback.onResponse(user);
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

    public void updateUser(Context context, User user, Response.Listener<JSONObject> responseCallback) {
        JSONObject jsonObject = null;

        try {
            jsonObject = new JSONObject(Utils.convertToJSON(user));
        } catch (JSONException exception) {
            Log.e(this.getClass().getName(), exception.getMessage());
        }

        RequestQueue queue = Volley.newRequestQueue(context);
        String url = context.getString(R.string.user_url);
        if (jsonObject != null) {
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(com.android.volley.Request.Method.PUT, url, jsonObject, responseCallback, error ->  {
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
}
