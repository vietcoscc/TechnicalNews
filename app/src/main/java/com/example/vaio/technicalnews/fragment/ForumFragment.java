package com.example.vaio.technicalnews.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
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
import android.support.v4.widget.SwipeRefreshLayout;
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
import com.example.vaio.technicalnews.activity.CommentActivity;
import com.example.vaio.technicalnews.activity.MainActivity;
import com.example.vaio.technicalnews.adapter.TopicsForumAdapter;
import com.example.vaio.technicalnews.model.Topic;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

import static com.example.vaio.technicalnews.activity.MainActivity.TYPE_1;

/**
 * Created by vaio on 12/22/2016.
 */

public class ForumFragment extends android.support.v4.app.Fragment {
    public static final int WHAT_COMPLETELY = 0;
    private Context context;
    private DatabaseReference databaseReference;
    private ArrayList<Topic> arrTopic;
    private ArrayList<String> arrTopicKey;

    private TopicsForumAdapter adapter;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ContentLoadingProgressBar contentLoadingProgressBar;

    public ForumFragment(final Context context, DatabaseReference databaseReference) {
        arrTopicKey = new ArrayList<>();
        arrTopic = new ArrayList<>();
        this.context = context;
        this.databaseReference = databaseReference;
        receiveData();
    }

    public void receiveData() {
        arrTopic.clear();
        arrTopicKey.clear();


        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Topic topic = dataSnapshot.getValue(Topic.class);
                arrTopic.add(topic);
                arrTopicKey.add(dataSnapshot.getKey());
                Toast.makeText(context, dataSnapshot.getKey(), Toast.LENGTH_SHORT).show();
                notifyData();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                receiveData();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        databaseReference.removeEventListener(childEventListener);
        databaseReference.child(MainActivity.TOPIC).child(TYPE_1).addChildEventListener(childEventListener);
    }

    private void initComponent(View view) {
        contentLoadingProgressBar = (ContentLoadingProgressBar) view.findViewById(R.id.contentLoadingProgressBar);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                receiveData();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 1000);
            }
        });
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
                Intent intent = new Intent(context, CommentActivity.class);
                startActivity(intent);
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
