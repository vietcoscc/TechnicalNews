package com.example.vaio.technicalnews.activity;

import android.annotation.TargetApi;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.vaio.technicalnews.R;
import com.example.vaio.technicalnews.adapter.forum.CommentAdapter;
import com.example.vaio.technicalnews.adapter.forum.RelpyAdapter;
import com.example.vaio.technicalnews.fragment.ForumFragment;
import com.example.vaio.technicalnews.model.application.AccountManager;
import com.example.vaio.technicalnews.model.application.MyNotification;
import com.example.vaio.technicalnews.model.forum.ChildForumItem;
import com.example.vaio.technicalnews.model.forum.Comment;
import com.example.vaio.technicalnews.model.application.Emoji;
import com.example.vaio.technicalnews.model.application.FireBaseReference;
import com.example.vaio.technicalnews.model.application.GlobalData;
import com.example.vaio.technicalnews.model.application.MyCalendar;
import com.example.vaio.technicalnews.model.forum.GroupForumItem;
import com.example.vaio.technicalnews.model.forum.Topic;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

import static com.example.vaio.technicalnews.model.application.FireBaseReference.TOPIC;
import static com.example.vaio.technicalnews.model.application.FireBaseReference.getArrCommentRef;
import static com.example.vaio.technicalnews.model.application.FireBaseReference.getArrFavoriteRef;
import static com.example.vaio.technicalnews.model.application.FireBaseReference.getChildForumItemRef;
import static com.example.vaio.technicalnews.model.application.FireBaseReference.getNotifocationRef;
import static com.example.vaio.technicalnews.model.application.FireBaseReference.getTopicKeyRef;

public class CommentActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "CommentActivity";
    //
    private ArrayList<Comment> arrComment = new ArrayList<>();
    private ArrayList<String> arrFavorite = new ArrayList<>();
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private EditText edtComment;
    private ImageView ibSend;
    //
    private String groupForumItem;
    private String childForumItem;
    private Topic topic;
    //
    private AccountManager accountManager;
    private CommentAdapter commentAdapter;
    private boolean begin = true;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        try {
            initData();
            initToolbar();
            initViews();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void initData() {
        GlobalData globalData = (GlobalData) getApplication();
        accountManager = globalData.getAccountManager();
        groupForumItem = getIntent().getExtras().getString(ForumFragment.GROUP_FORUM_ITEM);
        childForumItem = getIntent().getExtras().getString(ForumFragment.CHILD_FORUM_ITEM);
        topic = (Topic) getIntent().getExtras().getSerializable(TOPIC);
        topic.setArrComment(arrComment);
        topic.setArrFavorite(arrFavorite);

        getArrComment();
        getArrFavorite();

        getTopicKeyRef(groupForumItem, childForumItem, topic.getKey()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    Log.e(TAG, "Data change ");
                    Topic tp = dataSnapshot.getValue(Topic.class);
                    if (tp == null) {
                        finish();
                    }
                    if (tp.getArrComment() != null) {
                        topic.setArrComment(tp.getArrComment());
                    }
                    if (tp.getArrFavorite() != null) {
                        topic.setArrFavorite(tp.getArrFavorite());
                    }
                    topic.setNumberCare(tp.getNumberCare());
                    topic.setNumberReply(tp.getNumberReply());
                    topic.setNumberView(tp.getNumberView());

                    if (commentAdapter != null) {
                        commentAdapter.notifyItemChanged(0);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    finish();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        getTopicKeyRef(groupForumItem, childForumItem, topic.getKey()).
                child(FireBaseReference.NUMBER_VIEW).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Integer count = dataSnapshot.getValue(Integer.class);
                if (count == null) {
                    return;
                }
                getTopicKeyRef(groupForumItem, childForumItem, topic.getKey()).child(FireBaseReference.NUMBER_VIEW).setValue(count + 1);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void getArrComment() {
        arrComment.clear();
        getArrCommentRef(groupForumItem, childForumItem, topic.getKey()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Comment comment = dataSnapshot.getValue(Comment.class);
                arrComment.add(comment);
                if (commentAdapter != null && arrComment != null) {
                    commentAdapter.notifyItemInserted(arrComment.size() + 1);
                }
                if (!begin) {
                    recyclerView.scrollToPosition(arrComment.size());
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Comment comment = dataSnapshot.getValue(Comment.class);
                int key = Integer.valueOf(dataSnapshot.getKey());
                commentAdapter.notifyItemChanged(key + 1);
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
        getArrCommentRef(groupForumItem, childForumItem, topic.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (begin) {
                    if (recyclerView != null) {
                        recyclerView.scrollToPosition(0);
                    }
                    begin = false;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getArrFavorite() {
        arrFavorite.clear();
        getArrFavoriteRef(topic.getGroupName(), topic.getChildName(), topic.getKey()).keepSynced(true);
        getArrFavoriteRef(topic.getGroupName(), topic.getChildName(), topic.getKey()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String string = dataSnapshot.getValue(String.class);
                arrFavorite.add(0, string);
                Log.e(TAG, "Added");
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                String string = dataSnapshot.getValue(String.class);
                arrFavorite.remove(arrFavorite.indexOf(string));
                commentAdapter.notifyItemChanged(0);
                Log.e(TAG, "removed");
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        getArrFavoriteRef(topic.getGroupName(), topic.getChildName(), topic.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                FireBaseReference.getNumberCareRef(topic.getGroupName(), topic.getChildName(), topic.getKey()).setValue(arrFavorite.size());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(childForumItem);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void initViews() throws Exception {

        edtComment = (EditText) findViewById(R.id.edtComment);
        edtComment.setBackground(null);
        ibSend = (ImageView) findViewById(R.id.ibSend);
        edtComment.setOnClickListener(this);
        ibSend.setOnClickListener(this);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        commentAdapter = new CommentAdapter(topic, accountManager);
        commentAdapter.setOnItemClick(new CommentAdapter.OnItemClick() {
            @Override
            public void onClick(View view, int positon) {

            }
        });
        recyclerView.setAdapter(commentAdapter);
//        recyclerView.smoothScrollToPosition(topic.getArrComment().size());


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
                if (accountManager.getUserInfo().isBanned()) {
                    Snackbar.make(v, "You have been banned !", 2000).show();
                    return;
                }
                if (edtComment.getText().toString().isEmpty()) {
                    return;
                }
                Calendar calendar = Calendar.getInstance();

                String date = calendar.get(Calendar.DAY_OF_MONTH) + "/" + (calendar.get(Calendar.MONTH) + 1) + "/" + calendar.get(Calendar.YEAR) + "";
                String time;
                if (calendar.get(Calendar.AM_PM) == 1) {
                    time = MyCalendar.getTimeStamp() + " PM";
                } else {
                    time = MyCalendar.getTimeStamp() + " AM";
                }
                String comment = Emoji.replaceInText(edtComment.getText().toString()).trim();

                ArrayList<Comment> arrReply = new ArrayList<>();
                Comment cmt = new Comment(accountManager.getCurrentUser().getUid(), comment, date, time, arrReply);
                getArrCommentRef(groupForumItem, childForumItem, topic.getKey()).child(arrComment.size() + "").setValue(cmt);
                if (!accountManager.getCurrentUser().getUid().equals(topic.getUid())) {
                    getNotifocationRef().push().setValue(new MyNotification(accountManager.getCurrentUser().getDisplayName(),
                            topic.getSubject(), edtComment.getText().toString(), accountManager.getCurrentUser().getUid(), topic.getUid(),
                            new GroupForumItem(null, groupForumItem, null), new ChildForumItem(0, childForumItem, "", ""), topic));
                }
                edtComment.setText("");
                try {
//                    getTopicAfter();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }
}
