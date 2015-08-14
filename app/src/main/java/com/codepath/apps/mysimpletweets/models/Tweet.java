package com.codepath.apps.mysimpletweets.models;

import android.text.format.DateUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Locale;

public class Tweet implements Serializable {
    private static final long serialVersionUID = -3460511703342121892L;
    private User user;
    private String body;
    private String createdAt;
    private long id;

    public User getUser() {
        return user;
    }

    public String getBody() {
        return body;
    }

    public String getCreatedAt() {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeTime;

        try {
            long created = sf.parse(createdAt).getTime();
            long current = System.currentTimeMillis();
            long measure = DateUtils.SECOND_IN_MILLIS;
            relativeTime = DateUtils.getRelativeTimeSpanString(created, current, measure).toString();
        } catch (ParseException e) {
            e.printStackTrace();
            relativeTime = "";
        }

        return relativeTime;
    }

    public long getId() {
        return id;
    }

    public static Tweet fromJSON(JSONObject jsonObject) {
        Tweet tweet = new Tweet();

        try {
            tweet.id = jsonObject.getLong("id");
            tweet.createdAt = jsonObject.getString("created_at");
            tweet.body = jsonObject.getString("text");
            tweet.user = User.fromJSON(jsonObject.getJSONObject("user"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return tweet;
    }

    public static ArrayList<Tweet> fromJSONArray(JSONArray jsonArray) {
        ArrayList<Tweet> tweets = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject tweetJSON = jsonArray.getJSONObject(i);
                tweets.add(Tweet.fromJSON(tweetJSON));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return tweets;
    }

}
