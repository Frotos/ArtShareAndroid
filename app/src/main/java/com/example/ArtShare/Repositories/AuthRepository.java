package com.example.ArtShare.Repositories;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.ArtShare.Activities.LoginActivity;
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
import java.util.Timer;
import java.util.TimerTask;

public class AuthRepository {
    private final int MAX_RECONNECT_TRIES = 5;
    private static int reconnectTries = 0;

    public void register(Context context, User user, Response.Listener<JSONObject> responseCallback) {
        JSONObject jsonObject = null;

        try {
            jsonObject = new JSONObject(Utils.convertToJSON(user));
        } catch (JSONException exception) {
            Log.e(this.getClass().getName(), exception.getMessage());
        }

        RequestQueue queue = Volley.newRequestQueue(context);
        String url = context.getString(R.string.base_register_url);
        if (jsonObject != null) {
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(com.android.volley.Request.Method.POST, url, jsonObject, responseCallback, error -> ((AppCompatActivity) context).runOnUiThread(() -> {
                if (error.networkResponse != null) {
                    Gson gson = new GsonBuilder().serializeNulls().create();
                    ErrorModel errorModel = gson.fromJson(new String(error.networkResponse.data), ErrorModel.class);
                    Toast.makeText(context, errorModel.Message, Toast.LENGTH_LONG).show();
                }
            }));

            queue.add(jsonObjectRequest);
        }
    }

    public void login(Context context, User user, Response.Listener<JSONObject> responseCallback) {
        JSONObject jsonObject = null;

        try {
            jsonObject = new JSONObject(Utils.convertToJSON(user));
        } catch (JSONException exception) {
            Log.e(this.getClass().getName(), exception.getMessage());
        }

        RequestQueue queue = Volley.newRequestQueue(context);
        String url = context.getString(R.string.base_login_url);
        if (jsonObject != null) {
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(com.android.volley.Request.Method.POST, url, jsonObject, responseCallback, error -> ((LoginActivity)context).runOnUiThread(() -> {
                if (error.networkResponse != null) {
                    Gson gson = new GsonBuilder().serializeNulls().create();
                    ErrorModel errorModel = gson.fromJson(new String(error.networkResponse.data), ErrorModel.class);
                    Toast.makeText(context, errorModel.Message, Toast.LENGTH_LONG).show();
                }
            }));

            queue.add(jsonObjectRequest);
        }
    }

    public void loginBySession(Context context, String sessionId, Response.Listener<JSONObject> responseCallback) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = context.getString(R.string.base_login_url);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(com.android.volley.Request.Method.POST, url, new JSONObject(), responseCallback, error -> new Timer().schedule(new TimerTask() {
             @Override
             public void run() {
                 if (reconnectTries == MAX_RECONNECT_TRIES) {
                     return;
                 }

                 loginBySession(context, sessionId, responseCallback);
                 reconnectTries++;
             }
         },
    5000))
        {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", String.valueOf(sessionId));
                return params;
            }
        };

        queue.add(jsonObjectRequest);
    }

    public void logout(Context context, Response.Listener<String> responseCallback) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = context.getString(R.string.base_logout_url);

        StringRequest request = new StringRequest(Request.Method.DELETE, url, responseCallback, error -> {
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
}
