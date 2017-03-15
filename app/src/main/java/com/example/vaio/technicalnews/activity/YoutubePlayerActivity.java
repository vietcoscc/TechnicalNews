package com.example.vaio.technicalnews.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.vaio.technicalnews.R;
import com.example.vaio.technicalnews.adapter.NewsClipHomeAdapter;
import com.example.vaio.technicalnews.fragment.ReviewsFragment;
import com.example.vaio.technicalnews.model.NewsClipItem;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.google.android.youtube.player.YouTubePlayerView;

import java.util.ArrayList;

/**
 * Created by vaio on 27/02/2017.
 */

public class YoutubePlayerActivity extends YouTubeBaseActivity {

    private static final String TAG = "YoutubePlayerActivity";
    private YouTubePlayerView youTubePlayerView;
    private Bundle bundleData;
    private ArrayList<NewsClipItem> arrNewsClipItem;
    private RecyclerView recyclerView;
    private YouTubePlayer youTubePlayer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube_player);
        prepairData();

        playClip();
        initViews();
    }

    private void prepairData() {
        bundleData = getIntent().getExtras();
        arrNewsClipItem = bundleData.getParcelableArrayList(ReviewsFragment.ARR_CLIPS_DATA);
    }

    private void playClip() {
        final String youtubeId = bundleData.getString(ReviewsFragment.YOUTUBE_ID);
        youTubePlayerView = (YouTubePlayerView) findViewById(R.id.youtubePlayerView);
        youTubePlayerView.initialize(ReviewsFragment.API_KEY, new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                YoutubePlayerActivity.this.youTubePlayer = youTubePlayer;
                Toast.makeText(YoutubePlayerActivity.this, youtubeId, Toast.LENGTH_SHORT).show();
                youTubePlayer.loadVideo(youtubeId);
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

            }
        });
    }

    private void initViews() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        NewsClipHomeAdapter newsClipHomeAdapter = new NewsClipHomeAdapter(this, arrNewsClipItem);
        recyclerView.setAdapter(newsClipHomeAdapter);
        newsClipHomeAdapter.setOnItemClickListener(new NewsClipHomeAdapter.OnItemClickListener() {
            @Override
            public void onClick(View itemView, int position) {
                youTubePlayer.loadVideo(arrNewsClipItem.get(position).getClipLink());
            }
        });
    }
}
