package com.example.businformapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONArray;

public class JsonDataManager {
    private SharedPreferences sharePref;
    private SharedPreferences.Editor editor;

    public JsonDataManager(Context mContext) {
        sharePref = mContext.getSharedPreferences("SHARE_PREF", Context.MODE_PRIVATE);
    }

    public JSONArray getData(String key) {
        JSONArray jsonArray = null;
        try {
            String shareData = sharePref.getString(key, null);

            jsonArray = new JSONArray(shareData);
        } catch (Exception e) {
            Log.e("JSON", e.toString());
        }

        return jsonArray;
    }

    public void setData(JSONArray jsonArray, String key) {
        editor = null;
        try {
            editor = sharePref.edit();
            editor.putString(key, jsonArray.toString());
            editor.apply();
        } catch (Exception e) {
            Log.e("JSON", e.toString());
        }
    }
}
