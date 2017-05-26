package com.example.vaio.technicalnews.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.example.vaio.technicalnews.R;
import com.example.vaio.technicalnews.activity.MainActivity;
import com.example.vaio.technicalnews.activity.TopicActivity;
import com.example.vaio.technicalnews.adapter.forum.GroupForumExpandableListViewAdapter;
import com.example.vaio.technicalnews.model.application.AccountManager;
import com.example.vaio.technicalnews.model.forum.ChildForumItem;
import com.example.vaio.technicalnews.model.application.FireBaseReference;
import com.example.vaio.technicalnews.model.forum.GroupForumItem;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;
import static com.example.vaio.technicalnews.model.application.FireBaseReference.MAIL;

/**
 * Created by vaio on 12/22/2016.
 */

public class ForumFragment extends Fragment {
    public static final int RC_POST = 0;
    public static final String TAG = "ForumFragment";
    public static final String GROUP_FORUM_ITEM = "Group forum item";
    public static final String CHILD_FORUM_ITEM = "Child forum item";
    public static final String GROUP_FORUM_KEY = "Group forum key";
    public static final String CHILD_FORUM_POSITION = "Child forum posiiton";
    public static final String RC = "code";

    private SwipeRefreshLayout swipeRefreshLayout;
    private ExpandableListView expandableListView;
    private GroupForumExpandableListViewAdapter adapter;
    private ContentLoadingProgressBar contentLoadingProgressBar;
    private AccountManager accountManager;
    private ArrayList<GroupForumItem> arrGroupForumItem = new ArrayList<>();
    private ArrayList<String> arrAdmin = new ArrayList<>();
    private ArrayList<String> arrBan = new ArrayList<>();

    public ForumFragment(AccountManager accountManager) {
        this.accountManager = accountManager;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View view = layoutInflater.inflate(R.layout.fragment_forum, container, false);
//        receiveBanList();
//        receiveAdmin();
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
        adapter = new GroupForumExpandableListViewAdapter(getContext(), arrGroupForumItem, accountManager);
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
                intent.putExtra(RC, TAG);
                intent.putExtra(GROUP_FORUM_ITEM, groupForumItem);
                intent.putExtra(CHILD_FORUM_ITEM, childForumItem);
                intent.putExtra(MAIL, "");
                Log.e(TAG, groupPosition + " : " + childForumItem.getPosition());
                startActivityForResult(intent, RC_POST);
                return true;
            }
        });
    }

    public void receiveBanList() {
        arrBan.clear();
        FireBaseReference.getBanRef().keepSynced(true);
        FireBaseReference.getBanRef().addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String mail = dataSnapshot.getValue(String.class);
                Log.e(TAG, mail);
                if (!(arrBan.indexOf(mail) > -1)) {
                    arrBan.add(mail);
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

    private void receiveAdmin() {
        arrAdmin.clear();
        FireBaseReference.getAdminRef().keepSynced(true);
        FireBaseReference.getAdminRef().addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String admin = dataSnapshot.getValue(String.class);
                arrAdmin.add(admin);
                Log.e(TAG, admin);
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
//        FireBaseReference.getAdminRef().addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
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
