package com.example.vaio.technicalnews.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.vaio.technicalnews.R;
import com.example.vaio.technicalnews.model.application.AccountManager;
import com.example.vaio.technicalnews.model.application.FireBaseReference;
import com.example.vaio.technicalnews.model.application.GlobalData;
import com.example.vaio.technicalnews.model.forum.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.example.vaio.technicalnews.model.application.FireBaseReference.getAccountRef;

public class SplashScreenActivity extends AppCompatActivity {

    private static final String TAG = "SplashScreenActivity";
    private static final int RC_MAIN = 1;
    private GlobalData globalData;
    private AccountManager accountManager;

//    private String[] room = {"Thông tin công nghệ", "Thương mại điện tử", "Đám mây, Dịch vụ trực tuyến", "Thăm dò công nghệ", "Quảng cáo - Khuyến mãi"
//            , "Quảng cáo - Khuyến mãi", "App", "Thông tin - Sự kiện", "Windows", "Apple - Mac OS X", "Linux", "Chrome OS", "Tư vấn chọn mua Máy tính", "Máy tính"
//            , "iOS", "Android", "Windows Phone", "BlackBerry", "Symbian", "Mạng di động", "Điện thoại"};
//    private String s[] = {"Information and technology", "Ecommerce", "Cloud", "Technology exploration",
//            "Advertising - Promotions", "App", "Information - Events", "Windows", "Apple - Mac OS X",
//            "Linux", "Chrome OS", "Computer Consulting", "Computer", "iOS", "Android", "Windows Phone",
//            "BlackBerry", "Symbian", "Mobile network", "Phone"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        globalData = (GlobalData) getApplication();
        accountManager = new AccountManager(SplashScreenActivity.this);
        globalData.setAccountManager(accountManager);
//        new LongOperation().execute();
        if (accountManager.getCurrentUser() != null) {
            getAccountRef().child(accountManager.getCurrentUser().getUid()).keepSynced(true);
            getAccountRef().child(accountManager.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    UserInfo userInfo = dataSnapshot.getValue(UserInfo.class);
                    accountManager.setUserInfo(userInfo);
                    Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
                    startActivityForResult(intent, RC_MAIN);
                    Log.e(TAG, userInfo.getDisplayName());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else {
            Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
            startActivityForResult(intent, RC_MAIN);
        }
    }

    private class LongOperation extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {


            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        onBackPressed();
    }
}
