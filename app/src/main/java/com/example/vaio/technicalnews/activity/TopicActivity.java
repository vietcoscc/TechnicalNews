package com.example.vaio.technicalnews.activity;

import android.app.ProgressDialog;
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
import static com.example.vaio.technicalnews.model.application.FireBaseReference.NUMBER_CARE;
import static com.example.vaio.technicalnews.model.application.FireBaseReference.NUMBER_VIEW;
import static com.example.vaio.technicalnews.model.application.FireBaseReference.TOPIC;
import static com.example.vaio.technicalnews.model.application.FireBaseReference.getChildForumItemRef;

public class TopicActivity extends AppCompatActivity implements MenuItem.OnMenuItemClickListener {
    public static final String TAG = "TopicActivity";
    public static final int RC_COMMENT = 0;
    public static final String UID = "uid";

    private ArrayList<Topic> arrTopic = new ArrayList<>();
    private ArrayList<Topic> arrTopicTmp = new ArrayList<>();
    private ArrayList<UserInfo> arrUserInfo = new ArrayList<>();
//    private ArrayList<UserInfo> arrUserInfoTmp = new ArrayList<>();

    private TopicsForumAdapter adapter;

    private RecyclerView recyclerView;
    private SwipeRefreshLayout srl;
    private ContentLoadingProgressBar loadingProgressBar;
    private FloatingActionButton fab;
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
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic);
        initData();
        initToolbar();
        initViews();
    }

    private void initViews() {
        dialog = new ProgressDialog(this);
        dialog.setProgressStyle(android.R.style.Widget_DeviceDefault_Light_ProgressBar_Horizontal);
        tvEmpty = (TextView) findViewById(R.id.tvEmpty);
        srl = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        srl.setRefreshing(false);
                    }
                }, 1000);
            }
        });

        fab = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (accountManager.getUserInfo().isBanned()) {
                    Snackbar.make(v, "You have been banned !", 2000).show();
                    return;
                }
                Intent intent = new Intent(TopicActivity.this, PostActivity.class);
                intent.putExtra(GROUP_FORUM_ITEM, groupForumItem);
                intent.putExtra(CHILD_FORUM_ITEM, childForumItem);
                startActivity(intent);
            }
        });
    }

    private void initData() {
        final GlobalData globalData = (GlobalData) getApplication();
        accountManager = globalData.getAccountManager();
        groupForumItem = (GroupForumItem) getIntent().getExtras().getSerializable(GROUP_FORUM_ITEM);
        childForumItem = (ChildForumItem) getIntent().getExtras().getSerializable(CHILD_FORUM_ITEM);
        receiveData(0);
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(childForumItem.getName());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initComponent() {


        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        adapter = new TopicsForumAdapter(arrTopic, accountManager);
        adapter.setOnCompleteLoading(new TopicsForumAdapter.OnCompleteLoading() {
            @Override
            public void onComplete() {
                loadingProgressBar.hide();
            }
        });
        adapter.setClickListener(new TopicsForumAdapter.ClickListener() {
            @Override
            public void onItemClick(final View view, final int position) {
                if (accountManager.getUserInfo().isBanned()) {
                    Snackbar.make(view, "You have been banned !", 2000).show();
                    return;
                }
                Intent intent = new Intent(TopicActivity.this, CommentActivity.class);
                intent.putExtra(GROUP_FORUM_ITEM, groupForumItem.getName());
                intent.putExtra(CHILD_FORUM_ITEM, childForumItem.getName());
                intent.putExtra(TOPIC, arrTopic.get(position));
                dialog.show();
                startActivityForResult(intent, RC_COMMENT);
            }
        });
        adapter.setOnItemLongClick(new TopicsForumAdapter.OnItemLongClick() {
            @Override
            public void onLongLick(View view, final int position) {

                if (accountManager.getCurrentUser() == null) {
                    return;
                }
                final Topic topic = arrTopic.get(position);
                final PopupMenu popupMenu = new PopupMenu(TopicActivity.this, view);
                popupMenu.getMenuInflater().inflate(R.menu.menu_topic_more, popupMenu.getMenu());
                MenuItem menuItem = popupMenu.getMenu().findItem(R.id.action_ban);
                MenuItem menuItem2 = popupMenu.getMenu().findItem(R.id.action_delete);
                MenuItem menuItem3 = popupMenu.getMenu().findItem(R.id.action_view_profile);
                if (!accountManager.getUserInfo().isAdmin()) {
                    menuItem.setVisible(false);
                    if (!accountManager.getUserInfo().getUid().equals(topic.getUid())) {
                        menuItem2.setVisible(false);
                    }
                }

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_delete:
                                if (!accountManager.getUserInfo().isAdmin()) {
                                    return false;
                                }
                                FireBaseReference.getTopicKeyRef(topic.getGroupName(), topic.getChildName(), topic.getKey()).removeValue(new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                        FireBaseReference.getDeletedRef().child(accountManager.getCurrentUser().getUid()).push().setValue(topic);
                                    }
                                });
                                break;
                            case R.id.action_view_profile:
                                Intent intent = new Intent(TopicActivity.this, ProfileActivity.class);
                                intent.putExtra("tag", TAG);
                                intent.putExtra(UID, topic.getUid());
                                startActivity(intent);
                                break;
                            case R.id.action_ban:
                                if (!topic.getUid().equals(accountManager.getCurrentUser().getUid())) {
                                    FireBaseReference.getAccountRef().child(topic.getUid()).child(FireBaseReference.BAN).setValue(true);
                                }
                                break;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });
        recyclerView.setAdapter(adapter);

        loadingProgressBar = (ContentLoadingProgressBar) findViewById(R.id.contentLoadingProgressBar);

    }

    private ChildEventListener newChildEvent = new ChildEventListener() {
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
                if (arrTopic.get(i).getKey().equals(dataSnapshot.getKey())) {
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
    };
    private ValueEventListener newValueEvent = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

            loadingProgressBar.hide();
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
    };

    @Override
    protected void onStop() {
        super.onStop();
        Log.e(TAG, "Stop");
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    public void receiveData(int flag) {
        Log.e(TAG, arrTopic.size() + "");
        initComponent();
        if (loadingProgressBar != null && !loadingProgressBar.isShown()) {
            loadingProgressBar.show();
        }
        arrTopic.clear();
        arrTopicTmp.clear();
        arrUserInfo.clear();
        getChildForumItemRef(groupForumItem.getName(), childForumItem.getName()).
                removeEventListener(newValueEvent);
        getChildForumItemRef(groupForumItem.getName(), childForumItem.getName()).
                removeEventListener(newChildEvent);
        getChildForumItemRef(groupForumItem.getName(), childForumItem.getName())
                .orderByChild(NUMBER_VIEW).removeEventListener(newValueEvent);
        getChildForumItemRef(groupForumItem.getName(), childForumItem.getName())
                .orderByChild(NUMBER_VIEW).removeEventListener(newChildEvent);
        getChildForumItemRef(groupForumItem.getName(), childForumItem.getName())
                .orderByChild(NUMBER_CARE).removeEventListener(newValueEvent);
        getChildForumItemRef(groupForumItem.getName(), childForumItem.getName())
                .orderByChild(NUMBER_CARE).removeEventListener(newChildEvent);
        switch (flag) {
            case 0:
                getChildForumItemRef(groupForumItem.getName(), childForumItem.getName()).keepSynced(true);
                getChildForumItemRef(groupForumItem.getName(), childForumItem.getName())
                        .addChildEventListener(newChildEvent);
                getChildForumItemRef(groupForumItem.getName(), childForumItem.getName())
                        .addListenerForSingleValueEvent(newValueEvent);
                break;
            case 1:
                getChildForumItemRef(groupForumItem.getName(), childForumItem.getName())
                        .orderByChild(NUMBER_CARE).keepSynced(true);
                getChildForumItemRef(groupForumItem.getName(), childForumItem.getName())
                        .orderByChild(NUMBER_CARE).addChildEventListener(newChildEvent);
                getChildForumItemRef(groupForumItem.getName(), childForumItem.getName())
                        .orderByChild(NUMBER_CARE).addListenerForSingleValueEvent(newValueEvent);
                break;
            case 2:
                getChildForumItemRef(groupForumItem.getName(), childForumItem.getName())
                        .orderByChild("numberView").keepSynced(true);
                getChildForumItemRef(groupForumItem.getName(), childForumItem.getName())
                        .orderByChild("numberView").addChildEventListener(newChildEvent);
                getChildForumItemRef(groupForumItem.getName(), childForumItem.getName())
                        .orderByChild("numberView").addListenerForSingleValueEvent(newValueEvent);
                break;
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_topic, menu);
        MenuItem itemFilterNew = menu.findItem(R.id.action_filter_new);
        MenuItem itemFilterFavorite = menu.findItem(R.id.action_filter_favorite);
        MenuItem itemFilterView = menu.findItem(R.id.action_filter_view);
        itemFilterNew.setOnMenuItemClickListener(this);
        itemFilterFavorite.setOnMenuItemClickListener(this);
        itemFilterView.setOnMenuItemClickListener(this);
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

                if (newText.isEmpty() || searchView.isIconified()) {
                    arrTopic.clear();
                    arrTopic.addAll(arrTopicTmp);
                    adapter.notifyDataSetChanged();
                    return true;
                }
                loadingProgressBar.show();

                TopicSearching topicSearching = new TopicSearching(newText);
                topicSearching.setOnSearchingComplete(new TopicSearching.OnSearchingComplete() {
                    @Override
                    public void onComplete(ArrayList<Topic> arrTopic) {
                        TopicActivity.this.arrTopic.clear();
                        TopicActivity.this.arrTopic.addAll(arrTopic);
                        adapter.notifyDataSetChanged();
                        loadingProgressBar.hide();
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
    protected void onPause() {
        super.onPause();
        dialog.show();
        Log.e(TAG, "Pause");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
        try {
            if (RC_COMMENT == requestCode) {
                if (resultCode == RESULT_OK) {

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

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_filter_new:
                receiveData(0);
                break;
            case R.id.action_filter_favorite:
                receiveData(1);
                break;
            case R.id.action_filter_view:
                receiveData(2);
                break;
        }
        return false;
    }
}
