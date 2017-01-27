package com.example.vaio.technicalnews.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.vaio.technicalnews.R;
import com.example.vaio.technicalnews.adapter.TopicsForumAdapter;
import com.example.vaio.technicalnews.model.Topic;

import java.util.ArrayList;

/**
 * Created by vaio on 12/22/2016.
 */

public class ForumFragment extends Fragment {
    public static final int WHAT_COMPLETELY = 0;
    private Context context;
    private TopicsForumAdapter adapter;
    private RecyclerView recyclerView;
    private ArrayList<Topic> arrTopic;
    private ContentLoadingProgressBar contentLoadingProgressBar;

    public ForumFragment(Context context, ArrayList<Topic> arrTopic) {
        this.arrTopic = arrTopic;
        this.context = context;
    }

    private void initComponent(View view) {
        contentLoadingProgressBar = (ContentLoadingProgressBar) view.findViewById(R.id.contentLoadingProgressBar);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.fragment_forum, container, false);
        initComponent(view);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(context.getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        adapter = new TopicsForumAdapter(arrTopic, handlerContentLoadingCompletely);
        adapter.setClickListener(new TopicsForumAdapter.ClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Toast.makeText(context, position + "", Toast.LENGTH_SHORT).show();
            }
        });
        recyclerView.setAdapter(adapter);
        return view;
    }

    public void notifyData() {
        if(adapter!=null) {
            adapter.notifyDataSetChanged();
        }
    }

    Handler handlerContentLoadingCompletely = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == WHAT_COMPLETELY) {
                contentLoadingProgressBar.hide();
            }
        }
    };
}
