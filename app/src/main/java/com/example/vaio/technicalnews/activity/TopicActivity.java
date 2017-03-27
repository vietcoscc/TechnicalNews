package com.example.vaio.technicalnews.activity;

import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ShareCompat;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vaio.technicalnews.R;
import com.example.vaio.technicalnews.adapter.TopicsForumAdapter;
import com.example.vaio.technicalnews.fragment.ForumFragment;
import com.example.vaio.technicalnews.model.AccountManager;
import com.example.vaio.technicalnews.model.ChildForumItem;
import com.example.vaio.technicalnews.model.FireBaseReference;
import com.example.vaio.technicalnews.model.GlobalData;
import com.example.vaio.technicalnews.model.GroupForumItem;
import com.example.vaio.technicalnews.model.Topic;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.example.vaio.technicalnews.fragment.ForumFragment.CHILD_FORUM_ITEM;
import static com.example.vaio.technicalnews.fragment.ForumFragment.GROUP_FORUM_ITEM;
import static com.example.vaio.technicalnews.model.FireBaseReference.TOPIC;
import static com.example.vaio.technicalnews.model.FireBaseReference.TOPIC_KEY;

public class TopicActivity extends AppCompatActivity {
    private static final String TAG = "TopicActivity";


    private static final int RC_COMMENT = 0;
    private ArrayList<Topic> arrTopic = new ArrayList<>();
    private ArrayList<String> arrTopicKey = new ArrayList<>();
    private TopicsForumAdapter adapter;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ContentLoadingProgressBar contentLoadingProgressBar;
    private FloatingActionButton floatingActionButton;
    private GroupForumItem groupForumItem;
    private ChildForumItem childForumItem;
    private AccountManager accountManager;
    private TextView tvEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic);
        GlobalData globalData = (GlobalData) getApplication();
        accountManager = globalData.getAccountManager();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        groupForumItem = (GroupForumItem) getIntent().getExtras().getSerializable(GROUP_FORUM_ITEM);
        childForumItem = (ChildForumItem) getIntent().getExtras().getSerializable(CHILD_FORUM_ITEM);
        Log.e(TAG, groupForumItem.getName());
        Log.e(TAG, childForumItem.getName());
        Log.e(TAG, childForumItem.getPosition() + "");
        initComponent();
        receiveData();
    }

    private void initComponent() {
        tvEmpty = (TextView) findViewById(R.id.tvEmpty);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        adapter = new TopicsForumAdapter(arrTopic, accountManager);
        adapter.setOnCompleteLoading(new TopicsForumAdapter.OnCompleteLoading() {
            @Override
            public void onComplete() {
                contentLoadingProgressBar.hide();
            }
        });
        adapter.setClickListener(new TopicsForumAdapter.ClickListener() {
            @Override
            public void onItemClick(final View view, final int position) {
                Intent intent = new Intent(TopicActivity.this, CommentActivity.class);
                intent.putExtra(GROUP_FORUM_ITEM, groupForumItem);
                intent.putExtra(CHILD_FORUM_ITEM, childForumItem);
                intent.putExtra(TOPIC, arrTopic.get(arrTopic.size() - position - 1));
                Log.e(TAG, arrTopic.get(position).getName());
                Log.e(TAG, arrTopic.get(position).getContent());
                Log.e(TAG, arrTopic.get(position).getPhotoPath());
                intent.putExtra(TOPIC_KEY, arrTopicKey.get(arrTopicKey.size() - position - 1));
                startActivityForResult(intent, RC_COMMENT);
            }
        });
        recyclerView.setAdapter(adapter);

        contentLoadingProgressBar = (ContentLoadingProgressBar) findViewById(R.id.contentLoadingProgressBar);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
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

        floatingActionButton = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TopicActivity.this, PostActivity.class);
                intent.putExtra(GROUP_FORUM_ITEM, groupForumItem);
                intent.putExtra(CHILD_FORUM_ITEM, childForumItem);
                startActivity(intent);
            }
        });
    }

    public void receiveData() {
        Log.e(TAG, arrTopic.size() + "");
        arrTopic.clear();
        arrTopicKey.clear();
        FireBaseReference.getChildForumItemRef(groupForumItem.getName(), childForumItem.getName()).
                keepSynced(true);
        FireBaseReference.getChildForumItemRef(groupForumItem.getName(), childForumItem.getName()).
                addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        Topic topic = dataSnapshot.getValue(Topic.class);
                        arrTopic.add(topic);
                        arrTopicKey.add(dataSnapshot.getKey());
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                        receiveData();
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
                });
        FireBaseReference.getChildForumItemRef(groupForumItem.getName(), childForumItem.getName())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        contentLoadingProgressBar.hide();
                        if (arrTopic.isEmpty()) {
                            tvEmpty.setText("Empty !");
                            tvEmpty.setVisibility(View.VISIBLE);
                        }
                        Toast.makeText(TopicActivity.this, "Complete !", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (RC_COMMENT == requestCode) {
            if (resultCode == RESULT_OK) {
                receiveData();
            }
        }
    }
}
