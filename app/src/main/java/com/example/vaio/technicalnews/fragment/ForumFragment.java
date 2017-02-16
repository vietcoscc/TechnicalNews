package com.example.vaio.technicalnews.fragment;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.service.notification.StatusBarNotification;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.transition.Transition;
import android.support.transition.TransitionValues;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.vaio.technicalnews.R;
import com.example.vaio.technicalnews.activity.MainActivity;
import com.example.vaio.technicalnews.adapter.TopicsForumAdapter;
import com.example.vaio.technicalnews.model.Topic;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

/**
 * Created by vaio on 12/22/2016.
 */

public class ForumFragment extends Fragment {
    public static final int WHAT_COMPLETELY = 0;
    private Context context;
    private DatabaseReference databaseReference;
    private ArrayList<Topic> arrTopic;
    private ArrayList<String> arrTopicKey;

    private TopicsForumAdapter adapter;
    private RecyclerView recyclerView;

    private ContentLoadingProgressBar contentLoadingProgressBar;
    private WindowManager windowManager;


    public ForumFragment(Context context, DatabaseReference databaseReference, ArrayList<Topic> arrTopic, ArrayList<String> arrTopicKey, WindowManager windowManager) {
        this.arrTopic = arrTopic;
        this.context = context;
        this.windowManager = windowManager;
        this.databaseReference = databaseReference;
        this.arrTopicKey = arrTopicKey;
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
            public void onItemClick(final View view, final int position) {

            }
        });
        recyclerView.setAdapter(adapter);
        return view;
    }

    public void notifyData() {
        if (adapter != null) {
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
