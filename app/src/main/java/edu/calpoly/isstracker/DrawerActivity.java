package edu.calpoly.isstracker;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.internal.NavigationMenuView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

public abstract class DrawerActivity extends AppCompatActivity {

    public static final String DRAWER_OPEN = "DRAWER_OPEN";
    public static final int HOME_ACTIVITY = 1;
    public static final int MAP_ACTIVITY = 2;
    public static final int STREAM_ACTIVITY = 3;
    public static int currentActivity = -1;

    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_activity_layout);
    }

    @Override
    protected void onPostResume() {
        super.onResume();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getDrawerLayout().closeDrawers();
            }
        }, 100);
    }

    public void inflateContentAndInitNavDrawer(int layoutId) {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        //inflate content
        View content = LayoutInflater.from(this).inflate(layoutId, mDrawerLayout, false);
        mDrawerLayout.addView(content, 0);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar,
                R.string.drawer_open, R.string.drawer_close);
        mDrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        mDrawerToggle.syncState();

        NavigationView mNavigationView = (NavigationView) findViewById(R.id.nav_view_left);
        NavigationMenuView navigationMenuView = (NavigationMenuView) mNavigationView.getChildAt(0);
        if (navigationMenuView != null) {
            navigationMenuView.setVerticalScrollBarEnabled(false);
        }

        switch (currentActivity) {
            case HOME_ACTIVITY:
                mNavigationView.setCheckedItem(R.id.home);
                break;
            case MAP_ACTIVITY:
                mNavigationView.setCheckedItem(R.id.map);
                break;
            case STREAM_ACTIVITY:
                mNavigationView.setCheckedItem(R.id.stream);
                break;
        }

        ImageView headerImage = (ImageView) mNavigationView.getHeaderView(0).findViewById(R.id.header_image_view);
        Glide.with(this)
                .load("https://pixabay.com/static/uploads/photo/2011/12/14/12/11/astronaut-11080_1280.jpg")
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .override(1280, 800)
                .centerCrop()
                .into(headerImage);

        mNavigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case android.R.id.home:
                                mDrawerLayout.openDrawer(GravityCompat.START);
                                break;
                            case R.id.home:
                                if (currentActivity != HOME_ACTIVITY) {
                                    startActivity(new Intent(DrawerActivity.this, MainActivity.class)
                                            .putExtra(DRAWER_OPEN, true)
                                            .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                    overridePendingTransition(0, 0);
                                    currentActivity = HOME_ACTIVITY;
                                }
                                break;
                            case R.id.map:
                                if (currentActivity != MAP_ACTIVITY) {
                                    startActivity(new Intent(DrawerActivity.this, IssMapActivity.class)
                                            .putExtra(DRAWER_OPEN, true)
                                            .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                    overridePendingTransition(0, 0);
                                    currentActivity = MAP_ACTIVITY;
                                }
                                break;
                            case R.id.stream:
                                if (currentActivity != STREAM_ACTIVITY) {
                                    startActivity(new Intent(DrawerActivity.this, IssStreamActivity.class)
                                            .putExtra(DRAWER_OPEN, true)
                                            .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                    overridePendingTransition(0, 0);
                                    currentActivity = STREAM_ACTIVITY;
                                }
                                break;
                            case R.id.about:
                                startActivity(new Intent(DrawerActivity.this, AboutActivity.class));
                                getDrawerLayout().closeDrawers();
                                break;
                        }
                        return true;
                    }
                });

        if (getIntent().getBooleanExtra(DRAWER_OPEN, false)) {
            mDrawerLayout.openDrawer(mNavigationView);
            getIntent().putExtra(DRAWER_OPEN, false);
        }

        if (layoutId == R.layout.activity_main && getResources().getBoolean(R.bool.phone)) {
            //lock right drawer if device is a phone
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED,
                    findViewById(R.id.nav_view_right_container));
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && (getResources().getBoolean(R.bool.landscape)
                || getResources().getBoolean(R.bool.tablet))) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN
                    | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, 0);
    }

    public DrawerLayout getDrawerLayout() {
        return mDrawerLayout;
    }
}
