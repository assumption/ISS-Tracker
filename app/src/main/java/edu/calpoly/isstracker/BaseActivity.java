package edu.calpoly.isstracker;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.internal.NavigationMenuView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

public abstract class BaseActivity extends AppCompatActivity {

    private Toolbar toolbar;

    public void initLeftNavDrawer() {
        if(toolbar == null){
            return;
        }

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
                                startActivity(new Intent(BaseActivity.this, IssMapActivity.class));
                                break;
                            case R.id.stream:
                                startActivity(new Intent(BaseActivity.this, IssStreamActivity.class));
                                break;
                            case R.id.about:
                                Toast.makeText(BaseActivity.this,
                                        getString(R.string.about_nav_drawer),
                                        Toast.LENGTH_SHORT).show();
                                break;
                        }
                        return true;
                    }
                });
    }

    public void setToolbar(Toolbar toolbar){
        this.toolbar = toolbar;
        setSupportActionBar(toolbar);
    }
}
