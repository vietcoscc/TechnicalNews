package com.example.vaio.technicalnews.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.example.vaio.technicalnews.R;
import com.example.vaio.technicalnews.activity.CommentActivity;
import com.example.vaio.technicalnews.activity.MainActivity;
import com.example.vaio.technicalnews.activity.TopicActivity;
import com.example.vaio.technicalnews.adapter.GroupForumExpandableListViewAdapter;
import com.example.vaio.technicalnews.adapter.TopicsForumAdapter;
import com.example.vaio.technicalnews.model.AccountManager;
import com.example.vaio.technicalnews.model.ChildForumItem;
import com.example.vaio.technicalnews.model.FireBaseReference;
import com.example.vaio.technicalnews.model.GroupForumItem;
import com.example.vaio.technicalnews.model.Topic;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

/**
 * Created by vaio on 12/22/2016.
 */

public class ForumFragment extends Fragment {
    public static final int RC_POST = 0;
    public static final String TAG = "ForumFragment";

    public static final String GROUP_FORUM_ITEM = "Group forum item";
    public static final String CHILD_FORUM_ITEM = "Child forum item";
    public static final String GROUP_FORUM_KEY = "Group forum key";

    public static final String ARR_CHILD_FORUM_ITEM = "arrChildForumItem";
    public static final String CHILD_FORUM_POSITION = "Child forum posiiton";

    private SwipeRefreshLayout swipeRefreshLayout;
    private ExpandableListView expandableListView;
    private GroupForumExpandableListViewAdapter adapter;
    private ContentLoadingProgressBar contentLoadingProgressBar;
    private AccountManager accountManager;
    private ArrayList<GroupForumItem> arrGroupForumItem = new ArrayList<>();

    public ForumFragment(AccountManager accountManager) {
        this.accountManager = accountManager;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View view = layoutInflater.inflate(R.layout.fragment_forum, container, false);

        receiveData();
        initViews(view);
        return view;
    }

    private void initViews(final View view) {
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
        contentLoadingProgressBar = (ContentLoadingProgressBar) view.findViewById(R.id.contentLoadingProgressBar);
        contentLoadingProgressBar.show();
        expandableListView = (ExpandableListView) view.findViewById(R.id.expandableListView);
        adapter = new GroupForumExpandableListViewAdapter(getContext(), arrGroupForumItem);
        expandableListView.setAdapter(adapter);
        expandableListView.setGroupIndicator(null);
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                if (accountManager.getCurrentUser() == null) {
                    MainActivity mainActivity = (MainActivity) getContext();
                    mainActivity.showLoginSnackBar();
                    return false;
                }

                Intent intent = new Intent(getContext(), TopicActivity.class);
                GroupForumItem groupForumItem = arrGroupForumItem.get(groupPosition);
                ChildForumItem childForumItem = groupForumItem.getArrChildForumItem().get(childPosition);
                intent.putExtra(GROUP_FORUM_ITEM, groupForumItem);
                intent.putExtra(CHILD_FORUM_ITEM, childForumItem);

                Log.e(TAG, groupPosition + " : " + childForumItem.getPosition());
                startActivityForResult(intent, RC_POST);
                return true;
            }
        });
    }

    private void receiveData() {
        arrGroupForumItem.clear();
        FireBaseReference.getForumRef().keepSynced(true);
        FireBaseReference.getForumRef().
                addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        GroupForumItem groupForumItem = dataSnapshot.getValue(GroupForumItem.class);
                        groupForumItem.setKey(dataSnapshot.getKey());
                        arrGroupForumItem.add(groupForumItem);

                        Log.e(TAG, dataSnapshot.getKey());
                        adapter.notifyDataSetChanged();
                        expandableListView.expandGroup(arrGroupForumItem.size() - 1);
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
                contentLoadingProgressBar.hide();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_POST) {
            if (resultCode == RESULT_OK) {
                receiveData();
            }
        }
    }
}
