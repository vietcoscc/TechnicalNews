package com.example.vaio.technicalnews.activity;


import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vaio.technicalnews.R;
import com.example.vaio.technicalnews.database.MyDatabase;
import com.example.vaio.technicalnews.fragment.NewsFragment;
import com.example.vaio.technicalnews.model.AccountManager;
import com.example.vaio.technicalnews.fragment.ForumFragment;
import com.example.vaio.technicalnews.fragment.HomeFragment;
import com.example.vaio.technicalnews.fragment.LoginFragment;
import com.example.vaio.technicalnews.fragment.RegisterFragment;
import com.example.vaio.technicalnews.model.Topic;
import com.example.vaio.technicalnews.parser.NewsContentParser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, MenuItem.OnMenuItemClickListener, View.OnClickListener {
    public static final String HOME_TAG = "home";
    public static final String FORUM_TAG = "forum";
    public static final int WHAT_SIGN_IN_SIGN_UP = 1;
    public static final int WHAT_SIGN_IN_SUCCESS = 2;
    public static final String TOPIC = "Topic";
    public static final String TYPE_1 = "Ask and answer";
    public static final String TYPE_2 = "Tips";
    public static final String TYPE_3 = "Trading";

    public static final int POST_REQUEST_CODE = 0;
    //
    public static final String DISPLAY_NAME = "display name";
    public static final String EMAIL = "email";
    // component
    private Toolbar toolbar; // toolbar main activity
    private FloatingActionButton floatingActionButton;
    private ImageView ivAvatar;
    private TextView tvDisplayName;
    private TextView tvEmail;
    // fragment
    private HomeFragment homeFragment;
    private ForumFragment forumFragment;
    private FrameLayout signInSignOutLayout;
    private ArrayList<Topic> arrTopic;
    private ArrayList<String> arrTopicKey;
    //
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Menu menu; // main activity option menu
    private int menuRes = R.menu.menu_home;

    private AccountManager accountManager;
    private Boolean signedIn = false;
    private Boolean onMenuItemForumSelected = false;

    private ProgressDialog progressDialog;
    private Handler handlerSignInSignUp = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == WHAT_SIGN_IN_SIGN_UP) {
                int result = msg.arg1; // 1 -> successful  0->failed
                if (result == 1) {
                    hideFragmentSignInSignUp();
                    Uri uri = accountManager.getCurrentUser().getPhotoUrl();
                    String displayName = accountManager.getCurrentUser().getDisplayName();
                    String email = accountManager.getCurrentUser().getEmail();
                    if (uri != null) {
                        ivAvatar.setImageURI(uri);
                    }
                    if (!displayName.isEmpty()) {
                        tvDisplayName.setText(displayName);
                    }
                    if (!email.isEmpty()) {
                        tvEmail.setText(accountManager.getCurrentUser().getEmail());
                    }
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

        try {
            accountManager = new AccountManager(this, handlerSignInSignUp);
            initToolbar();
            initDrawerLayout();
            initFragment();
            loadContentFragment(HOME_TAG);
            initOthers();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void initOthers() throws Exception {
        progressDialog = new ProgressDialog(this);

        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!signedIn) {
                    Snackbar.make(findViewById(R.id.content_main), "Sign in now ?", Snackbar.LENGTH_LONG).setAction("SIGN IN", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            showFragmentSignInSignUp(new LoginFragment(accountManager));
                        }
                    }).show();
                    return;
                }
                Intent intent = new Intent(MainActivity.this, PostActivity.class);
                intent.putExtra(DISPLAY_NAME, accountManager.getCurrentUser().getDisplayName());
                intent.putExtra(EMAIL, accountManager.getCurrentUser().getEmail());
                startActivity(intent);
            }
        });
        signInSignOutLayout = (FrameLayout) findViewById(R.id.login_register_content_main);

        ivAvatar = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.ivAvatar);
        tvDisplayName = (TextView) navigationView.getHeaderView(0).findViewById(R.id.tvDisplayName);
        tvEmail = (TextView) navigationView.getHeaderView(0).findViewById(R.id.tvEmail);
    }

    private void initToolbar() throws Exception {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle("Home");
    }

    private void initFragment() throws Exception {
        DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference();
        arrTopic = new ArrayList<>();
        arrTopicKey = new ArrayList<>();
        forumFragment = new ForumFragment(this, firebaseDatabase, arrTopic, arrTopicKey, getWindowManager());
        homeFragment = new HomeFragment(this, getSupportFragmentManager());

        Calendar calendar = Calendar.getInstance();
//        String date = calendar.get(Calendar.DAY_OF_MONTH) + "/" + calendar.get(Calendar.MONTH) + "/" + calendar.get(Calendar.YEAR) + "";
//        String time = calendar.get(Calendar.HOUR) + ":" + calendar.get(Calendar.MINUTE) + ":" + calendar.get(Calendar.SECOND) + "";
        //
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Topic topic = dataSnapshot.getValue(Topic.class);
                arrTopic.add(topic);
                arrTopicKey.add(dataSnapshot.getKey());
                Toast.makeText(MainActivity.this, dataSnapshot.getKey(), Toast.LENGTH_SHORT).show();
                forumFragment.notifyData();
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
        };
        //

        firebaseDatabase.child(TOPIC).child(TYPE_1).addChildEventListener(childEventListener);
//        Topic topic = new Topic("demo", date, time, 0, 0, 0, accountManager.getCurrentUser().getDisplayName());
//        arrTopic.add(topic);

    }

    private void initDrawerLayout() throws Exception {

        final CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout); // app_bar_main layout
        navigationView = (NavigationView) findViewById(R.id.navigationView);
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
                homeFragment = new HomeFragment(this, getSupportFragmentManager());
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
                loadContentFragment(HOME_TAG);
                toolbar.setTitle("Home");
                floatingActionButton.setVisibility(View.GONE);
                menuRes = R.menu.menu_home;
                onCreateOptionsMenu(menu);
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.forum:
                loadContentFragment(FORUM_TAG);
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
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.setting:
                Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(intent);
                break;
        }
//        Toast.makeText(this, contentTag, Toast.LENGTH_SHORT).show();


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
                accountManager.logout();
                signedIn = false;
                break;
        }
        return false;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab:

                break;
        }
    }


    @Override
    public void onBackPressed() {
        if (onMenuItemForumSelected) {
            hideFragmentSignInSignUp();
            signedIn = false;
            return;
        }
        showAcceptQuitingDialog();
    }

    public void showAcceptQuitingDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar);
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
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                FrameLayout frameLayout = (FrameLayout) findViewById(R.id.content_main);
                frameLayout.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        signInSignOutLayout.startAnimation(animation);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.login_register_content_main, fragment);
        transaction.commit();
    }

    public void hideFragmentSignInSignUp() {
        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.content_main);
        frameLayout.setFocusable(true);
        frameLayout.setClickable(true);
        frameLayout.setFocusableInTouchMode(true);
        signedIn = true;
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        TranslateAnimation animation = (TranslateAnimation) AnimationUtils.loadAnimation(this, R.anim.anim_fragment_out);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                FrameLayout frameLayout = (FrameLayout) findViewById(R.id.content_main);
                frameLayout.setVisibility(View.VISIBLE);
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
    @Override
    protected void onStop() {
        super.onStop();
        try {
            MyDatabase myDatabase = new MyDatabase(MainActivity.this);
            myDatabase.clearTable(MyDatabase.TB_NAME_NEWS);
            myDatabase.addArrNewsItem(homeFragment.getArrNewsItem());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
