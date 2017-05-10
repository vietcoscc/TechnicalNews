package com.example.vaio.technicalnews.activity;

import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vaio.technicalnews.R;
import com.example.vaio.technicalnews.model.application.AccountManager;
import com.example.vaio.technicalnews.model.forum.ChildForumItem;
import com.example.vaio.technicalnews.model.forum.Comment;
import com.example.vaio.technicalnews.model.application.Emoji;
import com.example.vaio.technicalnews.model.application.FireBaseReference;
import com.example.vaio.technicalnews.model.application.GlobalData;
import com.example.vaio.technicalnews.model.forum.GroupForumItem;
import com.example.vaio.technicalnews.model.forum.Topic;
import com.example.vaio.technicalnews.model.forum.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;

import static com.example.vaio.technicalnews.fragment.ForumFragment.CHILD_FORUM_ITEM;
import static com.example.vaio.technicalnews.fragment.ForumFragment.GROUP_FORUM_ITEM;

/**
 * Created by vaio on 12/28/2016.
 */

public class PostActivity extends AppCompatActivity {
    private static final int REQUEST_CODE = 1;
    private static final String TAG = "PostActivity";
    private ImageView ivAvatar;
    private TextView tvName;
    private EditText edtContent;
    private EditText edtSubject;

    private FloatingActionButton fab;
    private Toolbar toolbar;
    private TextView tvPost;

    private GroupForumItem groupForumItem;
    private ChildForumItem childForumItem;
    //    private String groupForumKey;
//    private int childForumPosition;
    private AccountManager accountManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        groupForumItem = (GroupForumItem) getIntent().getExtras().getSerializable(GROUP_FORUM_ITEM);
        childForumItem = (ChildForumItem) getIntent().getExtras().getSerializable(CHILD_FORUM_ITEM);
//        groupForumKey = getIntent().getExtras().getString(ForumFragment.GROUP_FORUM_KEY);
//        childForumPosition = getIntent().getExtras().getInt(ForumFragment.CHILD_FORUM_POSITION);
        Log.e(TAG, groupForumItem.getName());
        Log.e(TAG, childForumItem.getName());
        Log.e(TAG, childForumItem.getPosition() + "");
//        Log.e(TAG, groupForumKey);
        GlobalData globalData = (GlobalData) getApplication();
        accountManager = globalData.getAccountManager();
        initToolbar();
        initViews();
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initViews() {


        ivAvatar = (ImageView) findViewById(R.id.ivAvatar);
        tvName = (TextView) findViewById(R.id.tvName);

//        Picasso.with(this).load(accountManager.getPathPhoto()).into(ivAvatar);
        tvName.setText(accountManager.getCurrentUser().getDisplayName());

        edtContent = (EditText) findViewById(R.id.edtContent);
        edtSubject = (EditText) findViewById(R.id.edtSubject);

        tvPost = (TextView) findViewById(R.id.tvPost);
        tvPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionPost();
            }
        });
        fab = (FloatingActionButton) findViewById(R.id.fabDone);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionPost();
            }
        });
    }

    private void actionPost() {

        String content = Emoji.replaceInText(edtContent.getText().toString()).trim();
        String subject = Emoji.replaceInText(edtSubject.getText().toString()).trim();
        if (content.isEmpty() || subject.isEmpty()) {
            return;
        }
        Calendar calendar = Calendar.getInstance();

        String date = calendar.get(Calendar.DAY_OF_MONTH) + "/" + (calendar.get(Calendar.MONTH) + 1) + "/" + calendar.get(Calendar.YEAR) + "";
        String time;
        if (calendar.get(Calendar.AM_PM) == 1) {
            time = calendar.get(Calendar.HOUR) + ":" + calendar.get(Calendar.MINUTE) + ":" + calendar.get(Calendar.SECOND) + " PM";
        } else {
            time = calendar.get(Calendar.HOUR) + ":" + calendar.get(Calendar.MINUTE) + ":" + calendar.get(Calendar.SECOND) + " AM";
        }
        String email = accountManager.getCurrentUser().getEmail();
        String name = accountManager.getCurrentUser().getDisplayName();

        FireBaseReference.getChildForumItemRef(groupForumItem.getName(), childForumItem.getName()).
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        long count = dataSnapshot.getChildrenCount();
                        Log.e(TAG, count + "");
                        FireBaseReference.getPostNumberRef(groupForumItem.getKey(), childForumItem.getPosition()).setValue(count + "");
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
        ArrayList<Comment> arrComment = new ArrayList<>();
        Topic topic = new Topic(accountManager.getCurrentUser().getUid(), subject, content, date, time, 0, 0, 0, arrComment);
        FireBaseReference.getChildForumItemRef(groupForumItem.getName(), childForumItem.getName()).push().setValue(topic);
        onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (REQUEST_CODE == requestCode) {

                ClipData clipData = data.getClipData();
                if (clipData != null) {
                    Log.e(TAG, clipData.getItemCount() + "");
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        ClipData.Item item = clipData.getItemAt(i);
                        Uri uri = item.getUri();
                        Toast.makeText(this, uri.toString(), Toast.LENGTH_SHORT).show();

                    }
                } else {
                    Uri uri = data.getData();
                    Toast.makeText(this, uri.toString(), Toast.LENGTH_SHORT).show();
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
