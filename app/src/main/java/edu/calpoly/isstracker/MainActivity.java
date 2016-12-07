package edu.calpoly.isstracker;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.badlogic.gdx.backends.android.AndroidFragmentApplication;

public class MainActivity extends DrawerActivity implements AndroidFragmentApplication.Callbacks {

    private boolean tablet;

    private IssDataFragment fragment;
    private SimulationFragment simFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inflateContent(R.layout.activity_main);

        tablet = getResources().getBoolean(R.bool.tablet);

        initSimulation();

        initDataFragment();

        initNavDrawer();
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
                    simFragment.getSimulation().onSlide(slideOffset);
                }
            });

            fragment.setCallback(new IssDataFragment.BottomSheetCallback() {
                @Override
                public void onClick() {
                    behavior.setState(behavior.getState() == BottomSheetBehavior.STATE_COLLAPSED ?
                            BottomSheetBehavior.STATE_EXPANDED : BottomSheetBehavior.STATE_COLLAPSED);
                }
            });

            lockRightDrawer();
        } else {
            transaction.replace(R.id.nav_view_right_container, fragment).commit();

            View bottomSheet = findViewById(R.id.bottom_sheet_container);
            BottomSheetBehavior behavior = BottomSheetBehavior.from(bottomSheet);
            behavior.setHideable(true);
            behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }

    public void initSimulation() {
        Simulation simulation = (Simulation) getLastCustomNonConfigurationInstance();
        simFragment = SimulationFragment.getInstance(simulation);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.simulation_view, simFragment)
                .commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getNavigationView().setCheckedItem(R.id.home_activity);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        //color cardboard icon
        menu.getItem(0).getIcon().setColorFilter(ContextCompat.getColor(this,
                R.color.text_color_secondary), PorterDuff.Mode.SRC_IN);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.cardboard:
                startActivity(new Intent(MainActivity.this,
                        SimulationVR.class));
                break;
            case R.id.open_right_drawer:
                getDrawerLayout().openDrawer(GravityCompat.END);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return simFragment.getSimulation();
    }

    @Override
    protected void onStop() {
        super.onStop();
        simFragment.onStop();
    }

    @Override
    public void exit() {

    }
}
