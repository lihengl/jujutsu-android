package com.lihengl.apps.jujutsu.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lihengl.apps.jujutsu.R;
import com.lihengl.apps.jujutsu.activities.ProfileActivity;
import com.lihengl.apps.jujutsu.models.Tweet;
import com.squareup.picasso.Picasso;

import java.util.List;

public class TweetsArrayAdapter extends ArrayAdapter<Tweet> {

    public TweetsArrayAdapter(Context context, List<Tweet> tweets) {
        super(context, android.R.layout.simple_list_item_1);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Tweet tweet = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_tweet, parent, false);
        }

        ImageView ivProfileImage = (ImageView) convertView.findViewById(R.id.ivProfileImage);
        ivProfileImage.setImageResource(android.R.color.transparent);
        ivProfileImage.setTag(tweet.getUser().getScreenName());
        ivProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), ProfileActivity.class);
                i.putExtra("screen_name", (String) v.getTag());
                getContext().startActivity(i);
            }
        });
        Picasso.with(getContext()).load(tweet.getUser().getProfileImageUrl()).into(ivProfileImage);

        TextView tvScreenName = (TextView) convertView.findViewById(R.id.tvScreenName);
        tvScreenName.setText("@" + tweet.getUser().getScreenName());

        TextView tvUsername = (TextView) convertView.findViewById(R.id.tvUsername);
        tvUsername.setText(tweet.getUser().getName());

        TextView tvCreatedAt = (TextView) convertView.findViewById(R.id.tvCreatedAt);
        tvCreatedAt.setText(tweet.getCreatedAt());

        TextView tvBody = (TextView) convertView.findViewById(R.id.tvBody);
        tvBody.setText(tweet.getBody());

        return convertView;
    }
}
