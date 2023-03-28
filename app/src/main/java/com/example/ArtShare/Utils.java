package com.example.ArtShare;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Utils {

    public static String convertToJSON(Object object){
        Gson gson = new GsonBuilder().serializeNulls().create();
        return  gson.toJson(object);
    }

    public static void saveLocale(Context context, String locale) {
        SharedPreferences sPref = context.getSharedPreferences("locale", MODE_PRIVATE);
        SharedPreferences.Editor editor = sPref.edit();
        editor.putString("Locale", locale);
        editor.apply();
    }

    public static String loadLocale(Context context) {
        SharedPreferences sPref = context.getSharedPreferences("locale", MODE_PRIVATE);

        return sPref.getString("Locale", "en");
    }

    public static void saveSessionId(Context context, String sessionId) {
        SharedPreferences sPref = context.getSharedPreferences("authorization", MODE_PRIVATE);
        SharedPreferences.Editor editor = sPref.edit();
        editor.putString("SessionID", sessionId);
        editor.apply();
    }

    public static String loadSessionId(Context context) {
        SharedPreferences sPref = context.getSharedPreferences("authorization", MODE_PRIVATE);

        return sPref.getString("SessionID", "");
    }

    public static String generateUrlWithFilters(String baseUrl, String[] params, boolean favourites, int userId, boolean uploadedByUser) {
        StringBuilder baseUrlBuilder = new StringBuilder(baseUrl);
        if (params.length > 0) {
            for (int i = 0; i < params.length; i ++) {
                baseUrlBuilder.append(i == 0 ? "?" : "&").append("SearchPhrases=").append(params[i]);
            }
        }
        baseUrlBuilder.append(params.length == 0 ? "?" : "&").append("Favourites=").append(favourites);
        baseUrlBuilder.append("&AuthorId=").append(userId);
        baseUrlBuilder.append("&UploadedByUser=").append(uploadedByUser);
        baseUrl = baseUrlBuilder.toString();
        return baseUrl;
    }
}
