package com.codepath.apps.mysimpletweets.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.TwitterApplication;
import com.codepath.apps.mysimpletweets.TwitterClient;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONObject;


public class ComposeActivity extends AppCompatActivity {
    private static final int MAX_COUNT = 140;

    private TwitterClient client;
    private EditText etStatus;
    private TextView tvCount;

    private final TextWatcher characterCounter = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            return;
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            return;
        }

        @Override
        public void afterTextChanged(Editable s) {
            tvCount.setText(String.valueOf(MAX_COUNT - s.length()));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        client = TwitterApplication.getRestClient();

        tvCount = (TextView) findViewById(R.id.tvCount);
        tvCount.setText(String.valueOf(MAX_COUNT));

        etStatus = (EditText) findViewById(R.id.etStatus);
        etStatus.addTextChangedListener(characterCounter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_compose, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_done) {
            String status = etStatus.getText().toString();
            client.postTweet(status, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    Intent i = new Intent();
                    i.putExtra("tweet", Tweet.fromJSON(response));
                    setResult(RESULT_OK, i);
                    ComposeActivity.this.finish();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Toast.makeText(ComposeActivity.this, "Post failed", Toast.LENGTH_LONG);
                    ComposeActivity.this.finish();
                }
            });
        }

        return super.onOptionsItemSelected(item);
    }

}
