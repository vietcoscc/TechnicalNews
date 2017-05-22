package com.example.vaio.technicalnews.activity;

import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.vaio.technicalnews.R;
import com.example.vaio.technicalnews.adapter.forum.TopicsForumAdapter;
import com.example.vaio.technicalnews.asyntask.TopicSearching;
import com.example.vaio.technicalnews.model.application.AccountManager;
import com.example.vaio.technicalnews.model.forum.ChildForumItem;
import com.example.vaio.technicalnews.model.application.FireBaseReference;
import com.example.vaio.technicalnews.model.application.GlobalData;
import com.example.vaio.technicalnews.model.forum.GroupForumItem;
import com.example.vaio.technicalnews.model.forum.Topic;
import com.example.vaio.technicalnews.model.forum.UserInfo;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.example.vaio.technicalnews.fragment.ForumFragment.CHILD_FORUM_ITEM;
import static com.example.vaio.technicalnews.fragment.ForumFragment.GROUP_FORUM_ITEM;
import static com.example.vaio.technicalnews.fragment.ForumFragment.RC;
import static com.example.vaio.technicalnews.model.application.FireBaseReference.MAIL;
import static com.example.vaio.technicalnews.model.application.FireBaseReference.TOPIC;

public class TopicActivity extends AppCompatActivity {
    private static final String TAG = "TopicActivity";
    private static final int RC_COMMENT = 0;

    private ArrayList<Topic> arrTopic = new ArrayList<>();
    private ArrayList<Topic> arrTopicTmp = new ArrayList<>();
    private ArrayList<UserInfo> arrUserInfo = new ArrayList<>();
//    private ArrayList<UserInfo> arrUserInfoTmp = new ArrayList<>();

    private TopicsForumAdapter adapter;

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ContentLoadingProgressBar contentLoadingProgressBar;
    private FloatingActionButton floatingActionButton;
    private SearchView searchView;
    //intent data
    private GroupForumItem groupForumItem;
    private ChildForumItem childForumItem;

    //intent data
    private AccountManager accountManager;
    private TextView tvEmpty;
    private String s[] = {"Information and technology", "Ecommerce", "Cloud", "Technology exploration",
            "Advertising - Promotions", "App", "Information - Events", "Windows", "Apple - Mac OS X",
            "Linux", "Chrome OS", "Computer Consulting", "Computer", "iOS", "Android", "Windows Phone",
            "BlackBerry", "Symbian", "Mobile network", "Phone"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic);
        initData();
        initToolbar();
        initComponent();
    }

    private void initData() {
        final GlobalData globalData = (GlobalData) getApplication();
        accountManager = globalData.getAccountManager();
        groupForumItem = (GroupForumItem) getIntent().getExtras().getSerializable(GROUP_FORUM_ITEM);
        childForumItem = (ChildForumItem) getIntent().getExtras().getSerializable(CHILD_FORUM_ITEM);
//        getBanList();
        receiveData();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(childForumItem.getName());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
                intent.putExtra(GROUP_FORUM_ITEM, groupForumItem.getName());
                intent.putExtra(CHILD_FORUM_ITEM, childForumItem.getName());
                intent.putExtra(TOPIC, arrTopic.get(position));
                startActivityForResult(intent, RC_COMMENT);
            }
        });
        adapter.setOnItemLongClick(new TopicsForumAdapter.OnItemLongClick() {
            @Override
            public void onLongLick(View view, final int position) {
                Log.e(TAG, position + "");

                final Topic topic = arrTopic.get(position);
                if (accountManager.getCurrentUser() == null || !accountManager.getCurrentUser().getUid().equals(topic.getUid()) && !accountManager.getUserInfo().isAdmin()) {
                    return;
                }
                final PopupMenu popupMenu = new PopupMenu(TopicActivity.this, view);
                popupMenu.getMenuInflater().inflate(R.menu.menu_topic_more, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_delete:

                                FireBaseReference.getTopicKeyRef(topic.getGroupName(), topic.getChildName(), topic.getKey()).removeValue(new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
//                                        FireBaseReference.getDeletedRef().child(accountManager.getCurrentUser().getUid()).push().setValue(topic);
                                    }
                                });
                                break;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });
        recyclerView.setAdapter(adapter);

        contentLoadingProgressBar = (ContentLoadingProgressBar) findViewById(R.id.contentLoadingProgressBar);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
//                receiveData();
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
//                if (arrBan.indexOf(accountManager.getCurrentUser().getEmail().trim()) > -1) {
//                    Snackbar.make(recyclerView, "You have been banned ! ", 1000).show();
//                    return;
//                }
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
//        arrTopicTmp.clear();
        arrUserInfo.clear();
//        arrUserInfoTmp.clear();

        FireBaseReference.getChildForumItemRef(groupForumItem.getName(), childForumItem.getName()).
                keepSynced(true);

        FireBaseReference.getChildForumItemRef(groupForumItem.getName(), childForumItem.getName()).
                addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        final Topic topic = dataSnapshot.getValue(Topic.class);
                        topic.setKey(dataSnapshot.getKey());
                        topic.setGroupName(groupForumItem.getName());
                        topic.setChildName(childForumItem.getName());

                        arrTopic.add(0, topic);
                        arrTopicTmp.add(0, topic);
                        adapter.notifyItemInserted(0);
//                        recyclerView.scrollToPosition(0);
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                        Log.e(TAG, dataSnapshot.getKey());
                        int n = arrTopic.size();
                        for (int i = 0; i < n; i++) {
                            if(arrTopic.get(i).getKey().equals(dataSnapshot.getKey())){
                                arrTopic.remove(i);
                                adapter.notifyItemRemoved(i);
                                break;
                            }
                        }

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
                            tvEmpty.setText("< No post >");
                            tvEmpty.setVisibility(View.VISIBLE);
                        } else {
                            tvEmpty.setVisibility(View.INVISIBLE);
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
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
//                arrTopic.clear();
//                for (int i = 0; i < arrTopicTmp.size(); i++) {
//                    Topic topic = arrTopicTmp.get(i);
//                    String s = topic.getName() + "_" + topic.getSubject() + "_" + topic.getDate() + "+" + topic.getTime();
//
//                    if (s.toLowerCase().trim().contains(newText.toLowerCase().trim())) {
//                        Log.e(TAG, s);
//                        arrTopic.add(topic);
//                    }
//                }
//                adapter.notifyDataSetChanged();
                if (newText.isEmpty() || searchView.isIconified()) {
                    arrTopic.clear();
                    arrTopic.addAll(arrTopicTmp);
                    adapter.notifyDataSetChanged();
                    return true;
                }


                contentLoadingProgressBar.show();

                TopicSearching topicSearching = new TopicSearching(newText);
                topicSearching.setOnSearchingComplete(new TopicSearching.OnSearchingComplete() {
                    @Override
                    public void onComplete(ArrayList<Topic> arrTopic) {
                        TopicActivity.this.arrTopic.clear();
                        TopicActivity.this.arrTopic.addAll(arrTopic);
                        adapter.notifyDataSetChanged();
                        contentLoadingProgressBar.hide();
                    }
                });
                topicSearching.execute(arrTopicTmp);
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
//                    receiveData();
//                    if (arrTopic.size() - 1 >= 0) {
//                        adapter.notifyItemInserted(arrTopic.size() - 1);
//                    }
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
