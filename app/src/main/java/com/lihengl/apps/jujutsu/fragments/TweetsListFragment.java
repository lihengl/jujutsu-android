package com.lihengl.apps.jujutsu.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.lihengl.apps.jujutsu.adapters.TweetsArrayAdapter;
import com.lihengl.apps.jujutsu.models.Tweet;

import java.util.ArrayList;

public class TweetsListFragment extends Fragment {

    private ArrayList<Tweet> tweets;
    private TweetsArrayAdapter aTweet;

    public ListView lvTweets;
    public SwipeRefreshLayout srContainer;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(com.lihengl.apps.jujutsu.R.layout.fragment_tweets_list, container, false);

        lvTweets = (ListView) v.findViewById(com.lihengl.apps.jujutsu.R.id.lvTweets);
        lvTweets.setAdapter(aTweet);

        srContainer = (SwipeRefreshLayout) v.findViewById(com.lihengl.apps.jujutsu.R.id.srContainer);
        srContainer.setColorSchemeResources(com.lihengl.apps.jujutsu.R.color.twitter_default);

        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tweets = new ArrayList<>();
        aTweet = new TweetsArrayAdapter(getActivity(), tweets);
    }

    public void addAll(ArrayList<Tweet> tweets) {
        aTweet.addAll(tweets);
    }

    public void insert(Tweet tweet) {
        aTweet.insert(tweet, 0);
    }

}
