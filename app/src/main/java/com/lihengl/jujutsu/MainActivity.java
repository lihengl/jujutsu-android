package com.lihengl.jujutsu;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import static java.lang.Long.*;


public class MainActivity extends ActionBarActivity {

    public static final String CLIENT_ID = "a08d215934244b2dabe76962220287c9";

    private SwipeRefreshLayout srPhotos;

    private ArrayList<InstagramPhoto> photos;
    private InstagramPhotosAdapter aPhotos;

    private String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time);
        String date = DateFormat.format("dd/MM/yyyy", cal).toString();
        return date;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView lvPhotos = (ListView) findViewById(R.id.lvPhotos);
        photos = new ArrayList<>();
        aPhotos = new InstagramPhotosAdapter(this, photos);
        lvPhotos.setAdapter(aPhotos);

        srPhotos = (SwipeRefreshLayout) findViewById(R.id.srPhotos);
        srPhotos.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchPopularPhotos();
            }
        });

        fetchPopularPhotos();
    }

    public void fetchPopularPhotos() {
        AsyncHttpClient client = new AsyncHttpClient();
        String popularApi = "https://api.instagram.com/v1/media/popular?client_id=" + CLIENT_ID;

        client.get(popularApi, null, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                JSONArray photosJSON = null;
                aPhotos.clear();

                try {
                    photosJSON = response.getJSONArray("data");
                    for (int i = 0; i < photosJSON.length(); i++) {
                        JSONObject photoJSON = photosJSON.getJSONObject(i);
                        InstagramPhoto photo = new InstagramPhoto();
                        photo.createdTime = getDate(1000 * parseLong(photoJSON.getString("created_time")));
                        photo.imageHeight = photoJSON.getJSONObject("images").getJSONObject("standard_resolution").getInt("height");
                        photo.imageUrl    = photoJSON.getJSONObject("images").getJSONObject("standard_resolution").getString("url");
                        photo.profileUrl  = photoJSON.getJSONObject("user").getString("profile_picture");
                        photo.username    = photoJSON.getJSONObject("user").getString("username");
                        photo.caption     = photoJSON.getJSONObject("caption").getString("text");
                        photo.likesCount  = photoJSON.getJSONObject("likes").getInt("count");
                        photos.add(photo);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                srPhotos.setRefreshing(false);
                aPhotos.notifyDataSetChanged();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e("NETWORK", responseString);
            }

        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
