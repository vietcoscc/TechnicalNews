package com.example.vaio.technicalnews.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.example.vaio.technicalnews.R;
import com.example.vaio.technicalnews.activity.CommentActivity;
import com.example.vaio.technicalnews.activity.SplashScreenActivity;
import com.example.vaio.technicalnews.activity.TopicActivity;
import com.example.vaio.technicalnews.model.application.FireBaseReference;
import com.example.vaio.technicalnews.model.application.MyNotification;
import com.example.vaio.technicalnews.model.application.MySharedPreferences;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

import static com.example.vaio.technicalnews.fragment.ForumFragment.CHILD_FORUM_ITEM;
import static com.example.vaio.technicalnews.fragment.ForumFragment.GROUP_FORUM_ITEM;
import static com.example.vaio.technicalnews.fragment.ForumFragment.RC;
import static com.example.vaio.technicalnews.model.application.FireBaseReference.MAIL;
import static com.example.vaio.technicalnews.model.application.FireBaseReference.TOPIC;
import static com.example.vaio.technicalnews.model.application.MySharedPreferences.USER_NAME;

/**
 * Created by vaio on 30/03/2017.
 */

public class NotificationService extends Service {
    private static final String TAG = "NotificationService";
    private String userName;
    private ArrayList<MyNotification> arrNotification = new ArrayList<>();
    private int position = 0;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (userName == null) {
            userName = MySharedPreferences.getString(this, USER_NAME);
        }
        if (userName != null) {
            receiveData();
        }

        return START_STICKY;
    }

    private void receiveData() {
        if (userName == null) {
            return;
        }
        position = 0;
        arrNotification.clear();
        FireBaseReference.getNotifocationRef().addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                MyNotification myNotification = dataSnapshot.getValue(MyNotification.class);
                myNotification.setKey(dataSnapshot.getKey());
                Log.e(TAG, "On child added : " + myNotification.getFrom());
//                Toast.makeText(NotificationService.this, myNotification.getTopicName(), Toast.LENGTH_SHORT).show();
                if (myNotification.getTo().equals(userName)) {
                    myNotification.setPosition(position);
                    position++;
                    arrNotification.add(myNotification);

//                    Intent topicActivity = new Intent(NotificationService.this, TopicActivity.class);
//                    topicActivity.putExtra(RC, TAG);
//                    topicActivity.putExtra(GROUP_FORUM_ITEM, myNotification.getGroup());
//                    topicActivity.putExtra(CHILD_FORUM_ITEM, myNotification.getChild());
//                    topicActivity.putExtra(MAIL, "");
//
//                    Intent commentActivity = new Intent(NotificationService.this, CommentActivity.class);
//                    commentActivity.putExtra(GROUP_FORUM_ITEM, myNotification.getGroup().getName());
//                    commentActivity.putExtra(CHILD_FORUM_ITEM, myNotification.getChild().getName());
//                    commentActivity.putExtra(TOPIC, myNotification.getTopic());
//
//                    android.support.v4.app.TaskStackBuilder task = android.support.v4.app.TaskStackBuilder.create(NotificationService.this);
//                    task.addNextIntentWithParentStack(topicActivity);
//                    task.addNextIntentWithParentStack(commentActivity);
//                    PendingIntent pending = task.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                    NotificationCompat.Builder build = (NotificationCompat.Builder) new NotificationCompat.Builder(NotificationService.this).
                            setSmallIcon(R.drawable.news).
                            setContentInfo(myNotification.getTopic().getSubject()).
                            setContentTitle(myNotification.getFromName()).
                            setContentText(myNotification.getContentComment());

                    NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    manager.notify(myNotification.getPosition(), build.build());
                    FireBaseReference.getNotifocationRef().child(myNotification.getKey()).removeValue(new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
//                            Toast.makeText(NotificationService.this, "removed", Toast.LENGTH_SHORT).show();
                        }
                    });
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

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
