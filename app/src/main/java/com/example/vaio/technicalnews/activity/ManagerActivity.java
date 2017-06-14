package com.example.vaio.technicalnews.activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.vaio.technicalnews.R;
import com.example.vaio.technicalnews.fragment.BannedFragment;
import com.example.vaio.technicalnews.fragment.DeletedFragment;
import com.example.vaio.technicalnews.fragment.ManagerFragment;
import com.example.vaio.technicalnews.model.application.AccountManager;
import com.example.vaio.technicalnews.model.application.GlobalData;

public class ManagerActivity extends AppCompatActivity {
    private ManagerFragment managerFragment;
    private BannedFragment bannedFragment;
    private DeletedFragment deletedFragment;
    public static final String MANAGER_TAG = "Manager";
    public static final String BANNED_TAG = "Banned";
    public static final String DELETED_TAG = "Deleted";
    private String currentTag = MANAGER_TAG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        GlobalData globalData = (GlobalData) getApplication();
        AccountManager accountManager = globalData.getAccountManager();
        managerFragment = new ManagerFragment(accountManager, this);
        bannedFragment = new BannedFragment(accountManager);
        deletedFragment = new DeletedFragment(accountManager);
        try {
            loadContentMain(managerFragment);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadContentMain(Fragment fragment) throws Exception{
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.anim_fragment_in_from_right, R.anim.anim_fragment_out_from_left);
        transaction.replace(R.id.layoutContentMain, fragment);
        transaction.commit();
    }

    public void loadContentMain(String tag) {
        try {
            switch (tag) {
                case MANAGER_TAG:
                    currentTag = MANAGER_TAG;
                    loadContentMain(managerFragment);
                    break;
                case BANNED_TAG:
                    currentTag = BANNED_TAG;
                    loadContentMain(bannedFragment);
                    break;
                case DELETED_TAG:
                    currentTag = DELETED_TAG;
                    loadContentMain(deletedFragment);
                    break;
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
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
    public void onBackPressed() {
        switch (currentTag) {
            case MANAGER_TAG:
                finish();
                break;
            case BANNED_TAG:
            case DELETED_TAG:
                loadContentMain(MANAGER_TAG);
                break;
        }
    }
}
