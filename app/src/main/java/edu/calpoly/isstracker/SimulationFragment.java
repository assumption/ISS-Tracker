package edu.calpoly.isstracker;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.badlogic.gdx.backends.android.AndroidFragmentApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import edu.calpoly.isstracker.IssData.ISSMath;

public class SimulationFragment extends AndroidFragmentApplication {

    private static final long REQUEST_INTERVAL = 2000;
    private static final String API_URL = "https://api.wheretheiss.at/v1/satellites/25544";

    private ScheduledExecutorService ses;

    public Simulation simulation;
    public Runnable issApiRequest;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        issApiRequest = new Runnable() {
            @Override
            public void run() {
                URL url = null;
                try {
                    url = new URL(API_URL);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                HttpURLConnection urlConnection = null;
                try {
                    urlConnection = (HttpURLConnection) url.openConnection();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder sb = new StringBuilder();

                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    String result = sb.toString();

                    JSONObject json = new JSONObject(result);

                    float latitude = 0;
                    float longitude = 0;
                    float altitude = 0;
                    try {
                        latitude = Float.valueOf(json.getString("latitude"));
                        longitude = Float.valueOf(json.getString("longitude"));
                        altitude = Float.valueOf(json.getString("altitude"));

                        simulation.issPosition.x = latitude;
                        simulation.issPosition.y = longitude;
                        simulation.issPosition.z = altitude;

                        ISSMath.convertToXyz(simulation.issPosition);
                        simulation.cam.position.set(simulation.issPosition.nor().scl(ISSMath.EARTH_R * 1.7f));
                        simulation.cam.lookAt(0f, 0f, 0f);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    urlConnection.disconnect();
                }
            }
        };

        simulation = new Simulation();
        return initializeForView(simulation);
    }

    @Override
    public void onResume() {
        super.onResume();

        ses = Executors.newSingleThreadScheduledExecutor();
        ses.scheduleAtFixedRate(issApiRequest, 0, REQUEST_INTERVAL, TimeUnit.MILLISECONDS);
    }

    @Override
    public void onPause() {
        super.onPause();

        ses.shutdown();
    }

    @Override
    public void exit() {

    }
}
