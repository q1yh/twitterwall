/*
 * Copyright (c) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.androidtv.twitterwall.ui;

import android.app.Activity;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

import com.androidtv.twitterwall.R;
import com.androidtv.twitterwall.adapter.TweetsWallAdapter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

import twitter4j.JSONArray;
import twitter4j.JSONObject;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.TwitterObjectFactory;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Loads TwitterWallFragment.
 */
public class TwitterWallActivity extends Activity {

    private List<Status> tweetList = new ArrayList<>();
    private RecyclerView recyclerView;
    private TweetsWallAdapter mAdapter;
    private final static String CONSUMER_KEY = "";
    private final static String CONSUMER_SECRET = "";
    private final static String ACCESS_TOKEN = "-";
    private final static String ACCESS_TOKEN_SECRET = "";

    /**
     * Called when the activity is created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_twittermasonry);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        mAdapter = new TweetsWallAdapter(this,tweetList);
        RecyclerView.LayoutManager mLayoutManager = new StaggeredGridLayoutManager(5, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(mAdapter);

        //prepareTweetsData();
        new Thread(netPrepareTweetsData).start();
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            String tJAS = data.getString("tweetsJSONArrayStr");
            //Log.d("vu",vu);
            if (tJAS!=null) {
                try {
                    JSONArray tweets = new JSONArray(tJAS);
                    for (int i=0;i<tweets.length();i++) {
                        tweetList.add(TwitterObjectFactory.createStatus(tweets.get(i).toString()));
                    }
                    mAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                    Log.e("Construct Tweets", "Failed");
                }
            }
        }
    };

    Runnable netPrepareTweetsData = new Runnable() {
        @Override
        public void run() {
            try {
                //String js = Utils.fetchJSONString(epVideoUrl);
                String tJAStr = prepareTweetsData();
                Message msg = new Message();
                Bundle data = new Bundle();
                data.putString("tweetsJSONArrayStr",tJAStr);
                msg.setData(data);
                handler.sendMessage(msg);
            } catch (Exception e) {
                Log.e("Fetch Tweets", "Failed");
                e.printStackTrace();
            }
        }
    };

    private String prepareTweetsData() {
        StringBuilder _tweetList = new StringBuilder();
        _tweetList.append("[");
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setJSONStoreEnabled(true)
                .setOAuthConsumerKey(CONSUMER_KEY)
                .setOAuthConsumerSecret(CONSUMER_SECRET)
                .setOAuthAccessToken(ACCESS_TOKEN)
                .setOAuthAccessTokenSecret(ACCESS_TOKEN_SECRET);
        TwitterFactory tf = new TwitterFactory(cb.build());
        Twitter twitter = tf.getInstance();
        //TwitterFactory twitterFactory = new TwitterFactory();
        //twitter = twitterFactory.getSingleton();
        Query query = new Query("lang:en F1 exclude:retweets");
        query.setCount(99);
        try {
            QueryResult result = twitter.search(query);
            for (Status status : result.getTweets()) {
                _tweetList.append(TwitterObjectFactory.getRawJSON(status));
                _tweetList.append(",");
            }
        } catch (Exception e) {
            Log.d("search twitter","failed");
        } finally {
            if (_tweetList.length()>1) {
                _tweetList.deleteCharAt(_tweetList.length()-1);
            }
            _tweetList.append("]");
            return _tweetList.toString();
        }
    }
}
