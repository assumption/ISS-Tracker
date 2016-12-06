package edu.calpoly.isstracker;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Locale;

import edu.calpoly.isstracker.IssData.AsyncTaskCallback;
import edu.calpoly.isstracker.IssData.IssData;
import edu.calpoly.isstracker.IssData.Pojos.IssPosition;

public class IssMapActivity extends DrawerActivity {

    private GoogleMap mMap;
    private IssData issData;

    private Marker issMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inflateContentAndInitNavDrawer(R.layout.activity_iss_map);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                //for the info window
                mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
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

                        TextView altitude = new TextView(IssMapActivity.this);
                        altitude.setTextColor(Color.GRAY);
                        altitude.setText("Altitude: " + String.format(Locale.getDefault(), "%.2f", issData.getPosition().getAltitude()) + " km");

                        TextView velocity = new TextView(IssMapActivity.this);
                        velocity.setTextColor(Color.GRAY);
                        velocity.setText("Velocity: " + String.format(Locale.getDefault(), "%.2f", issData.getPosition().getVelocity()) + " km/h");

                        info.addView(title);
                        info.addView(altitude);
                        info.addView(velocity);

                        return info;
                    }
                });
            }
        });

        issData = new IssData();
        listenToIssPosition();
    }

    public void listenToIssPosition(){
        issData.listenToPositionRefreshing(new AsyncTaskCallback() {
            @Override
            public void done(IssData issData) {
                IssPosition position = issData.getPosition();
                setPosition(position.getLatitude(), position.getLongitude());
            }
        });
    }

    public void setPosition(double latitude, double longitude) {
        if (mMap != null) {
            LatLng pos = new LatLng(latitude, longitude);
            if (issMarker == null) {
                Bitmap.Config conf = Bitmap.Config.ARGB_8888;
                Bitmap bmp = Bitmap.createBitmap(150, 150, conf);
                Canvas canvas = new Canvas(bmp);

                Paint color = new Paint();
                color.setTextSize(30);
                color.setColor(Color.WHITE);

                BitmapFactory.Options opt = new BitmapFactory.Options();
                opt.inMutable = true;

                Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.iss_icon_for_map, opt);
                Bitmap resized = Bitmap.createScaledBitmap(imageBitmap, 130, 130, true);
                canvas.drawBitmap(resized, 10, 10, color);

                issMarker = mMap.addMarker(new MarkerOptions()
                        .position(pos)
                        .title("ISS")
                        .icon(BitmapDescriptorFactory.fromBitmap(bmp))
                        .infoWindowAnchor(0.5f, 0.5f)
                        .anchor(0.5f, 0.5f));

                mMap.moveCamera(CameraUpdateFactory.newLatLng(pos));
            } else {
                issMarker.setPosition(pos);
            }
            issMarker.showInfoWindow();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        issData.startRefreshingPosition();
        getNavigationView().setCheckedItem(R.id.map_activity);
    }

    @Override
    protected void onPause() {
        super.onPause();
        issData.stopRefreshingPosition();
    }
}