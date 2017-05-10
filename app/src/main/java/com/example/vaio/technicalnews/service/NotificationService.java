package com.example.vaio.technicalnews.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;

import com.example.vaio.technicalnews.R;
import com.example.vaio.technicalnews.activity.SplashScreenActivity;
import com.example.vaio.technicalnews.model.application.FireBaseReference;
import com.example.vaio.technicalnews.model.application.MyNotification;
import com.example.vaio.technicalnews.model.application.MySharedPreferences;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

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
//                Toast.makeText(NotificationService.this, myNotification.getTopicName(), Toast.LENGTH_SHORT).show();

                if (myNotification.getTo().equals(userName)) {
                    myNotification.setPosition(position);
                    position++;
                    arrNotification.add(myNotification);
                    Intent intent = new Intent(NotificationService.this, SplashScreenActivity.class);
                    PendingIntent pending = PendingIntent.getActivity(NotificationService.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    NotificationCompat.Builder build = (NotificationCompat.Builder) new NotificationCompat.Builder(NotificationService.this).
                            setSmallIcon(R.drawable.news).
                            setContentTitle(myNotification.getFromName()).
                            setContentText(myNotification.getContentComment()).
                            setContentIntent(pending);

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
