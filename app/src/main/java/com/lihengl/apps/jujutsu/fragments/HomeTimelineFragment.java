package com.lihengl.apps.jujutsu.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lihengl.apps.jujutsu.InfiniteScrollListener;
import com.lihengl.apps.jujutsu.TwitterApplication;
import com.lihengl.apps.jujutsu.TwitterClient;
import com.lihengl.apps.jujutsu.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class HomeTimelineFragment extends TweetsListFragment {

    private TwitterClient client;

    private long newestId;
    private long oldestId;

    private void populateTimeline() {
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                ArrayList<Tweet> results = Tweet.fromJSONArray(response);
                addAll(results);
                newestId = results.get(0).getId();
                oldestId = results.get(results.size() - 1).getId();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", errorResponse.toString());
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);

        lvTweets.setOnScrollListener(new InfiniteScrollListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                client.getOlderTweets(oldestId, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                        ArrayList<Tweet> results = Tweet.fromJSONArray(response);
                        addAll(results);
                        oldestId = results.get(results.size() - 1).getId();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        Log.d("DEBUG", errorResponse.toString());
                    }
                });
            }
        });

        srContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                client.getNewerTweets(newestId, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                        srContainer.setRefreshing(false);
                        ArrayList<Tweet> results = Tweet.fromJSONArray(response);

                        if (results.size() < 1) {
                            return;
                        }
                        for (int i = results.size(); i > 0; i--) {
                            insert(results.get(i - 1));
                        }
                        newestId = results.get(0).getId();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        Log.d("DEBUG", errorResponse.toString());
                    }
                });
            }
        });

        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        newestId = 1;
        oldestId = 1;

        client = TwitterApplication.getRestClient();
        populateTimeline();
    }
}
