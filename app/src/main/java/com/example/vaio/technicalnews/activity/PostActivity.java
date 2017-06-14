package com.example.vaio.technicalnews.activity;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vaio.technicalnews.R;
import com.example.vaio.technicalnews.model.application.AccountManager;
import com.example.vaio.technicalnews.model.application.MyCalendar;
import com.example.vaio.technicalnews.model.forum.ChildForumItem;
import com.example.vaio.technicalnews.model.forum.Comment;
import com.example.vaio.technicalnews.model.application.Emoji;
import com.example.vaio.technicalnews.model.application.FireBaseReference;
import com.example.vaio.technicalnews.model.application.GlobalData;
import com.example.vaio.technicalnews.model.forum.GroupForumItem;
import com.example.vaio.technicalnews.model.forum.Topic;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

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
        try {
            groupForumItem = (GroupForumItem) getIntent().getExtras().getSerializable(GROUP_FORUM_ITEM);
            childForumItem = (ChildForumItem) getIntent().getExtras().getSerializable(CHILD_FORUM_ITEM);
//        groupForumKey = getIntent().getExtras().getString(ForumFragment.GROUP_FORUM_KEY);
//        childForumPosition = getIntent().getExtras().getInt(ForumFragment.CHILD_FORUM_POSITION);
            GlobalData globalData = (GlobalData) getApplication();
            accountManager = globalData.getAccountManager();
            initToolbar();
            initViews();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initToolbar() throws Exception {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initViews() throws Exception {


        ivAvatar = (ImageView) findViewById(R.id.ivAvatar);
        tvName = (TextView) findViewById(R.id.tvName);
        if (!accountManager.getUserInfo().getPhotoUrl().isEmpty()) {
            Picasso.with(this).load(accountManager.getUserInfo().getPhotoUrl()).into(ivAvatar);
        } else {
            Picasso.with(this).load(R.drawable.boss).into(ivAvatar);
        }
        tvName.setText(accountManager.getCurrentUser().getDisplayName());

        fab = (FloatingActionButton) findViewById(R.id.fabDone);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_CODE);

            }
        });
        edtContent = (EditText) findViewById(R.id.edtContent);
        edtContent.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    fab.setVisibility(View.VISIBLE);
                }
            }
        });
        edtSubject = (EditText) findViewById(R.id.edtSubject);
        edtSubject.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    fab.setVisibility(View.GONE);
                }
            }
        });
        tvPost = (TextView) findViewById(R.id.tvPost);
        tvPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (accountManager.getUserInfo().isBanned()) {
                    Snackbar.make(v, "You have been banned !", 2000);
                    return;
                }
                try {
                    actionPost();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void actionPost() throws Exception {

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
        FireBaseReference.getChildForumItemRef(groupForumItem.getName(), childForumItem.getName()).
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        try {
                            long count = dataSnapshot.getChildrenCount();
                            Log.e(TAG, count + "");
                            FireBaseReference.getPostNumberRef(groupForumItem.getKey(), childForumItem.getPosition()).setValue(count + "");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
        ArrayList<Comment> arrComment = new ArrayList<>();
        Comment comment = new Comment(accountManager.getCurrentUser().getUid(), "Comment !", date, time, new ArrayList<Comment>());
        arrComment.add(comment);
        ArrayList<String> arrFavorite = new ArrayList<>();
        arrFavorite.add(" ");
        Topic topic = new Topic(accountManager.getCurrentUser().getUid(), subject, content, date, time, 0, 0, 0, arrComment, arrFavorite);
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

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (REQUEST_CODE == requestCode) {
                if (data == null) {
                    return;
                }
                Uri uri = data.getData();
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
                Bitmap bitmap1 = ThumbnailUtils.
                        extractThumbnail(bitmap, windowManager.getDefaultDisplay().getWidth(), windowManager.getDefaultDisplay().getWidth() * bitmap.getHeight() / bitmap.getWidth());

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap1.compress(Bitmap.CompressFormat.JPEG, 30, baos);

                byte[] bytes = baos.toByteArray();
                StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Forum/" + accountManager.getUserInfo().getUid() + "_" + new Random().nextInt(10000));
                final ProgressDialog dialog = new ProgressDialog(this);
                dialog.setMessage("Rendering ... ");
                dialog.show();
                final UploadTask uploadTask = storageReference.putBytes(bytes);
                uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        dialog.dismiss();
                        Toast.makeText(PostActivity.this, "Complete", Toast.LENGTH_SHORT).show();
                        edtContent.setText(edtContent.getText() + "\n<div><img src=\"" + uploadTask.getResult().getDownloadUrl().toString() + "\"/></div>\n");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        dialog.dismiss();
                        Toast.makeText(PostActivity.this, "Failure", Toast.LENGTH_SHORT).show();
                    }
                });
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
