package com.lihengl.apps.jujutsu.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.lihengl.apps.jujutsu.R;
import com.lihengl.apps.jujutsu.TwitterApplication;
import com.lihengl.apps.jujutsu.TwitterClient;
import com.lihengl.apps.jujutsu.fragments.UserTimelineFragment;
import com.lihengl.apps.jujutsu.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.json.JSONObject;

public class ProfileActivity extends AppCompatActivity {
    TwitterClient client;
    User user;

    private JsonHttpResponseHandler handler = new JsonHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            user = User.fromJSON(response);
            getSupportActionBar().setTitle("@" + user.getScreenName());
            populateProfileHeader(user);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        String screenName = getIntent().getStringExtra("screen_name");

        client = TwitterApplication.getRestClient();

        if (screenName == null) {
            client.getSelfInfo(handler);
        } else {
            client.getUserInfo(screenName, handler);
        }

        if (savedInstanceState == null) {
            UserTimelineFragment fragment = UserTimelineFragment.newInstance(screenName);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.flContainer, fragment);
            ft.commit();
        } else {
            Log.i("INFO", "ProfileActivity.onCreate() has been run already");
        }
    }

    private void populateProfileHeader(User user) {
        ImageView ivProfileImage = (ImageView) findViewById(R.id.ivProfileImage);
        Picasso.with(this).load(user.getProfileImageUrl()).into(ivProfileImage);

        TextView tvName = (TextView) findViewById(R.id.tvName);
        tvName.setText(user.getName());

        TextView tvTagline = (TextView) findViewById(R.id.tvTagline);
        tvTagline.setText(user.getTagline());

        TextView tvFollowers = (TextView) findViewById(R.id.tvFollowers);
        tvFollowers.setText(user.getFollowersCount() + " Followers");

        TextView tvFollowing = (TextView) findViewById(R.id.tvFollowing);
        tvFollowing.setText(user.getFriendsCount() + " Following");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
