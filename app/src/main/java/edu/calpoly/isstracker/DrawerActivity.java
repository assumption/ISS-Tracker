package edu.calpoly.isstracker;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.internal.NavigationMenuView;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

public abstract class DrawerActivity extends AppCompatActivity {

    public static final String OPEN_DRAWER = "OPEN_DRAWER";

    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_activity_layout);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(getIntent().getBooleanExtra(OPEN_DRAWER, false)){
            overridePendingTransition(0, 0);
            getDrawerLayout().getViewTreeObserver().addOnGlobalLayoutListener(
                    new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            getDrawerLayout().getViewTreeObserver()
                                    .removeOnGlobalLayoutListener(this);
                            getDrawerLayout().closeDrawers();
                        }
                    });
            getIntent().putExtra(OPEN_DRAWER, false);
        }

        if(!isNetworkAvailable()){
            new AlertDialog.Builder(this)
                    .setTitle("No Network Connection")
                    .setMessage("ISS Tracker is unable to refresh the ISS position.")
                    .setPositiveButton("OK", null)
                    .setNegativeButton("Close App", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            DrawerActivity.this.onBackPressed();
                        }
                    })
                    .create().show();
        }
    }

    public void inflateContent(int layoutId){
        mDrawerLayout = findViewById(R.id.drawer_layout);

        //inflate content
        View content = LayoutInflater.from(this).inflate(layoutId, mDrawerLayout, false);
        mDrawerLayout.addView(content, 0);
    }

    public void initNavDrawer(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle mDrawerToggle =
                new ActionBarDrawerToggle(this, mDrawerLayout, toolbar,
                        R.string.drawer_open, R.string.drawer_close);
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        mNavigationView = findViewById(R.id.nav_view_left);
        NavigationMenuView navigationMenuView = (NavigationMenuView) mNavigationView.getChildAt(0);
        if (navigationMenuView != null) {
            navigationMenuView.setVerticalScrollBarEnabled(false);
        }

        ImageView headerImage = mNavigationView.getHeaderView(0).findViewById(R.id.header_image_view);

        RequestOptions options = new RequestOptions()
                .override(1280, 800)
                .centerCrop();

        Glide.with(this)
                .load("https://pixabay.com/static/uploads/photo/2011/12/14/12/11/astronaut-11080_1280.jpg")
                .apply(options)
                .into(headerImage);

        mNavigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                        //Issue 63570, Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                        switch (menuItem.getItemId()) {
                            case R.id.home_activity:
                                startActivity(new Intent(DrawerActivity.this, MainActivity.class)
                                        .putExtra(OPEN_DRAWER, true)
                                        .setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                overridePendingTransition(0, 0);
                                break;
                            case R.id.map_activity:
                                startActivity(new Intent(DrawerActivity.this, IssMapActivity.class)
                                        .putExtra(OPEN_DRAWER, true)
                                        .setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                overridePendingTransition(0, 0);
                                break;
                            case R.id.stream_activity:
                                startActivity(new Intent(DrawerActivity.this, IssStreamActivity.class)
                                        .putExtra(OPEN_DRAWER, true)
                                        .setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                overridePendingTransition(0, 0);
                                break;
                            case R.id.about_activity:
                                startActivity(new Intent(DrawerActivity.this, AboutActivity.class));
                                getDrawerLayout().closeDrawers();
                                break;
                        }
                        return true;
                    }
                });

        if (getIntent().getBooleanExtra(OPEN_DRAWER, false)) {
            mDrawerLayout.openDrawer(mNavigationView);
        }
    }

    public View getLayout(){
        return getDrawerLayout().getChildAt(0);
    }

    public void lockRightDrawer(){
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED,
                findViewById(R.id.nav_view_right_container));
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void inflateContentAndInitNavDrawer(int layoutId) {
        inflateContent(layoutId);
        initNavDrawer();
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
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }

    public DrawerLayout getDrawerLayout() {
        return mDrawerLayout;
    }

    public NavigationView getNavigationView(){
        return mNavigationView;
    }
}
