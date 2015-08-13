package com.codepath.apps.mysimpletweets.activities;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.codepath.apps.mysimpletweets.InfiniteScrollListener;
import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.TweetsArrayAdapter;
import com.codepath.apps.mysimpletweets.TwitterApplication;
import com.codepath.apps.mysimpletweets.TwitterClient;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class TimelineActivity extends ActionBarActivity {
    final int REQUEST_CODE = 72;

    private TwitterClient client;
    private ArrayList<Tweet> tweets;
    private TweetsArrayAdapter aTweet;
    private ListView lvTweets;
    private SwipeRefreshLayout srContainer;

    private long newestId;
    private long oldestId;

    private void populateTimeline() {
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                ArrayList<Tweet> results = Tweet.fromJSONArray(response);
                aTweet.addAll(results);
                newestId = results.get(0).getId();
                oldestId = results.get(results.size() -1).getId();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", errorResponse.toString());
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        newestId = 1;
        oldestId = 1;

        lvTweets = (ListView) findViewById(R.id.lvTweets);
        tweets = new ArrayList<>();
        aTweet = new TweetsArrayAdapter(this, tweets);

        srContainer = (SwipeRefreshLayout) findViewById(R.id.srContainer);
        srContainer.setColorSchemeResources(R.color.twitter_default);
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
                            aTweet.insert(results.get(i - 1), 0);
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

        lvTweets.setAdapter(aTweet);
        lvTweets.setOnScrollListener(new InfiniteScrollListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                client.getOlderTweets(oldestId, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                        ArrayList<Tweet> results = Tweet.fromJSONArray(response);
                        aTweet.addAll(results);
                        oldestId = results.get(results.size() - 1).getId();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        Log.d("DEBUG", errorResponse.toString());
                    }
                });
            }
        });



        client = TwitterApplication.getRestClient();
        populateTimeline();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_timeline, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_compose) {
            Intent i = new Intent(this, ComposeActivity.class);
            startActivityForResult(i, REQUEST_CODE);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            System.err.println("Unknown result code: " + resultCode);
            return;
        }
        if (requestCode != REQUEST_CODE) {
            System.err.println("Unknown request code: " + requestCode);
            return;
        }
        Tweet tweeted = (Tweet) data.getSerializableExtra("tweet");
        aTweet.insert(tweeted, 0);
        newestId = tweeted.getId();
    }

}
