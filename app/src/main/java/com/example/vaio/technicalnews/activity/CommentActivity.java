package com.example.vaio.technicalnews.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.vaio.technicalnews.R;
import com.example.vaio.technicalnews.adapter.CommentAdapter;
import com.example.vaio.technicalnews.adapter.TopicsForumAdapter;
import com.example.vaio.technicalnews.fragment.ForumFragment;
import com.example.vaio.technicalnews.model.AccountManager;
import com.example.vaio.technicalnews.model.ChildForumItem;
import com.example.vaio.technicalnews.model.Comment;
import com.example.vaio.technicalnews.model.FireBaseReference;
import com.example.vaio.technicalnews.model.GlobalData;
import com.example.vaio.technicalnews.model.GroupForumItem;
import com.example.vaio.technicalnews.model.Topic;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

import static com.example.vaio.technicalnews.model.FireBaseReference.ARR_COMMENT;
import static com.example.vaio.technicalnews.model.FireBaseReference.TOPIC;

public class CommentActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "CommentActivity";
    //

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private EditText edtComment;
    private ImageButton ibSend;
    //
    private GroupForumItem groupForumItem;
    private ChildForumItem childForumItem;
    private Topic topic;
    private String topicKey;
    //
    private AccountManager accountManager;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private CommentAdapter commentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        // data
        groupForumItem = (GroupForumItem) getIntent().getExtras().getSerializable(ForumFragment.GROUP_FORUM_ITEM);
        childForumItem = (ChildForumItem) getIntent().getExtras().getSerializable(ForumFragment.CHILD_FORUM_ITEM);
        topic = (Topic) getIntent().getExtras().getSerializable(TOPIC);
        topicKey = (String) getIntent().getExtras().getSerializable(TopicActivity.TOPIC_KEY);
        //data
        GlobalData globalData = (GlobalData) getApplication();
        accountManager = globalData.getAccountManager();
        getTopicCreate();
        initOthers();
        initViews();

    }

    private void initOthers() {
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    private void getTopicCreate() {
        FireBaseReference.getChildForumItemRef(groupForumItem.getName(), childForumItem.getName()).child(topicKey).
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Topic topicTmp = dataSnapshot.getValue(Topic.class);
                        topic.setArrComment(topicTmp.getArrComment());
                        commentAdapter.notifyDataSetChanged();
                        recyclerView.scrollToPosition(0);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void getTopicAfter() {
        FireBaseReference.getTopicKeyRef(groupForumItem.getName(), childForumItem.getName(), topicKey).
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Topic topicTmp = dataSnapshot.getValue(Topic.class);
                        topic.setArrComment(topicTmp.getArrComment());
                        commentAdapter.notifyDataSetChanged();
                        recyclerView.scrollToPosition(topic.getArrComment().size());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void initViews() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        edtComment = (EditText) findViewById(R.id.edtComment);
        edtComment.setBackground(null);
        ibSend = (ImageButton) findViewById(R.id.ibSend);
        edtComment.setOnClickListener(this);
        ibSend.setOnClickListener(this);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        commentAdapter = new CommentAdapter(groupForumItem, childForumItem, topic);
        recyclerView.setAdapter(commentAdapter);
        recyclerView.scrollToPosition(topic.getArrComment().size());
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        super.onBackPressed();
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.edtComment:


                break;
            case R.id.ibSend:

                Log.e(TAG, TOPIC);
                Log.e(TAG, groupForumItem.getName());
                Log.e(TAG, childForumItem.getName());
                Log.e(TAG, ARR_COMMENT);


                Calendar calendar = Calendar.getInstance();

                String date = calendar.get(Calendar.DAY_OF_MONTH) + "/" + (calendar.get(Calendar.MONTH) + 1) + "/" + calendar.get(Calendar.YEAR) + "";
                String time;
                if (calendar.get(Calendar.AM_PM) == 1) {
                    time = calendar.get(Calendar.HOUR) + ":" + calendar.get(Calendar.MINUTE) + ":" + calendar.get(Calendar.SECOND) + " PM";
                } else {
                    time = calendar.get(Calendar.HOUR) + ":" + calendar.get(Calendar.MINUTE) + ":" + calendar.get(Calendar.SECOND) + " AM";
                }

                String comment = edtComment.getText().toString();
                edtComment.setText("");
                Comment cmt = new Comment(accountManager.getPathPhoto(), accountManager.getCurrentUser().getDisplayName(), comment, date, time);

                ArrayList<Comment> arrComment = topic.getArrComment();
                arrComment.add(cmt);

                FireBaseReference.getArrCommentRef(groupForumItem.getName(), childForumItem.getName(), topicKey).setValue(arrComment);
                getTopicAfter();
                break;
        }
    }
}
