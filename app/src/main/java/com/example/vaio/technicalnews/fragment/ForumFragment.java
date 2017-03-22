package com.example.vaio.technicalnews.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
import com.example.vaio.technicalnews.model.GroupForumItem;
import com.example.vaio.technicalnews.model.Topic;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.example.vaio.technicalnews.activity.MainActivity.TYPE_1;

/**
 * Created by vaio on 12/22/2016.
 */

public class ForumFragment extends Fragment {

    public static final String TAG = "ForumFragment";
    private static final String FORUM = "Forum";
    public static final String GROUP_FORUM_ITEM = "Group forum item";
    public static final String CHILD_FORUM_ITEM = "Child forum item";
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    private ExpandableListView expandableListView;
    private GroupForumExpandableListViewAdapter adapter;
    private ContentLoadingProgressBar contentLoadingProgressBar;
    private AccountManager accountManager;
    private ArrayList<GroupForumItem> arrGroupForumItem = new ArrayList<>();
    private ArrayList<String> arrGroupForumItemKey = new ArrayList<>();

    public ForumFragment(AccountManager accountManager) {
//        arrTopicKey = new ArrayList<>();
//        arrTopic = new ArrayList<>();
//        receiveData();
        this.accountManager = accountManager;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View view = layoutInflater.inflate(R.layout.fragment_forum, container, false);
//        initComponent(view);

        receiveData();
        initViews(view);
        return view;
    }

    private void initViews(final View view) {
        contentLoadingProgressBar = (ContentLoadingProgressBar) view.findViewById(R.id.contentLoadingProgressBar);
        contentLoadingProgressBar.show();
        expandableListView = (ExpandableListView) view.findViewById(R.id.expandableListView);
//        ArrayList<GroupForumItem> arrGroupForumItem = new ArrayList<>();
//        ArrayList<ChildForumItem> arrChildForumItem = new ArrayList<>();
//        arrChildForumItem.add(new ChildForumItem("Thông tin công nghệ", "0", "0"));
//        arrChildForumItem.add(new ChildForumItem("Thương mại điện tử", "0", "0"));
//        arrChildForumItem.add(new ChildForumItem("Đám mây, Dịch vụ trực tuyến", "0", "0"));
//        arrChildForumItem.add(new ChildForumItem("Thăm dò công nghệ", "0", "0"));
//        arrChildForumItem.add(new ChildForumItem("Quảng cáo - Khuyến mãi", "0", "0"));
//        arrChildForumItem.add(new ChildForumItem("App", "0", "0"));
//        GroupForumItem groupForumItem = new GroupForumItem("Thông tin - Sự kiện", arrChildForumItem);
//        arrGroupForumItem.add(groupForumItem);
//
//        ArrayList<ChildForumItem> arrChildForumItem2 = new ArrayList<>();
//        arrChildForumItem2.add(new ChildForumItem("Windows", "0", "0"));
//        arrChildForumItem2.add(new ChildForumItem("Apple - Mac OS X", "0", "0"));
//        arrChildForumItem2.add(new ChildForumItem("Linux", "0", "0"));
//        arrChildForumItem2.add(new ChildForumItem("Chrome OS", "0", "0"));
//        arrChildForumItem2.add(new ChildForumItem("Tư vấn chọn mua Máy tính", "0", "0"));
//        GroupForumItem groupForumItem2 = new GroupForumItem("Máy tính", arrChildForumItem2);
//        arrGroupForumItem.add(groupForumItem2);
//
//        ArrayList<ChildForumItem> arrChildForumItem3 = new ArrayList<>();
//        arrChildForumItem3.add(new ChildForumItem("iOS", "0", "0"));
//        arrChildForumItem3.add(new ChildForumItem("Android", "0", "0"));
//        arrChildForumItem3.add(new ChildForumItem("Windows Phone", "0", "0"));
//        arrChildForumItem3.add(new ChildForumItem("BlackBerry", "0", "0"));
//        arrChildForumItem3.add(new ChildForumItem("Symbian", "0", "0"));
//        arrChildForumItem3.add(new ChildForumItem("Mạng di động", "0", "0"));
//        GroupForumItem groupForumItem3 = new GroupForumItem("Điện thoại", arrChildForumItem3);
//        arrGroupForumItem.add(groupForumItem3);
//        for (int i = 0; i < arrGroupForumItem.size(); i++) {
//            databaseReference.child(FORUM).push().setValue(arrGroupForumItem.get(i));
//        }
        adapter = new GroupForumExpandableListViewAdapter(getContext(), arrGroupForumItem);
        expandableListView.setAdapter(adapter);
        expandableListView.setGroupIndicator(null);
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Log.e(TAG, groupPosition + " : " + childPosition);
                Intent intent = new Intent(getContext(), TopicActivity.class);
                GroupForumItem groupForumItem = arrGroupForumItem.get(groupPosition);
                ChildForumItem childForumItem = groupForumItem.getArrChildForumItem().get(childPosition);
                intent.putExtra(GROUP_FORUM_ITEM, groupForumItem);
                intent.putExtra(CHILD_FORUM_ITEM, childForumItem);
                startActivity(intent);
                return true;
            }
        });
//        for (int i = 0; i < arrGroupForumItem.size(); i++) {
//            Log.e(TAG, i + "");
//            expandableListView.expandGroup(i);
//            GroupForumItem groupForumItem_ = arrGroupForumItem.get(i);
//            FirebaseDatabase.getInstance().getReference().child(FORUM).push().setValue(groupForumItem_);
//            for (int j = 0; j < groupForumItem_.getArrChildForumItem().size(); j++) {
//                ChildForumItem childForumItem = arrChildForumItem.get(j);
//                FirebaseDatabase.getInstance().getReference().
//                        child(FORUM).
//                        child(groupForumItem_.getName()).
//                        child(childForumItem.getName()).setValue(childForumItem);
//            }
//
//
//        }
    }

    private void receiveData() {
        arrGroupForumItem.clear();
        databaseReference.child(FORUM).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                GroupForumItem groupForumItem = dataSnapshot.getValue(GroupForumItem.class);
                arrGroupForumItem.add(groupForumItem);
                arrGroupForumItemKey.add(dataSnapshot.getKey());
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
        databaseReference.child(FORUM).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                contentLoadingProgressBar.hide();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
