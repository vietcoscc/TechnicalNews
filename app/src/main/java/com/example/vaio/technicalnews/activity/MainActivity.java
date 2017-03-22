package com.example.vaio.technicalnews.activity;


import android.app.AlertDialog;
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
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vaio.technicalnews.R;
import com.example.vaio.technicalnews.database.MyDatabase;
import com.example.vaio.technicalnews.fragment.ChatRoomFragment;
import com.example.vaio.technicalnews.model.AccountManager;
import com.example.vaio.technicalnews.fragment.ForumFragment;
import com.example.vaio.technicalnews.fragment.HomeFragment;
import com.example.vaio.technicalnews.model.GlobalData;
import com.example.vaio.technicalnews.model.Topic;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, MenuItem.OnMenuItemClickListener, View.OnClickListener {
    public static final String HOME_TAG = "home";
    public static final String FORUM_TAG = "forum";
    public static final String CHAT_ROOM_TAG = "chat room";
    public static final String TOPIC = "Topic";
    public static final String TYPE_1 = "Ask and answer";
    public static final String TYPE_2 = "Tips";
    public static final String TYPE_3 = "Trading";

    public static final int RC_POST = 0;
    //
    public static final String DISPLAY_NAME = "display name";
    public static final String EMAIL = "email";
    private static final int RC_LOGIN = 1;
    private static final String TAG = "MainActivity";
    // component
    private Toolbar toolbar; // toolbar main activity
    //    private FloatingActionButton floatingActionButton;
    private CircleImageView ivAvatar;
    private TextView tvDisplayName;
    private TextView tvEmail;
    // fragment
    private HomeFragment homeFragment;
    private ForumFragment forumFragment;
    private ChatRoomFragment chatRoomFragment;


    private FrameLayout signInSignOutLayout;
    private ArrayList<Topic> arrTopic;
    private ArrayList<String> arrTopicKey;
    //
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Menu menu; // main activity option menu
    private int menuRes = R.menu.menu_home;

    private AccountManager accountManager;
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference();

    private ProgressDialog progressDialog;
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
            updateUI();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void checkLogin() throws Exception {
        progressDialog.show();

        SharedPreferences sharedPreferences = getSharedPreferences(LoginActivity.SHARED_PREF, MODE_PRIVATE);
        String userName = sharedPreferences.getString(LoginActivity.USER_NAME, "");
        String password = sharedPreferences.getString(LoginActivity.PASSWORD, "");
        if (userName.isEmpty() || password.isEmpty()) {
            progressDialog.hide();
            return;
        }
        accountManager.setOnLoginSuccess(new AccountManager.OnLoginSuccess() {
            @Override
            public void onSuccess() {
                updateUI();
                progressDialog.hide();
            }
        });
        accountManager.setOnLoginFail(new AccountManager.OnLoginFail() {
            @Override
            public void onFail() {
                progressDialog.hide();
            }
        });
        accountManager.login(userName, password);
    }

    private void initAccountManager() throws Exception {
        //
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Signing up ... ");
        progressDialog.setCancelable(false);
        //
        accountManager = new AccountManager(this);
        GlobalData globalData = (GlobalData) getApplication();
        globalData.setAccountManager(accountManager);
        accountManager.setOnLogout(new AccountManager.OnLogout() {
            @Override
            public void logout() {
                updateUI();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (progressDialog != null && progressDialog.isShowing()) {
                            progressDialog.hide();
                        }
                    }
                }, 2000);
            }
        });
    }

    private void updateUI() {
        Log.e(TAG, "updateUI");
        try {
            FirebaseUser user = accountManager.getCurrentUser();
            if (user == null) {
                ivAvatar.setVisibility(View.GONE);
                tvDisplayName.setText("");
                tvEmail.setText("");
                return;
            }
            ivAvatar.setVisibility(View.VISIBLE);
            Uri uri = accountManager.getCurrentUser().getPhotoUrl();

            Log.e(TAG, uri + "");
            if (uri != null) {
                Picasso.with(this).load(uri).error(R.drawable.warning).placeholder(R.drawable.loading).into(ivAvatar);
            }

            String displayName = accountManager.getCurrentUser().getDisplayName();
            Log.e(TAG, displayName);
            String email = accountManager.getCurrentUser().getEmail();

            tvDisplayName.setText(displayName);
            tvEmail.setText(email);
            accountManager.setSignedIn(true);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_LOGIN) {
            if (resultCode == RESULT_OK) {
                updateUI();
            }
        }
    }

    private void initOthers() throws Exception {
        signInSignOutLayout = (FrameLayout) findViewById(R.id.login_register_content_main);
        tvDisplayName = (TextView) navigationView.getHeaderView(0).findViewById(R.id.tvDisplayName);
        tvEmail = (TextView) navigationView.getHeaderView(0).findViewById(R.id.tvEmail);
        ivAvatar = (CircleImageView) navigationView.getHeaderView(0).findViewById(R.id.ivAvatar);
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
                AppBarLayout.LayoutParams params = new AppBarLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS | AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL);
                toolbar.setLayoutParams(params);
                loadContentFragment(HOME_TAG);
                toolbar.setTitle("Home");
//                floatingActionButton.setVisibility(View.GONE);
                menuRes = R.menu.menu_home;
                onCreateOptionsMenu(menu);
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.forum:
                AppBarLayout.LayoutParams params1 = new AppBarLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params1.setScrollFlags(0);
                toolbar.setLayoutParams(params1);
                loadContentFragment(FORUM_TAG);
                toolbar.setTitle("Forum");
//                floatingActionButton.setVisibility(View.VISIBLE);
                if (!accountManager.isSignedIn()) {
                    Snackbar.make(findViewById(R.id.content_main), "Sign in now ?", Snackbar.LENGTH_LONG).setAction("SIGN IN", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            showActivityLogin();
                        }
                    }).show();
                }
                menuRes = R.menu.menu_forum;
                onCreateOptionsMenu(menu);
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.chat_room:
                toolbar.setTitle("Chat room");
                AppBarLayout.LayoutParams params2 = new AppBarLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params2.setScrollFlags(0);
                toolbar.setLayoutParams(params2);
                menuRes = R.menu.menu_forum;
                onCreateOptionsMenu(menu);
//                floatingActionButton.setVisibility(View.GONE);
                loadContentFragment(CHAT_ROOM_TAG);
                drawerLayout.closeDrawer(GravityCompat.START);
                if (!accountManager.isSignedIn()) {
                    Snackbar.make(findViewById(R.id.content_main), "Sign in now ?", Snackbar.LENGTH_LONG).setAction("SIGN IN", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
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
        startActivityForResult(intent, RC_LOGIN);
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
//                SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
//                searchView.setQueryHint("Search ... ");
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
                progressDialog.setMessage("Singing out ...");
                progressDialog.show();
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
            accountManager.setSignedIn(false);
            return;
        }
        showAcceptQuitingDialog();
    }

    public void showAcceptQuitingDialog() {
        try {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        try {
//            if (MainActivity.isNetWorkAvailable(this) && !homeFragment.getArrNewsItem().isEmpty()) {
//                MyDatabase myDatabase = new MyDatabase(MainActivity.this);
//                myDatabase.clearTable(MyDatabase.TB_NAME_NEWS);
//                myDatabase.addArrNewsItem(homeFragment.getArrNewsItem());
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
