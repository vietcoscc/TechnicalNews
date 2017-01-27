package com.example.vaio.technicalnews.fragment;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Toast;

import com.example.vaio.technicalnews.R;
import com.example.vaio.technicalnews.activity.WebContentActivity;
import com.example.vaio.technicalnews.adapter.NewsHomeAdapter;
import com.example.vaio.technicalnews.model.NewsItem;
import com.example.vaio.technicalnews.parser.NewsContentParser;

import java.util.ArrayList;
import java.util.Collection;

import static android.content.Context.ACTIVITY_SERVICE;

/**
 * Created by vaio on 12/22/2016.
 */

public class HomeFragment extends Fragment {
    public static final int WHAT_RECEIVE_DATA = 0;
    public static final int WHAT_SCROLL_POSITION_BOTTOM = 1;
    public static String linkNews = "https://www.cnet.com/news/";
    private int currentPage = 1;
    private Context context;
    private ArrayList<NewsItem> arrNewsItem = new ArrayList<>();
    private RecyclerView recyclerView;
    private NewsHomeAdapter newsHomeAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    public static final String LINK_CONTENT = "link content";

    public HomeFragment(Context context) {
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.fragment_home, container, false);
        initViews(view);
        return view;
    }

    private void initViews(final View view) {

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);

        final RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        newsHomeAdapter = new NewsHomeAdapter(arrNewsItem, handlerContentLoading);
        newsHomeAdapter.setClickListener(new NewsHomeAdapter.ClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Toast.makeText(context, position + "", Toast.LENGTH_SHORT).show();
                //
                Intent intent = new Intent(context, WebContentActivity.class);
                intent.putExtra(LINK_CONTENT, arrNewsItem.get(position).getContentLink());
                startActivity(intent);
            }
        });
        recyclerView.swapAdapter(newsHomeAdapter, true);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                arrNewsItem.clear();
                receiveNewsItem(linkNews, 1);
                notifyData();
            }
        });
        receiveNewsItem(linkNews, currentPage);
    }


    private void receiveNewsItem(String link, int page) {
        swipeRefreshLayout.setRefreshing(true);
        NewsContentParser newsContentParser = new NewsContentParser(context, handlerReceiveData);
        if (page == 1) {
            newsContentParser.execute(link);
        } else {
            newsContentParser.execute(link + page);
        }
        currentPage++;
    }

    public void notifyData() {
        if (newsHomeAdapter != null) {
            newsHomeAdapter.notifyDataSetChanged();
        }
    }

    private Handler handlerContentLoading = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == WHAT_SCROLL_POSITION_BOTTOM) {
                receiveNewsItem(linkNews, currentPage);
            }

        }
    };
    private Handler handlerReceiveData = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == WHAT_RECEIVE_DATA) {
                arrNewsItem.addAll((Collection<? extends NewsItem>) msg.obj);
                notifyData();
                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 1000);

            }
        }
    };
}
