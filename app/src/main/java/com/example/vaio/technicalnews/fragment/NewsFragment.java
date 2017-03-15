package com.example.vaio.technicalnews.fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.vaio.technicalnews.R;
import com.example.vaio.technicalnews.activity.WebContentActivity;
import com.example.vaio.technicalnews.adapter.NewsHomeAdapter;
import com.example.vaio.technicalnews.database.MyDatabase;
import com.example.vaio.technicalnews.model.NewsItem;
import com.example.vaio.technicalnews.parser.NewsContentParser;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by vaio on 2/5/2017.
 */

public class NewsFragment extends Fragment {

    public static final int WHAT_RECEIVE_DATA = 0;
    public static final int WHAT_SCROLL_POSITION_BOTTOM = 1;
    public static String linkNews = "https://www.cnet.com/news/";
    protected int currentPage = 1;
    protected Context context;
    protected ArrayList<NewsItem> arrNewsItem = new ArrayList<>();
    protected RecyclerView recyclerView;
    protected ContentLoadingProgressBar contentNewsLoading;
    protected NewsHomeAdapter newsHomeAdapter;
    protected SwipeRefreshLayout swipeRefreshLayout;
    public static final String LINK_CONTENT = "link content";
    private MyDatabase myDatabase;

    public NewsFragment(Context context) {

        this.context = context;
        try {
            myDatabase = new MyDatabase(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.fragment_news, container, false);

        try {
            initViews(view);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }

    protected void initViews(final View view) throws Exception {
        contentNewsLoading = (ContentLoadingProgressBar) view.findViewById(R.id.contentNewsLoading);
        //
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        final RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        newsHomeAdapter = new NewsHomeAdapter(arrNewsItem, handlerContentNewsLoading);
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
        recyclerView.setAdapter(newsHomeAdapter);
        //
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


    protected void receiveNewsItem(String link, int page) {
        NewsContentParser newsContentParser = new NewsContentParser(context, handlerReceiveNewsData);
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

    private Handler handlerContentNewsLoading = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == WHAT_SCROLL_POSITION_BOTTOM) {
                swipeRefreshLayout.setRefreshing(true);
                receiveNewsItem(linkNews, currentPage);
            }

        }
    };
    private Handler handlerReceiveNewsData = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            try {
                if (msg.what == WHAT_RECEIVE_DATA) {
                    if (msg.arg1 == NewsContentParser.FAIL) {
                        Toast.makeText(context, "Failed from connection ", Toast.LENGTH_SHORT).show();
                        swipeRefreshLayout.setRefreshing(false);
                        return;
                    }
                    if (contentNewsLoading.isShown()) {
                        contentNewsLoading.hide();
                    }
                    arrNewsItem.addAll((Collection<? extends NewsItem>) msg.obj);
                    notifyData();
                    (new Handler()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            swipeRefreshLayout.setRefreshing(false);

                            try {

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }, 1000);

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    public ArrayList<NewsItem> getArrNewsItem() {
        return arrNewsItem;
    }
}
