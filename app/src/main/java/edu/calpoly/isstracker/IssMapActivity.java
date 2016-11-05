package edu.calpoly.isstracker;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import edu.calpoly.isstracker.IssData.AsyncTaskCallback;
import edu.calpoly.isstracker.IssData.IssData;
import edu.calpoly.isstracker.IssData.IssDataAsyncRequest;
import edu.calpoly.isstracker.IssData.Pojos.IssPosition;
import isstracker.calpoly.edu.iss_tracker.R;

public class IssMapActivity extends AppCompatActivity {

    private GoogleMap mMap;
    private IssData issData;

    private Marker iss;

    private Snackbar snackbar;

    private Timer t;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iss_map);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        colorToolbarBackButton(toolbar, ContextCompat.getColor(this, R.color.text_color_secondary));

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                //for the info window
                /*mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

                    @Override
                    public View getInfoWindow(Marker arg0) {
                        return null;
                    }

                    @Override
                    public View getInfoContents(Marker marker) {
                        LinearLayout info = new LinearLayout(IssMapActivity.this);
                        info.setOrientation(LinearLayout.VERTICAL);

                        TextView title = new TextView(IssMapActivity.this);
                        title.setTextColor(Color.BLACK);
                        title.setTypeface(Typeface.DEFAULT_BOLD);
                        title.setGravity(Gravity.CENTER);
                        title.setText(marker.getTitle());

                        *//*TextView altitude = new TextView(IssMapActivity.this);
                        altitude.setTextColor(Color.GRAY);
                        altitude.setText("Altitude: " + String.format(Locale.getDefault(), "%.2f", issData.altitude) + " km");

                        TextView velocity = new TextView(IssMapActivity.this);
                        velocity.setTextColor(Color.GRAY);
                        velocity.setText("Velocity: " + String.format(Locale.getDefault(), "%.2f", issData.velocity) + " km/h");*//*

                        info.addView(title);
                        *//*info.addView(altitude);
                        info.addView(velocity);*//*

                        return info;
                    }
                });*/
            }
        });

        issData = new IssData();
    }

    public void refreshData() {
        this.snackbar = Snackbar.make(findViewById(R.id.root_view), "Loading...", Snackbar.LENGTH_INDEFINITE);
        snackbar.show();
        issData.refreshPosition(new AsyncTaskCallback() {
            @Override
            public void done(IssData issData) {
                IssPosition position = issData.getPosition();
                setPosition(position.getLatitude(), position.getLongitude());
                snackbar.dismiss();
            }

            @Override
            public void timeoutError() {
                Toast.makeText(IssMapActivity.this, "SocketTimeoutException", Toast.LENGTH_SHORT).show();
                snackbar.dismiss();
            }
        });
    }

    public void setPosition(double latitude, double longitude) {
        if (mMap != null) {
            LatLng pos = new LatLng(latitude, longitude);
            if (iss == null) {
                iss = mMap.addMarker(new MarkerOptions()
                        .position(pos)
                        .title("ISS"));
            } else {
                iss.setPosition(pos);
            }
            mMap.moveCamera(CameraUpdateFactory.newLatLng(pos));
            iss.showInfoWindow();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //refresh Iss position every 10 seconds
        t = new Timer();
        t.scheduleAtFixedRate(
                new TimerTask() {
                    @Override
                    public void run() {
                        refreshData();

                    }
                }, 0, 10 *1000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(t != null){
            t.cancel();
            t.purge();
            t = null;
        }

        if (snackbar != null) {
            snackbar.dismiss();
        }

        iss = null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                try {
                    onBackPressed();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void colorToolbarBackButton(Toolbar toolbar, int color) {
        //back button
        for (int i = 0; i < toolbar.getChildCount(); i++) {
            if (toolbar.getChildAt(i) instanceof ImageView) {
                ImageView drawerIcon = (ImageView) toolbar.getChildAt(i);
                drawerIcon.setColorFilter(color, PorterDuff.Mode.SRC_IN);
            }
        }
    }

}