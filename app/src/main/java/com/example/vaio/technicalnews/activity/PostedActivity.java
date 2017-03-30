package com.example.vaio.technicalnews.activity;

import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vaio.technicalnews.R;
import com.example.vaio.technicalnews.adapter.TopicsForumAdapter;
import com.example.vaio.technicalnews.adapter.TopicsPostedForumAdapter;
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
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.example.vaio.technicalnews.fragment.ForumFragment.CHILD_FORUM_ITEM;
import static com.example.vaio.technicalnews.fragment.ForumFragment.GROUP_FORUM_ITEM;
import static com.example.vaio.technicalnews.fragment.ForumFragment.RC;
import static com.example.vaio.technicalnews.fragment.ForumFragment.TAG;
import static com.example.vaio.technicalnews.model.FireBaseReference.MAIL;
import static com.example.vaio.technicalnews.model.FireBaseReference.TOPIC;
import static com.example.vaio.technicalnews.model.FireBaseReference.TOPIC_KEY;

public class PostedActivity extends AppCompatActivity {
    private static final String TAG = "PostedActivity";
    private static final int RC_COMMENT = 0;
    private ArrayList<Topic> arrTopic = new ArrayList<>();
    private ArrayList<GroupForumItem> arrGroupForumItem = new ArrayList<>();
    private ArrayList<Topic> arrTopicTmp = new ArrayList<>();
    private TopicsPostedForumAdapter adapter;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ContentLoadingProgressBar contentLoadingProgressBar;
    private SearchView searchView;
    private String tag;
    private String email;
    private AccountManager accountManager;
    private TextView tvEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posted);
        initData();
        initToolbar();
        initComponent();
    }

    private void initData() {
        GlobalData globalData = (GlobalData) getApplication();
        accountManager = globalData.getAccountManager();
        email = getIntent().getExtras().getString(MAIL);
        receiveData();
        Log.e(TAG, email);
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Posted");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initComponent() {
        tvEmpty = (TextView) findViewById(R.id.tvEmpty);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        adapter = new TopicsPostedForumAdapter(arrTopic, accountManager);
        adapter.setOnCompleteLoading(new TopicsPostedForumAdapter.OnCompleteLoading() {
            @Override
            public void onComplete() {
                contentLoadingProgressBar.hide();
            }
        });
        adapter.setClickListener(new TopicsPostedForumAdapter.ClickListener() {
            @Override
            public void onItemClick(final View view, final int position) {
                onClickItem(position);
            }
        });
        adapter.setOnDeletePost(new TopicsPostedForumAdapter.OnDeletePost() {
            @Override
            public void onDelete() {
                receiveData();
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

    }

    private void onClickItem(int position) {
        Intent intent2 = new Intent(PostedActivity.this, CommentActivity.class);
        intent2.putExtra(GROUP_FORUM_ITEM, arrTopic.get(position).getGroupName());
        intent2.putExtra(CHILD_FORUM_ITEM, arrTopic.get(position).getChildName());
        intent2.putExtra(TOPIC, arrTopic.get(arrTopic.size() - position - 1));
        startActivityForResult(intent2, RC_COMMENT);
    }

    public void receiveData() {
        arrGroupForumItem.clear();
        arrTopic.clear();
        arrTopicTmp.clear();
        FireBaseReference.getForumRef().addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                GroupForumItem groupForumItem = dataSnapshot.getValue(GroupForumItem.class);
                groupForumItem.setKey(dataSnapshot.getKey());
                Log.e(TAG, groupForumItem.getName());
                arrGroupForumItem.add(groupForumItem);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        FireBaseReference.getForumRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (int i = 0; i < arrGroupForumItem.size(); i++) {
                    final GroupForumItem groupForumItem = arrGroupForumItem.get(i);
                    ArrayList<ChildForumItem> arrChildForumItem = arrGroupForumItem.get(i).getArrChildForumItem();
                    Log.e(TAG, arrChildForumItem.size() + "");
                    for (int j = 0; j < arrChildForumItem.size(); j++) {
                        final ChildForumItem childForumItem = arrChildForumItem.get(j);
                        childForumItem.setPosition(j);
                        Log.e(TAG, childForumItem.getName());
                        FireBaseReference.getChildForumItemRef(groupForumItem.getName(), childForumItem.getName()).
                                addChildEventListener(new ChildEventListener() {
                                    @Override
                                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                        Topic topic = dataSnapshot.getValue(Topic.class);
                                        if (topic.getMail().trim().equals(accountManager.getCurrentUser().getEmail().trim())) {
                                            //
                                            topic.setGroupName(groupForumItem.getName());
                                            topic.setChildName(childForumItem.getName());
                                            topic.setKey(dataSnapshot.getKey());
                                            //
                                            arrTopic.add(topic);
                                            arrTopicTmp.add(topic);
                                            adapter.notifyDataSetChanged();
                                        }
                                    }

                                    @Override
                                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                                    }

                                    @Override
                                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                                    }

                                    @Override
                                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        Log.e(TAG, arrTopic.size() + "");
        arrTopic.clear();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_topic, menu);
        MenuItem itemSearch = menu.findItem(R.id.action_search);
        searchView = (SearchView) itemSearch.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.e(TAG, newText);
                arrTopic.clear();
                for (int i = 0; i < arrTopicTmp.size(); i++) {
                    Topic topic = arrTopicTmp.get(i);
                    String s = topic.getName() + "_" + topic.getSubject() + "_" + topic.getDate() + "+" + topic.getTime();

                    if (s.toLowerCase().contains(newText.toLowerCase())) {
                        Log.e(TAG, s);
                        arrTopic.add(topic);
                    }
                }
                adapter.notifyDataSetChanged();
                return true;
            }
        });
        return true;
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
        try {
            if (RC_COMMENT == requestCode) {
                if (resultCode == RESULT_OK) {
                    receiveData();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        super.onBackPressed();
    }
}