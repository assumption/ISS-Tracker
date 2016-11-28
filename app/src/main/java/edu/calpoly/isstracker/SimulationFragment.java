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

    private ScheduledExecutorService ses;

    public Simulation simulation;
    public Runnable issApiRequest;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        simulation = new Simulation();

        return initializeForView(simulation);
    }

    @Override
    public void exit() {

    }
}
