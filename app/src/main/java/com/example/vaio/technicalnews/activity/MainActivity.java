package com.example.vaio.technicalnews.activity;


import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;

import com.example.vaio.technicalnews.R;
import com.example.vaio.technicalnews.fragment.AccountManager;
import com.example.vaio.technicalnews.fragment.ForumFragment;
import com.example.vaio.technicalnews.fragment.HomeFragment;
import com.example.vaio.technicalnews.fragment.LoginFragment;
import com.example.vaio.technicalnews.fragment.RegisterFragment;
import com.example.vaio.technicalnews.model.Topic;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, MenuItem.OnMenuItemClickListener, View.OnClickListener {
    public static final String HOME_TAG = "home";
    public static final String FORUM_TAG = "forum";
    public static final int WHAT_SIGN_IN_SIGN_UP = 1;
    public static final String TOPIC = "Topic";
    public static final String TYPE_1 = "Question and answer";
    private Toolbar toolbar; // toolbar main activity
    private FloatingActionButton floatingActionButton;

    private HomeFragment homeFragment;
    private ForumFragment forumFragment;
    private FrameLayout signInSignOutLayout;

    private DrawerLayout drawerLayout;
    private Menu menu; // main activity option menu
    private int menuRes = R.menu.menu_home;

    private AccountManager accountManager;
    private Boolean signedIn = false;
    private Boolean onMenuItemForumSelected = false;
    private Handler handlerSignInSignUp = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == WHAT_SIGN_IN_SIGN_UP) {
                int result = msg.arg1; // 1 -> successful  0->failed
                if (result == 1) {
                    hideFragmentSignInSignUp();
                }
            }
        }
    };

    public static boolean isNetWorkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        accountManager = new AccountManager(this, handlerSignInSignUp);
        initToolbar();
        initDrawer();
        initFragment();
        loadContentFragment(HOME_TAG);
        initOthers();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();

        Calendar calendar = Calendar.getInstance();
        String date = calendar.get(Calendar.DAY_OF_MONTH) + "/" + calendar.get(Calendar.MONTH) + "/" + calendar.get(Calendar.YEAR) + "";
        String time = calendar.get(Calendar.HOUR) + ":" + calendar.get(Calendar.MINUTE) + ":" + calendar.get(Calendar.SECOND) + "";
        Topic topic = new Topic("Nguyễn Quốc việt", date, time, 0, 0, 0, "vietcoscc@gmail.com");
        myRef.child(TOPIC).child(TYPE_1).push().setValue(topic);
    }

    private void initOthers() {
        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(this);
        signInSignOutLayout = (FrameLayout) findViewById(R.id.login_register_content_main);
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle("Home");
    }

    private void initFragment() {
        homeFragment = new HomeFragment(this);
        ArrayList<Topic> arrTopic = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        String date = calendar.get(Calendar.DAY_OF_MONTH) + "/" + calendar.get(Calendar.MONTH) + "/" + calendar.get(Calendar.YEAR) + "";
        String time = calendar.get(Calendar.HOUR) + ":" + calendar.get(Calendar.MINUTE) + ":" + calendar.get(Calendar.SECOND) + "";
        Topic topic = new Topic("Nguyễn Quốc việt", date, time, 0, 0, 0, "vietcoscc@gmail.com");
        arrTopic.add(topic);
        Topic topic2 = new Topic("Nguyễn Quốc việt2", date, time, 0, 0, 0, "vietcoscc@gmail.com");
        arrTopic.add(topic2);
        arrTopic.add(topic2);arrTopic.add(topic2);arrTopic.add(topic2);arrTopic.add(topic2);arrTopic.add(topic2);arrTopic.add(topic2);arrTopic.add(topic2);arrTopic.add(topic2);arrTopic.add(topic2);arrTopic.add(topic2);arrTopic.add(topic2);arrTopic.add(topic2);arrTopic.add(topic2);arrTopic.add(topic2);arrTopic.add(topic2);

        forumFragment = new ForumFragment(this,arrTopic);
    }

    private void initDrawer() {

        final CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout); // app_bar_main layout
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(this);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            public void onDrawerClosed(View view) {
                supportInvalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                supportInvalidateOptionsMenu();
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                coordinatorLayout.setTranslationX(slideOffset * drawerView.getWidth());
                drawerLayout.bringChildToFront(drawerView);
                drawerLayout.requestLayout();
            }
        };
        toggle.syncState();
        drawerLayout.addDrawerListener(toggle);
    }

    private void loadContentFragment(String contentTag) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        switch (contentTag) {
            case HOME_TAG:
                transaction.replace(R.id.content_main, homeFragment);
                break;
            case FORUM_TAG:
                transaction.replace(R.id.content_main, forumFragment);
                break;
        }
        transaction.commit();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.home:
                toolbar.setTitle("Home");
                floatingActionButton.setVisibility(View.GONE);
                menuRes = R.menu.menu_home;
                onCreateOptionsMenu(menu);
                loadContentFragment(HOME_TAG);
                break;
            case R.id.forum:
                toolbar.setTitle("Forum");
                floatingActionButton.setVisibility(View.VISIBLE);
                if (!signedIn) {
                    Snackbar.make(findViewById(R.id.content_main), "Sign in now ?", Snackbar.LENGTH_LONG).setAction("SIGN IN", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            showFragmentSignInSignUp(new LoginFragment(accountManager));
                        }
                    }).show();
                }
                menuRes = R.menu.menu_forum;
                onCreateOptionsMenu(menu);
                loadContentFragment(FORUM_TAG);
                break;
        }
//        Toast.makeText(this, contentTag, Toast.LENGTH_SHORT).show();

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(menuRes, menu);
        switch (menuRes) {
            case R.menu.menu_home:
                MenuItem listView = menu.findItem(R.id.action_list);
                MenuItem gridView = menu.findItem(R.id.action_grid);
                listView.setOnMenuItemClickListener(this);
                gridView.setOnMenuItemClickListener(this);
                break;
            case R.menu.menu_forum:
                MenuItem signIn = menu.findItem(R.id.action_sign_in);
                MenuItem signUp = menu.findItem(R.id.action_sign_up);
                MenuItem signOut = menu.findItem(R.id.action_sign_out);
                signUp.setOnMenuItemClickListener(this);
                signIn.setOnMenuItemClickListener(this);
                signOut.setOnMenuItemClickListener(this);
                break;
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.action_list:
                break;
            case R.id.action_grid:
                break;
            case R.id.action_sign_in:
                showFragmentSignInSignUp(new LoginFragment(accountManager));
                break;
            case R.id.action_sign_up:
                showFragmentSignInSignUp(new RegisterFragment(accountManager));
                break;
            case R.id.action_sign_out:
                break;
        }
        return false;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab:
                Intent intent = new Intent(MainActivity.this, PostActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (onMenuItemForumSelected) {
            hideFragmentSignInSignUp();
            return;
        }
        showAcceptQuitingDialog();
    }

    public void showAcceptQuitingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this,android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar);
        builder.setTitle("Are you sure you want to quit ?");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.create().show();
    }

    public void showFragmentSignInSignUp(Fragment fragment) {
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        signInSignOutLayout.setVisibility(View.VISIBLE);
        onMenuItemForumSelected = true;
        TranslateAnimation animation = (TranslateAnimation) AnimationUtils.loadAnimation(this, R.anim.anim_fragment_in);
        signInSignOutLayout.startAnimation(animation);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.login_register_content_main, fragment);
        transaction.commit();
    }

    public void hideFragmentSignInSignUp() {
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        TranslateAnimation animation = (TranslateAnimation) AnimationUtils.loadAnimation(this, R.anim.anim_fragment_out);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                signInSignOutLayout.removeAllViews();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

        });
        signInSignOutLayout.startAnimation(animation);
        onMenuItemForumSelected = false;
    }
}
