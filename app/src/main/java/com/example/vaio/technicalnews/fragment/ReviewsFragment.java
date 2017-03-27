package com.example.vaio.technicalnews.fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;

import com.example.vaio.technicalnews.R;
import com.example.vaio.technicalnews.activity.YoutubePlayerActivity;
import com.example.vaio.technicalnews.adapter.NewsClipHomeAdapter;
import com.example.vaio.technicalnews.database.MyDatabase;
import com.example.vaio.technicalnews.model.NewsClipItem;
import com.example.vaio.technicalnews.parser.NewsClipParser;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.google.android.youtube.player.YouTubePlayerView;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by vaio on 2/5/2017.
 */

public class ReviewsFragment extends Fragment {
    public static final String API_KEY = "AIzaSyDJHxrgp8rmu145DeiFcVaLvmQyZ-GQOeM";

    public static final int WHAT_RECEIVE_NEWS_CLIP_DATA = 0;
    public static final String LINK_CHANEL = "https://www.youtube.com/user/CNETTV/videos?shelf_id=0&view=0&sort=dd";
    public static final String YOUTUBE_ID = "youtubeId";
    public static final String ARR_CLIPS_DATA = "arrClipsData";
    private RecyclerView recyclerView;
    private NewsClipHomeAdapter newsClipHomeAdapter;
    private ArrayList<NewsClipItem> arrNewsClipItem = new ArrayList<>();
    private ContentLoadingProgressBar contentLoadingProgressBar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private android.app.FragmentManager fragmentManager;


    public ReviewsFragment(android.app.FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        inflater = LayoutInflater.from(getContext());

        View view = inflater.inflate(R.layout.fragment_reviews, container, false);
        initViews(view);
        getDataFromWeb();
        return view;
    }

    private void getDataFromWeb() {
        NewsClipParser newsClipParser = new NewsClipParser(getContext());
        newsClipParser.setOnReciveData(new NewsClipParser.OnReciveData() {
            @Override
            public void onReceive(ArrayList<NewsClipItem> arrNewsClipItem) {
                arrNewsClipItem.clear();
                arrNewsClipItem.addAll(arrNewsClipItem);
                newsClipHomeAdapter.notifyDataSetChanged();
                if (contentLoadingProgressBar != null && contentLoadingProgressBar.isShown()) {
                    contentLoadingProgressBar.hide();
                }
            }
        });
        newsClipParser.execute(LINK_CHANEL);

    }

    private void initViews(View view) {
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getDataFromWeb();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 1000);
            }
        });
        contentLoadingProgressBar = (ContentLoadingProgressBar) view.findViewById(R.id.contentNewsLoading);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
//        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        newsClipHomeAdapter = new NewsClipHomeAdapter(getContext(), arrNewsClipItem);
        recyclerView.setAdapter(newsClipHomeAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        newsClipHomeAdapter.setOnItemClickListener(new NewsClipHomeAdapter.OnItemClickListener() {
            @Override
            public void onClick(View itemView, final int position) {
                Intent intent = new Intent(getContext(), YoutubePlayerActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList(ARR_CLIPS_DATA, arrNewsClipItem);
                bundle.putString(YOUTUBE_ID, arrNewsClipItem.get(position).getClipLink());
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }
}
