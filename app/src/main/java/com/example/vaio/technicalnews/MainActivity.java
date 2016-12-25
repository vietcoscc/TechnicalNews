package com.example.vaio.technicalnews;


import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
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

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, MenuItem.OnMenuItemClickListener, View.OnClickListener {
    public static final String HOME_TAG = "home";
    public static final String FORUM_TAG = "forum";
    public static final String ACCOUNT_MANAGER_EXTRA = "Account Manager";

    private Toolbar toolbar; // toolbar main activity
    private FloatingActionButton floatingActionButton;

    private HomeFragment homeFragment;
    private ForumFragment forumFragment;
    private FrameLayout loginRegisterLayout;

    private DrawerLayout drawerLayout;
    private Menu menu; // main activity option menu
    private int menuRes = R.menu.menu_home;

    private AccountManager accountManager;
    private Boolean signedIn = false;
    private Boolean onMenuItemForumSelected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        accountManager = new AccountManager(this);
        initToolbar();
        initDrawer();
        initFragment();
        loadContentFragment(HOME_TAG);
        initOthers();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();

        myRef.child("hoten2").child("abc").setValue("Hello");
    }

    private void initOthers() {
        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(this);
        loginRegisterLayout = (FrameLayout) findViewById(R.id.login_register_content_main);
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle("Home");
    }

    private void initFragment() {
        homeFragment = new HomeFragment(this);
        forumFragment = new ForumFragment(this);
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
//                            showLoginDialog();
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
                MenuItem login = menu.findItem(R.id.action_login);
                MenuItem register = menu.findItem(R.id.action_register);
                MenuItem logout = menu.findItem(R.id.action_logout);
                register.setOnMenuItemClickListener(this);
                login.setOnMenuItemClickListener(this);
                logout.setOnMenuItemClickListener(this);
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
            case R.id.action_login:
                showFragmentSignInSignOut(new LoginFragment(accountManager));
                break;
            case R.id.action_register:
                showFragmentSignInSignOut(new RegisterFragment(accountManager));
                break;
            case R.id.action_logout:
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
            hideFragmentSignInSignOut();
            return;
        }
        super.onBackPressed();
    }

    public void showFragmentSignInSignOut(Fragment fragment) {
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        loginRegisterLayout.setVisibility(View.VISIBLE);
        onMenuItemForumSelected = true;
        TranslateAnimation animation = (TranslateAnimation) AnimationUtils.loadAnimation(this, R.anim.anim_fragment_in);
        loginRegisterLayout.startAnimation(animation);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.login_register_content_main, fragment);
        transaction.commit();
    }

    public void hideFragmentSignInSignOut() {
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        TranslateAnimation animation = (TranslateAnimation) AnimationUtils.loadAnimation(this, R.anim.anim_fragment_out);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                loginRegisterLayout.removeAllViews();
                loginRegisterLayout.setFocusable(false);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

        });
        loginRegisterLayout.startAnimation(animation);
        onMenuItemForumSelected = false;
    }
}
