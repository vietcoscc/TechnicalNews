package com.example.vaio.technicalnews.activity;


import android.app.AlertDialog;
import android.app.Notification;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.vaio.technicalnews.R;
import com.example.vaio.technicalnews.database.MyDatabase;
import com.example.vaio.technicalnews.fragment.ChatRoomFragment;
import com.example.vaio.technicalnews.model.AccountManager;
import com.example.vaio.technicalnews.fragment.ForumFragment;
import com.example.vaio.technicalnews.fragment.HomeFragment;
import com.example.vaio.technicalnews.fragment.LoginFragment;
import com.example.vaio.technicalnews.fragment.RegisterFragment;
import com.example.vaio.technicalnews.model.GlobalData;
import com.example.vaio.technicalnews.model.Topic;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, MenuItem.OnMenuItemClickListener, View.OnClickListener {
    public static final String HOME_TAG = "home";
    public static final String FORUM_TAG = "forum";
    public static final String CHAT_ROOM_TAG = "chat room";
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
    private ChatRoomFragment chatRoomFragment;
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
    private Boolean onMenuItemForumSelected = false;

    public static boolean isNetWorkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            initAccountManager();
            if (isNetWorkAvailable(this)) {
                checkLogin();
            }
            initToolbar();
            initDrawerLayout();
            initFragment();
            loadContentFragment(HOME_TAG);
            initOthers();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void checkLogin() {
        SharedPreferences sharedPreferences = getSharedPreferences(LoginActivity.SHARED_PREF, MODE_PRIVATE);
        String userName = sharedPreferences.getString(LoginActivity.USER_NAME, "");
        String password = sharedPreferences.getString(LoginActivity.PASSWORD, "");
        if (userName.isEmpty() || password.isEmpty()) {
            return;
        }
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loging in ... ");
        progressDialog.show();
        progressDialog.setCancelable(false);
        accountManager.login(userName, password);
        accountManager.setOnLoginSuccess(new AccountManager.OnLoginSuccess() {
            @Override
            public void onSuccess() {
                progressDialog.hide();
            }
        });
    }

    private void initAccountManager() {
        accountManager = new AccountManager(this);
        GlobalData globalData = (GlobalData) getApplication();
        globalData.setAccountManager(accountManager);

        accountManager.setOnLoginSuccess(new AccountManager.OnLoginSuccess() {
            @Override
            public void onSuccess() {
                hideFragmentSignInSignUp();
                Uri uri = accountManager.getCurrentUser().getPhotoUrl();
                String displayName = accountManager.getCurrentUser().getDisplayName();
                String email = accountManager.getCurrentUser().getEmail();
                ivAvatar.setImageURI(uri);
                tvDisplayName.setText(displayName);
                tvEmail.setText(email);
                accountManager.setSignedIn(true);
            }
        });
        accountManager.setOnRegisterSuccess(new AccountManager.OnRegisterSuccess() {
            @Override
            public void onSuccses() {
                hideFragmentSignInSignUp();
            }
        });
    }

    private void initOthers() throws Exception {

        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!accountManager.isSignedIn()) {
                    Snackbar.make(findViewById(R.id.content_main), "Sign in now ?", Snackbar.LENGTH_LONG).setAction("SIGN IN", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
//                            showFragmentSignInSignUp(loginFragment);
                            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.anim_fragment_in_from_right, R.anim.anim_fragment_out_from_right);

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
        arrTopic = new ArrayList<>();
        arrTopicKey = new ArrayList<>();
        forumFragment = new ForumFragment(accountManager);
        homeFragment = new HomeFragment(this, getFragmentManager());
        chatRoomFragment = new ChatRoomFragment(accountManager);
        loginFragment = new LoginFragment(accountManager);
        loginFragment.setOnClickButtonSignUp(new LoginFragment.OnClickButtonSignUp() {
            @Override
            public void onClick() {
                registerFragment = new RegisterFragment(accountManager);
                showFragmentSignInSignUp(registerFragment);
            }
        });
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
                homeFragment = new HomeFragment(this, getFragmentManager());
                transaction.replace(R.id.content_main, homeFragment);
                break;
            case FORUM_TAG:
                transaction.replace(R.id.content_main, forumFragment);
                break;
            case CHAT_ROOM_TAG:
                transaction.replace(R.id.content_main, chatRoomFragment);
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
                if (!accountManager.isSignedIn()) {
                    Snackbar.make(findViewById(R.id.content_main), "Sign in now ?", Snackbar.LENGTH_LONG).setAction("SIGN IN", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
//                            showFragmentSignInSignUp(loginFragment);
                            showActivityLogin();
                        }
                    }).show();
                }
                menuRes = R.menu.menu_forum;
                onCreateOptionsMenu(menu);
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.chat_room:
                menuRes = R.menu.menu_forum;
                onCreateOptionsMenu(menu);
                floatingActionButton.setVisibility(View.GONE);
                loadContentFragment(CHAT_ROOM_TAG);
                drawerLayout.closeDrawer(GravityCompat.START);
                if (!accountManager.isSignedIn()) {
                    Snackbar.make(findViewById(R.id.content_main), "Sign in now ?", Snackbar.LENGTH_LONG).setAction("SIGN IN", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
//                            showFragmentSignInSignUp(loginFragment);
                            showActivityLogin();
                        }
                    }).show();
                }
                break;
            case R.id.setting:
                Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(intent);
                break;
        }
        return true;
    }

    private void showActivityLogin() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.anim_fragment_in_from_right, R.anim.anim_fragment_out_from_right);
    }

    private void showActivityRegister() {
        Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.anim_fragment_in_from_right, R.anim.anim_fragment_out_from_right);
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
                SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
                searchView.setQueryHint("Search ... ");
                break;

        }


        return true;
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.action_list:

                break;
            case R.id.action_grid:
                break;
            case R.id.action_sign_in:
//                showFragmentSignInSignUp(loginFragment);
                showActivityLogin();
                break;
            case R.id.action_sign_up:
//                showFragmentSignInSignUp(registerFragment);
                showActivityRegister();
                break;
            case R.id.action_sign_out:
                SharedPreferences sharedPreferences = getSharedPreferences(LoginActivity.SHARED_PREF, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.commit();

                accountManager.logout();
                accountManager.setSignedIn(false);
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
            accountManager.setSignedIn(false);
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
