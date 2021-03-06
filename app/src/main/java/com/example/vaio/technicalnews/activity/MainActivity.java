package com.example.vaio.technicalnews.activity;


import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.AnyRes;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vaio.technicalnews.R;
import com.example.vaio.technicalnews.fragment.ChatRoomFragment;
import com.example.vaio.technicalnews.model.application.AccountManager;
import com.example.vaio.technicalnews.fragment.ForumFragment;
import com.example.vaio.technicalnews.fragment.HomeFragment;
import com.example.vaio.technicalnews.model.application.GlobalData;
import com.example.vaio.technicalnews.model.application.MySharedPreferences;
import com.example.vaio.technicalnews.model.forum.Topic;
import com.example.vaio.technicalnews.model.forum.UserInfo;
import com.example.vaio.technicalnews.service.NewsService;
import com.example.vaio.technicalnews.service.NotificationService;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.vaio.technicalnews.model.application.FireBaseReference.getAccountRef;
import static com.example.vaio.technicalnews.model.application.MySharedPreferences.USER_NAME;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, MenuItem.OnMenuItemClickListener, View.OnClickListener {
    public static final String TAG = "MainActivity";
    public static final String HOME_TAG = "home";
    public static final String FORUM_TAG = "forum";
    public static final String CHAT_ROOM_TAG = "chat room";
    public static final int RC_LOGIN = 1;
    private static final int RC_PROFILE = 2;
    private static final java.lang.String EMAIL = "vietcoscc@gmail.com";
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

    private ArrayList<Topic> arrTopic;
    private ArrayList<String> arrTopicKey;
    //
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Menu menu; // main activity option menu
    private int menuRes = R.menu.menu_home;

    private AccountManager accountManager;
    private ArrayList<String> arrAdmin;
    private ProgressDialog progressDialog;

    private Boolean onMenuItemForumSelected = false;

    private MenuItem signIn;
    private MenuItem signUp;
    private MenuItem signOut;
    private MenuItem profile;
    private MenuItem manager;

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
//                checkLogin();
            }
            initToolbar();
            initDrawerLayout();
            initFragment();
            loadContentFragment(HOME_TAG);
            initOthers();
            updateUI();
            if (!isMyServiceRunning(NewsService.class)) {
                Intent intentService = new Intent(MainActivity.this, NewsService.class);
                startService(intentService);
            }
            if (!isMyServiceRunning(NotificationService.class)) {
                MySharedPreferences.putString(MainActivity.this, USER_NAME, accountManager.getCurrentUser().getUid());
                Intent intent = new Intent(MainActivity.this, NotificationService.class);
                intent.putExtra(USER_NAME, accountManager.getCurrentUser().getUid());
                startService(intent);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private boolean isMyServiceRunning(Class<?> serviceClass) throws Exception {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void initAccountManager() throws Exception {

        accountManager = new AccountManager(this);
        GlobalData globalData = (GlobalData) getApplication();
        accountManager = globalData.getAccountManager();


        accountManager.setOnLogout(new AccountManager.OnLogout() {
            @Override
            public void logout() {
                try {
                    updateUI();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (progressDialog != null && progressDialog.isShowing()) {
                            progressDialog.hide();
                        }
                    }
                }, 1000);
            }
        });
        arrAdmin = globalData.getArrAdmin();
        if (accountManager != null && accountManager.getCurrentUser() != null) {
            if (arrAdmin != null) {
//                if (arrAdmin.indexOf(accountManager.getCurrentUser().getEmail()) > -1) {
//                    accountManager.setAdmin(true);
//                } else {
//                    accountManager.setAdmin(false);
//                }
            }
        }
    }

    private void updateUI() throws Exception {
        Log.e(TAG, "updateUI");
        try {
            FirebaseUser user = null;
            if (accountManager != null) {
                user = accountManager.getCurrentUser();
                if (user == null) {
                    ivAvatar.setVisibility(View.GONE);
                    tvDisplayName.setText("");
                    tvEmail.setText("");
                    return;
                }
            } else {
                return;
            }

            ivAvatar.setVisibility(View.VISIBLE);
            ivAvatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                    startActivityForResult(intent, RC_PROFILE);
                }
            });
            if (accountManager != null && user.getPhotoUrl() != null) {
                Picasso.with(this).
                        load(user.getPhotoUrl()).
                        error(R.drawable.warning).
                        placeholder(R.drawable.loading).
                        into(ivAvatar);
            } else {
                Uri photoUri = getUriToDrawable(this, R.drawable.boss);
                Picasso.with(this).load(photoUri).error(R.drawable.warning).placeholder(R.drawable.loading).into(ivAvatar);
            }
            String displayName = user.getDisplayName();
            String email = user.getEmail();
            tvDisplayName.setText(displayName);
            tvEmail.setText(email);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static final Uri getUriToDrawable(@NonNull Context context,
                                             @AnyRes int drawableId) throws Exception {
        Uri imageUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +
                "://" + context.getResources().getResourcePackageName(drawableId)
                + '/' + context.getResources().getResourceTypeName(drawableId)
                + '/' + context.getResources().getResourceEntryName(drawableId));
        return imageUri;
    }


    private void initOthers() throws Exception {
        tvDisplayName = (TextView) navigationView.getHeaderView(0).findViewById(R.id.tvDisplayName);
        tvEmail = (TextView) navigationView.getHeaderView(0).findViewById(R.id.tvEmail);
        ivAvatar = (CircleImageView) navigationView.getHeaderView(0).findViewById(R.id.ivAvatar);
        progressDialog = new ProgressDialog(this);

        getAccountRef().child(accountManager.getCurrentUser().getUid()).keepSynced(true);
        getAccountRef().child(accountManager.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try{
                    UserInfo userInfo = dataSnapshot.getValue(UserInfo.class);
                    if (userInfo != null) {
                        accountManager.setUserInfo(userInfo);
                        if (userInfo.isAdmin()) {
                            if (manager != null) {
                                manager.setVisible(true);
                            }
                        }else {
                            if (manager != null) {
                                manager.setVisible(false);
                            }
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void initToolbar() throws Exception {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("News");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    private void initFragment() throws Exception {
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

    private void loadContentFragment(String contentTag) throws Exception {
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
                try {
                    AppBarLayout.LayoutParams params = new AppBarLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS | AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL);
                    toolbar.setLayoutParams(params);
                    loadContentFragment(HOME_TAG);
                    toolbar.setTitle("News");
//                floatingActionButton.setVisibility(View.GONE);
                    menuRes = R.menu.menu_home;
                    onCreateOptionsMenu(menu);
                    drawerLayout.closeDrawer(GravityCompat.START);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
            case R.id.forum:
                try {
                    AppBarLayout.LayoutParams params1 = new AppBarLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    params1.setScrollFlags(0);
                    toolbar.setLayoutParams(params1);
                    loadContentFragment(FORUM_TAG);
                    toolbar.setTitle("Forum");
//                floatingActionButton.setVisibility(View.VISIBLE);
                    if (accountManager.getCurrentUser() == null) {
                        Snackbar.make(findViewById(R.id.content_main), "Sign in now ?", Snackbar.LENGTH_LONG).setAction("SIGN IN", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                try {
                                    showActivityLogin();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }).show();
                    }
                    menuRes = R.menu.menu_forum;
                    onCreateOptionsMenu(menu);
                    drawerLayout.closeDrawer(GravityCompat.START);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
            case R.id.chat_room:
                try {
                    toolbar.setTitle("Chat room");
                    AppBarLayout.LayoutParams params2 = new AppBarLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    params2.setScrollFlags(0);
                    toolbar.setLayoutParams(params2);
                    menuRes = R.menu.menu_forum;
                    onCreateOptionsMenu(menu);
//                floatingActionButton.setVisibility(View.GONE);
                    loadContentFragment(CHAT_ROOM_TAG);
                    drawerLayout.closeDrawer(GravityCompat.START);
                } catch (Exception e) {
                    e.printStackTrace();
                }


                break;

            case R.id.share:
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("text/plain");
                share.putExtra(Intent.EXTRA_TEXT, "_Time_stone_app_");
                startActivity(Intent.createChooser(share, "Share on ..."));
                break;
            case R.id.rate:
                Uri uri = Uri.parse("market://details?id=" + getPackageName());
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                // To count with Play market backstack, After pressing back button,
                // to taken back to our application, we need to add following flags to intent.
                goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                        Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                try {
                    startActivity(goToMarket);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
                }
                break;
            case R.id.feedBack:
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO,
                        Uri.parse("mailto:" + Uri.encode(EMAIL)));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "");
                startActivity(Intent.createChooser(emailIntent, "Send feed back via..."));
                break;
            case R.id.exit:
                setResult(RESULT_OK);
                finish();
                break;
        }
        return true;
    }

    public void showActivityLogin() throws Exception {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivityForResult(intent, RC_LOGIN);
        overridePendingTransition(R.anim.anim_fragment_in_from_right, R.anim.anim_fragment_out_from_right);
    }

    public void showLoginSnackBar() throws Exception {
        if (accountManager.getCurrentUser() == null) {
            Snackbar.make(findViewById(R.id.content_main), "Sign in now ?", Snackbar.LENGTH_LONG).setAction("SIGN IN", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        showActivityLogin();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).show();

        }
    }

    private void showActivityRegister() throws Exception {
        Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.anim_fragment_in_from_right, R.anim.anim_fragment_out_from_right);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;

        switch (menuRes) {
//            case R.menu.menu_home:
//                listView = menu.findItem(R.id.action_list);
//                gridView = menu.findItem(R.id.action_grid);
//                listView.setOnMenuItemClickListener(this);
//                gridView.setOnMenuItemClickListener(this);
//                break;
            case R.menu.menu_forum:
                getMenuInflater().inflate(menuRes, menu);
                signIn = menu.findItem(R.id.action_sign_in);
                signUp = menu.findItem(R.id.action_sign_up);
                signOut = menu.findItem(R.id.action_sign_out);
                profile = menu.findItem(R.id.action_view_profile);
                manager = menu.findItem(R.id.action_mangage);
                if(!accountManager.getUserInfo().isAdmin()){
                    manager.setVisible(false);
                }
                signUp.setOnMenuItemClickListener(this);
                signIn.setOnMenuItemClickListener(this);
                signOut.setOnMenuItemClickListener(this);
                profile.setOnMenuItemClickListener(this);
                manager.setOnMenuItemClickListener(this);
                break;

        }


        return true;
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        try {
            switch (menuItem.getItemId()) {
                case R.id.action_list:

                    break;
                case R.id.action_grid:

                    break;
                case R.id.action_sign_in:
                    showActivityLogin();
                    break;
                case R.id.action_sign_up:
                    showActivityRegister();
                    break;
                case R.id.action_sign_out:
                    progressDialog.setMessage("Singing out ...");
                    progressDialog.show();
                    accountManager.logout();
                    manager.setVisible(false);
                    progressDialog.dismiss();
                    break;
                case R.id.action_view_profile:
                    if (accountManager.getCurrentUser() == null) {
                        showLoginSnackBar();
                    } else {
                        Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                        intent.putExtra("tag", TAG);
                        startActivityForResult(intent, RC_PROFILE);
                    }

                    break;
                case R.id.action_mangage:
                    Intent intent = new Intent(MainActivity.this, ManagerActivity.class);
                    startActivity(intent);
                    break;
            }
        } catch (Exception e) {

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
        try {
            if (!isMyServiceRunning(NewsService.class)) {
                Intent intentService = new Intent(MainActivity.this, NewsService.class);
                startService(intentService);
            }
            if (!isMyServiceRunning(NotificationService.class)) {
                MySharedPreferences.putString(MainActivity.this, USER_NAME, accountManager.getCurrentUser().getUid());
                Intent intent = new Intent(MainActivity.this, NotificationService.class);
                intent.putExtra(USER_NAME, accountManager.getCurrentUser().getUid());
                startService(intent);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        if (onMenuItemForumSelected) {
            return;
        }
        try {
            showAcceptQuitingDialog();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showAcceptQuitingDialog() throws Exception {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Are you sure you want to quit ?");
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    setResult(RESULT_OK);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == RC_LOGIN) {
                if (resultCode == RESULT_OK) {
//                    if (arrAdmin.indexOf(accountManager.getCurrentUser().getEmail()) > -1) {
//                        accountManager.setAdmin(true);
//                        manager.setVisible(true);
//                    } else {
//                        accountManager.setAdmin(false);
//                        manager.setVisible(false);
//                    }
                    updateUI();
                }
            }
            if (requestCode == RC_PROFILE) {
                if (resultCode == RESULT_OK) {
                    Log.e(TAG, "Profile");
                    StorageReference storePhotoAuth = FirebaseStorage.getInstance().getReference().child("Auth/" + accountManager.getCurrentUser().getUid());
                    storePhotoAuth.getDownloadUrl().addOnSuccessListener(this, new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            getAccountRef().child(accountManager.getCurrentUser().getUid()).child("photoUrl").setValue(uri.toString());
                            accountManager.logout();
                            try {
                                updateUI();
                                showLoginSnackBar();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    });


                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
