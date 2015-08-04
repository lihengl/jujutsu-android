package com.lihengl.jujutsu.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

public class ImageResult implements Serializable {
    private static final long serialVersionUID = -404559972763751944L;

    public String thumbUrl;
    public String fullUrl;
    public String title;

    public ImageResult(JSONObject json) {
        try {
            this.thumbUrl= json.getString("tbUrl");
            this.fullUrl= json.getString("url");
            this.title = json.getString("title");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<ImageResult> fromJSONArray(JSONArray array) {
        ArrayList<ImageResult> results = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            try {
                results.add(new ImageResult(array.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return results;
    }
}
