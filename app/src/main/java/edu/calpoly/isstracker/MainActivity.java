package edu.calpoly.isstracker;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.support.design.internal.NavigationMenuView;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

public class MainActivity extends AppCompatActivity {

    private boolean tablet;

    private Toolbar toolbar;
    private IssDataFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tablet = getResources().getBoolean(R.bool.tablet);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        initDataFragment();

        initLeftNavDrawer();
    }

    public void initDataFragment() {
        //know if device has NavigationBar
        int id = getResources().getIdentifier("config_showNavigationBar", "bool", "android");
        boolean navBar = id > 0 && getResources().getBoolean(id);
        boolean landscape = getResources().getBoolean(R.bool.landscape);

        fragment = new IssDataFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if (!tablet) {
            transaction.replace(R.id.bottom_sheet_container, fragment).commit();

            View bottomSheet = findViewById(R.id.bottom_sheet_container);
            final BottomSheetBehavior behavior = BottomSheetBehavior.from(bottomSheet);
            int peekHeight = (int) (getResources().getDimension(R.dimen.bottom_sheet_header_height)
                    + (navBar && !landscape ? getResources().getDimension(R.dimen.nav_bar_height) : 0));
            behavior.setPeekHeight(peekHeight);
            behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                @Override
                public void onStateChanged(@NonNull View bottomSheet, int newState) {

                }

                @Override
                public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                    //animate bottom sheet
                    fragment.onSlide(slideOffset);

                    //scale and move simulation upwards
                    /*simulation.setTranslationY(-slideOffset * simulation.getTop() * 0.7f);
                    simulation.setScaleX(1.0f - 0.3f * slideOffset);
                    simulation.setScaleY(1.0f - 0.3f * slideOffset);*/
                }
            });
        } else {
            transaction.replace(R.id.nav_view_right_container, fragment).commit();

            View bottomSheet = findViewById(R.id.bottom_sheet_container);
            BottomSheetBehavior behavior = BottomSheetBehavior.from(bottomSheet);
            behavior.setHideable(true);
            behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }

    public void initLeftNavDrawer() {
        final DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
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
                                break;
                            case R.id.map:
                                startActivity(new Intent(MainActivity.this, IssMapActivity.class));
                                break;
                            case R.id.stream:
                                startActivity(new Intent(MainActivity.this, IssStreamActivity.class));
                                break;
                            case R.id.about:
                                Toast.makeText(MainActivity.this,
                                        getString(R.string.about_nav_drawer),
                                        Toast.LENGTH_SHORT).show();
                                break;
                        }
                        return true;
                    }
                });

        if (!tablet) {
            //lock right drawer if device is a phone
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED,
                    findViewById(R.id.nav_view_right_container));
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && getResources().getBoolean(R.bool.landscape)) {
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        //color info icon
        menu.getItem(0).getIcon().setColorFilter(ContextCompat.getColor(this, R.color.text_color_secondary), PorterDuff.Mode.SRC_IN);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.cardboard:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
