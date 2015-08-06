package com.lihengl.jujutsu.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.GridView;

import com.lihengl.jujutsu.R;
import com.lihengl.jujutsu.adapters.ImageResultsAdapter;
import com.lihengl.jujutsu.dialogues.EditFilterDialogue;
import com.lihengl.jujutsu.listeners.InfiniteScrollListener;
import com.lihengl.jujutsu.models.ImageResult;
import com.lihengl.jujutsu.models.SearchFilter;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements EditFilterDialogue.FilterApplyListener {

    private GridView gvResults;
    private ArrayList<ImageResult> imageResults;
    private ArrayList<SearchFilter> searchFilters;
    private ImageResultsAdapter aImageResults;
    private String queryText = "";

    private String searchUrl(int start) {
        ArrayList<String> queryParams = new ArrayList<>();

        queryParams.add(String.format("v=%s", "1.0"));
        queryParams.add(String.format("q=%s", queryText));
        queryParams.add(String.format("rsz=%d", 8));
        queryParams.add(String.format("start=%d", start));

        for (int i = 0; i < searchFilters.size(); i++) {
            SearchFilter sf = searchFilters.get(i);
            String queryParam = String.format("%s=%s", sf.name, sf.value());
            queryParams.add(queryParam);
        }

        String apiEndpoint = "https://ajax.googleapis.com/ajax/services/search/images?";

        return apiEndpoint + TextUtils.join("&", queryParams);
    }

    private void populateFilters() {
        searchFilters = new ArrayList<>();

        SearchFilter colorFilter = new SearchFilter("color", "imgcolor");
        colorFilter.options.add("black");
        colorFilter.options.add("blue");
        colorFilter.options.add("brown");
        colorFilter.options.add("gray");
        colorFilter.options.add("green");
        colorFilter.options.add("orange");
        colorFilter.options.add("pink");
        colorFilter.options.add("purple");
        colorFilter.options.add("red");
        colorFilter.options.add("teal");
        colorFilter.options.add("white");
        colorFilter.options.add("yellow");
        searchFilters.add(colorFilter);

        SearchFilter sizeFilter = new SearchFilter("size", "imgsize");
        sizeFilter.options.add("icon");
        sizeFilter.options.add("small");
        sizeFilter.options.add("medium");
        sizeFilter.options.add("large");
        sizeFilter.options.add("xlarge");
        sizeFilter.options.add("xxlarge");
        sizeFilter.options.add("huge");
        searchFilters.add(sizeFilter);

        SearchFilter typeFilter = new SearchFilter("type", "imgtype");
        typeFilter.options.add("face");
        typeFilter.options.add("photo");
        typeFilter.options.add("clipart");
        typeFilter.options.add("lineart");
        searchFilters.add(typeFilter);
    }

    private void setupViews() {
        gvResults = (GridView) findViewById(R.id.gvResults);
        gvResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(MainActivity.this, ImageDisplayActivity.class);
                ImageResult result = imageResults.get(position);
                i.putExtra("result", result);
                startActivity(i);
            }
        });
        gvResults.setOnScrollListener(new InfiniteScrollListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                loadMoreImages(6);
            }
        });
    }

    private void startSearch() {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(searchUrl(0), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                JSONArray imageResultsJson = null;
                try {
                    imageResultsJson = response.getJSONObject("responseData").getJSONArray("results");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                imageResults.clear();
                aImageResults.addAll(ImageResult.fromJSONArray(imageResultsJson));
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e("API", responseString, throwable);
            }
        });
    }

    private void loadMoreImages(int offset) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(searchUrl(offset), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                JSONArray imageResultsJson = null;
                try {
                    imageResultsJson = response.getJSONObject("responseData").getJSONArray("results");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                aImageResults.addAll(ImageResult.fromJSONArray(imageResultsJson));
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e("API", responseString, throwable);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        populateFilters();
        setupViews();

        imageResults = new ArrayList<>();
        aImageResults = new ImageResultsAdapter(this, imageResults);

        gvResults.setAdapter(aImageResults);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(MainActivity.this.getCurrentFocus().getWindowToken(), 0);
                queryText = query;
                startSearch();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        for (int i = 0; i < searchFilters.size(); i++) {
            SearchFilter filter = searchFilters.get(i);
            String title = filter.title();
            String value = (filter.value() == "") ? "all" : filter.value();
            MenuItem item = menu.add(Menu.NONE, 100 + i, i, (title + ": " + value));
            item.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id >= 100 && id <= 100 + searchFilters.size()) {
            SearchFilter sf = searchFilters.get(id - 100);
            EditFilterDialogue dialogue = EditFilterDialogue.newInstance(sf);
            FragmentManager fragmentManager = getSupportFragmentManager();
            dialogue.show(fragmentManager, "Filter Editor");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onApplyFilter(SearchFilter filter) {
        if (queryText == "") {
            return;
        }
        startSearch();
    }
}
