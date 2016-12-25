package com.example.vaio.technicalnews;


import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, MenuItem.OnMenuItemClickListener {
    public static final String HOME_TAG = "home";
    public static final String FORUM_TAG = "forum";
    private Toolbar toolbar; // toolbar main activity
    private DrawerLayout drawerLayout;// drawer layout
    private NavigationView navigationView;
    private FrameLayout frameLayout; // main frame content

    private HomeFragment homeFragment;
    private ForumFragment forumFragment;

    private Menu menu; // main activity option menu
    private int menuRes = R.menu.menu_home;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initToolbar();
        initDrawer();
        initFragment();
        loadContentFragment(HOME_TAG);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();

        myRef.child("hoten2").child("abc").setValue("Hello");
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initFragment() {
        homeFragment = new HomeFragment(this);
        forumFragment = new ForumFragment(this);
    }

    private void initDrawer() {
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
                menuRes = R.menu.menu_home;
                onCreateOptionsMenu(menu);
                loadContentFragment(HOME_TAG);
                Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show();
                break;
            case R.id.forum:
                menuRes = R.menu.menu_forum;
                onCreateOptionsMenu(menu);
                loadContentFragment(FORUM_TAG);
                Toast.makeText(this, "Forum", Toast.LENGTH_SHORT).show();

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
                MenuItem logout = menu.findItem(R.id.action_logout);
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
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                break;
            case R.id.action_logout:

                break;
        }
        return false;
    }
}
