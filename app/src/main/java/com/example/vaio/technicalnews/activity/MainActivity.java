package com.example.vaio.technicalnews.activity;


import android.app.AlertDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
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
    public static final int WHAT_SIGN_IN = 1;
    public static final int WHAT_SIGN_UP = 2;
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
    private LoginFragment loginFragment;
    private RegisterFragment registerFragment;
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
            int result = msg.arg1; // 1 -> successful  0->failed
            switch (msg.what) {

                case WHAT_SIGN_IN:

                    if (result == 1) {
                        hideFragmentSignInSignUp();
                        Uri uri = accountManager.getCurrentUser().getPhotoUrl();
                        String displayName = accountManager.getCurrentUser().getDisplayName();
                        String email = accountManager.getCurrentUser().getEmail();
                        ivAvatar.setImageURI(uri);
                        tvDisplayName.setText(displayName);
                        tvEmail.setText(email);
                        signedIn = true;
                    }
                    break;

                case WHAT_SIGN_UP:
                    if (result == 1) {
                        hideFragmentSignInSignUp();
                    }
                    break;

            }
        }
    };
    private Handler handlerSignUp = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == WHAT_SIGN_UP) {
                registerFragment = new RegisterFragment(accountManager);
                showFragmentSignInSignUp(registerFragment);
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
                            loginFragment = new LoginFragment(accountManager, handlerSignUp);
                            showFragmentSignInSignUp(loginFragment);
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
        forumFragment = new ForumFragment(this, firebaseDatabase);
        homeFragment = new HomeFragment(this, getSupportFragmentManager());
        loginFragment = new LoginFragment(accountManager, handlerSignUp);
        registerFragment = new RegisterFragment(accountManager);
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
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
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
                            showFragmentSignInSignUp(loginFragment);
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

//                SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
                SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
//                searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
                searchView.setQueryHint("Search ... ");
                break;
        }


        return true;
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.action_list:
                NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
                builder.setSmallIcon(android.R.drawable.ic_menu_zoom);
                builder.setContentTitle("Nguyễn Quốc Việt");
                builder.setContentInfo("Hệ quản trị cơ sở dữ liệu");
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_menu_camera);
                builder.setLargeIcon(bitmap);
                Notification notificationCompat = builder.build();
                NotificationManagerCompat.from(this).notify(0, notificationCompat);

                break;
            case R.id.action_grid:
                break;
            case R.id.action_sign_in:
                showFragmentSignInSignUp(loginFragment);
                break;
            case R.id.action_sign_up:
                showFragmentSignInSignUp(registerFragment);
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

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Are you sure you want to quit ?");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.create().show();
    }

    public void showFragmentSignInSignUp(final Fragment fragment) {

        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        signInSignOutLayout.setVisibility(View.VISIBLE);
        onMenuItemForumSelected = true;

        FrameLayout contentMainLayout = (FrameLayout) findViewById(R.id.content_main);
        contentMainLayout.setFocusable(false);
        FrameLayout loginRegisterContentMainLayout = (FrameLayout) findViewById(R.id.login_register_content_main);
        AnimationSet animationSet = (AnimationSet) AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);
        loginRegisterContentMainLayout.startAnimation(animationSet);
        contentMainLayout.setVisibility(View.GONE);
        replaceFragment(R.id.login_register_content_main, fragment);

    }

    public void replaceFragment(int idRes, Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        transaction.replace(idRes, fragment);
        transaction.commit();
    }

    public void hideFragmentSignInSignUp() {
        final FrameLayout contentMainLayout = (FrameLayout) findViewById(R.id.content_main);
        contentMainLayout.setFocusable(true);
        contentMainLayout.setClickable(true);
        contentMainLayout.setFocusableInTouchMode(true);
        contentMainLayout.setVisibility(View.VISIBLE);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);

        FrameLayout loginRegisterContentMainLayout = (FrameLayout) findViewById(R.id.login_register_content_main);
        AnimationSet animationSet = (AnimationSet) AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right);
        loginRegisterContentMainLayout.startAnimation(animationSet);
        loginRegisterContentMainLayout.setVisibility(View.GONE);
        onMenuItemForumSelected = false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            if (MainActivity.isNetWorkAvailable(this) && !homeFragment.getArrNewsItem().isEmpty()) {
                MyDatabase myDatabase = new MyDatabase(MainActivity.this);
                myDatabase.clearTable(MyDatabase.TB_NAME_NEWS);
                myDatabase.addArrNewsItem(homeFragment.getArrNewsItem());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
