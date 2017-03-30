package com.example.vaio.technicalnews.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.vaio.technicalnews.R;
import com.example.vaio.technicalnews.database.MyDatabase;
import com.example.vaio.technicalnews.model.AccountManager;
import com.example.vaio.technicalnews.model.FireBaseReference;
import com.example.vaio.technicalnews.model.GlobalData;
import com.example.vaio.technicalnews.model.ItemChat;
import com.example.vaio.technicalnews.model.RoomChat;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SplashScreenActivity extends AppCompatActivity {

    private static final String TAG = "SplashScreenActivity";
    private static final int RC_MAIN = 1;
    private GlobalData globalData;
    private String[] room = {"Thông tin công nghệ", "Thương mại điện tử", "Đám mây, Dịch vụ trực tuyến", "Thăm dò công nghệ", "Quảng cáo - Khuyến mãi"
            , "Quảng cáo - Khuyến mãi", "App", "Thông tin - Sự kiện", "Windows", "Apple - Mac OS X", "Linux", "Chrome OS", "Tư vấn chọn mua Máy tính", "Máy tính"
            , "iOS", "Android", "Windows Phone", "BlackBerry", "Symbian", "Mạng di động", "Điện thoại"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        globalData = (GlobalData) getApplication();
        new LongOperation().execute();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private class LongOperation extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {
//                for (int i = 0; i < room.length; i++) {
//                    RoomChat roomChat = new RoomChat(room[i], 0, null);
//                    FireBaseReference.getRoomChatRef().push().setValue(roomChat);
//                }
                FirebaseDatabase.getInstance().setPersistenceEnabled(true);
//                FireBaseReference.getBanRef().push().setValue("vietcoscc9x@gmail.com");
                AccountManager accountManager = new AccountManager(SplashScreenActivity.this);
                globalData.setAccountManager(accountManager);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            final ArrayList<String> arrAdmin = new ArrayList<>();
            final ArrayList<String> arrBan = new ArrayList<>();
            arrAdmin.clear();
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
            FireBaseReference.getAdminRef().addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    globalData.setArrAdmin(arrAdmin);
                    FireBaseReference.getBanRef().addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            String mail = dataSnapshot.getValue(String.class);
                            arrBan.add(mail);
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
                    FireBaseReference.getBanRef().addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            globalData.setArrBan(arrBan);
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
                                    startActivityForResult(intent, RC_MAIN);
                                }
                            }, 1000);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        onBackPressed();
    }
}
