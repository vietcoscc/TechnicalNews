package com.example.vaio.technicalnews.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.vaio.technicalnews.R;
import com.example.vaio.technicalnews.activity.WebContentActivity;
import com.example.vaio.technicalnews.adapter.news.NewsHomeAdapter;
import com.example.vaio.technicalnews.database.MyDatabase;
import com.example.vaio.technicalnews.model.news.NewsItem;
import com.example.vaio.technicalnews.parser.NewsContentParser;

import java.util.ArrayList;

/**
 * Created by vaio on 2/5/2017.
 */

public class NewsFragment extends Fragment implements NewsContentParser.OnReceiveData {

    public static final int WHAT_RECEIVE_DATA = 0;
    public static final int WHAT_SCROLL_POSITION_BOTTOM = 1;
    private static final String TAG = "NewsFragment";
    public static String linkNews = "https://www.cnet.com/news/";
    protected int currentPage = 1;

    protected ArrayList<NewsItem> arrNewsItem = new ArrayList<>();
    protected RecyclerView recyclerView;
    protected ContentLoadingProgressBar contentNewsLoading;
    protected NewsHomeAdapter newsHomeAdapter;
    protected SwipeRefreshLayout swipeRefreshLayout;
    public static final String LINK_CONTENT = "link content";
    private MyDatabase myDatabase;

    public NewsFragment() {
        try {
            myDatabase = new MyDatabase(getContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        inflater = LayoutInflater.from(getContext());
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
        final RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(layoutManager);


        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
//                arrNewsItem.clear();
                receiveNewsItem(linkNews, 1);
                newsHomeAdapter.notifyDataSetChanged();
            }
        });

        newsHomeAdapter = new NewsHomeAdapter(arrNewsItem);
        newsHomeAdapter.setOnCompleteLoading(new NewsHomeAdapter.OnCompleteLoading() {
            @Override
            public void onSuccess() {
                swipeRefreshLayout.setRefreshing(true);
                receiveNewsItem(linkNews, currentPage);
            }
        });
        newsHomeAdapter.setClickListener(new NewsHomeAdapter.ClickListener() {
            @Override
            public void onItemClick(View view, int position) {
//                Toast.makeText(getContext(), position + "", Toast.LENGTH_SHORT).show();
                //
                Intent intent = new Intent(getContext(), WebContentActivity.class);
                intent.putExtra(LINK_CONTENT, arrNewsItem.get(position).getContentLink());
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(newsHomeAdapter);
        //

        receiveNewsItem(linkNews, currentPage);
    }


    protected void receiveNewsItem(String link, int page) {
        NewsContentParser newsContentParser = new NewsContentParser(getContext());
        newsContentParser.setOnReceiveData(this);
        if (page == 1) {
            newsContentParser.execute(link);
        } else {
            newsContentParser.execute(link + page);
        }
        currentPage++;
    }


    public ArrayList<NewsItem> getArrNewsItem() {
        return arrNewsItem;
    }

    @Override
    public void onReceive(ArrayList<NewsItem> arrNewsItem) {

        if (arrNewsItem.isEmpty()) {
            Toast.makeText(getContext(), "Failed from connection ", Toast.LENGTH_SHORT).show();
            swipeRefreshLayout.setRefreshing(false);
            return;
        }
        if (contentNewsLoading.isShown()) {
            contentNewsLoading.hide();
        }
        this.arrNewsItem.addAll(arrNewsItem);
        newsHomeAdapter.notifyDataSetChanged();
        (new Handler()).postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(false);
            }
        }, 1000);
    }


}
