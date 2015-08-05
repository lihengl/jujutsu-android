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
    private ImageResultsAdapter aImageResults;
    private String queryText = "";
    private SearchFilter searchFilter;

    private String searchUrl(int start, SearchFilter filter) {
        ArrayList<String> queryParams = new ArrayList<>();

        queryParams.add(String.format("as_sitesearch=%s", filter.site));
        queryParams.add(String.format("imgcolor=%s", filter.color));
        queryParams.add(String.format("imgsize=%s", filter.size));
        queryParams.add(String.format("imgtype=%s", filter.style));
        queryParams.add(String.format("v=%s", "1.0"));
        queryParams.add(String.format("q=%s", queryText));
        queryParams.add(String.format("rsz=%d", 8));
        queryParams.add(String.format("start=%d", start));

        String apiEndpoint = "https://ajax.googleapis.com/ajax/services/search/images?";

        return apiEndpoint + TextUtils.join("&", queryParams);
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
        client.get(searchUrl(0, searchFilter), new JsonHttpResponseHandler() {
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
        client.get(searchUrl(offset, searchFilter), new JsonHttpResponseHandler() {
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

        setupViews();

        searchFilter = new SearchFilter();
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
                queryText = query;

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(MainActivity.this.getCurrentFocus().getWindowToken(), 0);

                startSearch();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.filter_color) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            EditFilterDialogue dialogue = EditFilterDialogue.newInstance(searchFilter);
            dialogue.show(fragmentManager, "Edit Filter");
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
