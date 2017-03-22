package com.example.vaio.technicalnews.activity;

import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vaio.technicalnews.R;
import com.example.vaio.technicalnews.model.AccountManager;
import com.example.vaio.technicalnews.model.ChildForumItem;
import com.example.vaio.technicalnews.model.GlobalData;
import com.example.vaio.technicalnews.model.GroupForumItem;
import com.example.vaio.technicalnews.model.Topic;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
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
    private Spinner spinnerType;
    private EditText edtContent;
    private EditText edtSubject;
    private FloatingActionButton fab;
    private Toolbar toolbar;
    private TextView tvPost;
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    private GroupForumItem groupForumItem;
    private ChildForumItem childForumItem;
    private AccountManager accountManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        groupForumItem = (GroupForumItem) getIntent().getExtras().getSerializable(GROUP_FORUM_ITEM);
        childForumItem = (ChildForumItem) getIntent().getExtras().getSerializable(CHILD_FORUM_ITEM);
        Log.e(TAG, groupForumItem.getName());
        Log.e(TAG, childForumItem.getName());
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
        Picasso.with(this).load(accountManager.getPathPhoto()).into(ivAvatar);
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

        String content = edtContent.getText().toString();
        String subject = edtSubject.getText().toString();
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
        ArrayList<String> arrComment = new ArrayList<String>();
        arrComment.add("NQV");
        Topic topic = new Topic(subject, content, date, time, 0, 0, 0, email, name, arrComment,accountManager.getPathPhoto());

        reference.child(MainActivity.TOPIC).child(groupForumItem.getName()).child(childForumItem.getName()).push().setValue(topic);
        finish();
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
    }
}
