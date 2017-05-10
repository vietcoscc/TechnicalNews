package com.example.vaio.technicalnews.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.vaio.technicalnews.R;
import com.example.vaio.technicalnews.activity.CommentActivity;
import com.example.vaio.technicalnews.adapter.forum.TopicsPostedForumAdapter;
import com.example.vaio.technicalnews.model.application.AccountManager;
import com.example.vaio.technicalnews.model.application.FireBaseReference;
import com.example.vaio.technicalnews.model.forum.Topic;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

import static com.example.vaio.technicalnews.activity.PostedActivity.RC_COMMENT;
import static com.example.vaio.technicalnews.fragment.ForumFragment.CHILD_FORUM_ITEM;
import static com.example.vaio.technicalnews.fragment.ForumFragment.GROUP_FORUM_ITEM;
import static com.example.vaio.technicalnews.model.application.FireBaseReference.TOPIC;

/**
 * Created by vaio on 30/03/2017.
 */

public class DeletedFragment extends Fragment {
    private AccountManager accountManager;
    private RecyclerView recyclerView;
    private ArrayList<Topic> arrTopic = new ArrayList<>();
    private TopicsPostedForumAdapter adapter;
    private TextView tvEmpty;

    public DeletedFragment(AccountManager accountManager) {
        this.accountManager = accountManager;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(container.getContext()).inflate(R.layout.fragment_deleted, container, false);
        initViews(view);
        receiveData();
        return view;
    }

    private void initViews(View view) {
        tvEmpty = (TextView) view.findViewById(R.id.tvEmpty);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        adapter = new TopicsPostedForumAdapter(arrTopic, accountManager);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
        adapter.setOnDeletePost(new TopicsPostedForumAdapter.OnDeletePost() {
            @Override
            public void onDelete(int position) {
                FireBaseReference.getDeletedRef().child(accountManager.getCurrentUser().getUid()).child(arrTopic.get(position).getKey()).removeValue(new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        receiveData();
                    }
                });
            }
        });
        adapter.setClickListener(new TopicsPostedForumAdapter.ClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent2 = new Intent(getContext(), CommentActivity.class);
                intent2.putExtra(GROUP_FORUM_ITEM, arrTopic.get(position).getGroupName());
                intent2.putExtra(CHILD_FORUM_ITEM, arrTopic.get(position).getChildName());
                intent2.putExtra(TOPIC, arrTopic.get(arrTopic.size() - position - 1));
                startActivityForResult(intent2, RC_COMMENT);
            }
        });
    }

    private void receiveData() {

        arrTopic.clear();
        FireBaseReference.getDeletedRef().child(accountManager.getCurrentUser().getUid()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Topic topic = dataSnapshot.getValue(Topic.class);
                topic.setKey(dataSnapshot.getKey());
                arrTopic.add(topic);
                adapter.notifyDataSetChanged();
                if (arrTopic.isEmpty()) {
                    tvEmpty.setVisibility(View.VISIBLE);
                } else {
                    tvEmpty.setVisibility(View.INVISIBLE);
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
