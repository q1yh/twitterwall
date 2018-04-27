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

package com.androidtv.twitterwall.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidtv.twitterwall.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import java.util.ArrayList;
import java.util.List;

import twitter4j.Status;

public class TweetsWallAdapter extends RecyclerView.Adapter<TweetsWallAdapter.TweetsWallView> {

    private Context context;
    private List<Status> tweets;

    class TweetsWallView extends RecyclerView.ViewHolder {

        CardView tweetCardOuter,tweetCardInner;
        ImageView userLogo;
        TextView userName,tweetText;
        LinearLayout tweetVisual,tweetVideoPlayButton;
        TextureView tweetVideo;

        public TweetsWallView(View itemView) {
            super(itemView);
            tweetCardOuter = (CardView) itemView.findViewById(R.id.tweet_card_outer);
            tweetCardInner = (CardView) itemView.findViewById(R.id.tweet_card_inner);
            userLogo = (ImageView) itemView.findViewById(R.id.tweet_logo);
            userName = (TextView) itemView.findViewById(R.id.tweet_username);
            tweetText = (TextView) itemView.findViewById(R.id.tweet_text);
            tweetVisual = (LinearLayout) itemView.findViewById(R.id.tweet_visual);
            tweetVideoPlayButton = (LinearLayout) itemView.findViewById(R.id.tweet_video_play_btn_layout);
            tweetVideo = (TextureView) itemView.findViewById(R.id.tweet_videoview);
            itemView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    int focus = 0;
                    if (hasFocus) {
                        focus = R.anim.zoomin;
                        tweetCardInner.setCardBackgroundColor(context.getResources().getColor(R.color.card_bg_focused));
                    } else {
                        focus = R.anim.zoomout;
                        tweetCardInner.setCardBackgroundColor(context.getResources().getColor(R.color.card_bg_unfocused));
                    }
                    Animation mAnimation = AnimationUtils.loadAnimation(
                            context, focus);
                    mAnimation.setBackgroundColor(Color.TRANSPARENT);
                    mAnimation.setFillAfter(hasFocus);
                    v.startAnimation(mAnimation);
                    mAnimation.start();
                    v.bringToFront();
                }
            });
        }
    }

    public TweetsWallAdapter(Context ctx, List<Status> statuses) {
        context=ctx;
        tweets=statuses;
    }

    @Override
    public TweetsWallView onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.tweet_card,parent,false);
        TweetsWallView tweetsWallView = new TweetsWallView(layoutView);
        return tweetsWallView;
    }

    @Override
    public void onBindViewHolder(TweetsWallView holder, int position) {
        Status tweet = tweets.get(position);
        //render userlogo
        Glide.with(context).load(tweet.getUser().getProfileImageURL()).apply(RequestOptions.circleCropTransform()).into(holder.userLogo);
        //get username
        holder.userName.setText(tweet.getUser().getName());
        //get tweet text
        holder.tweetText.setText(tweet.getText().split("http")[0]);
        //render tweet image if any
        try {
            Glide.with(context).load(tweet.getMediaEntities()[0].getMediaURL()).into(new SimpleTarget<Drawable>() {
                @Override
                public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                    holder.tweetVisual.setBackground(resource);
                }
            });
        } catch (Exception e) { // if no image, make it gone
            holder.tweetVisual.setVisibility(View.GONE);
        }
        //render tweet video view & play button if any
        try {
            tweet.getMediaEntities()[0].getVideoVariants()[0].getUrl();
            holder.tweetVideoPlayButton.setVisibility(View.VISIBLE);
            //holder.tweetVideo.setVisibility(View.VISIBLE);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Intent intent = new Intent(context, PlaybackActivity.class);
                    //intent.putExtra("Video", tweet.getMediaEntities()[0].getVideoVariants()[0].getUrl());
                    //context.startActivity(intent);
                }
            });
        } catch (Exception e) { // if no image, make it gone
            holder.tweetVideoPlayButton.setVisibility(View.GONE);
            holder.tweetVideo.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }
}